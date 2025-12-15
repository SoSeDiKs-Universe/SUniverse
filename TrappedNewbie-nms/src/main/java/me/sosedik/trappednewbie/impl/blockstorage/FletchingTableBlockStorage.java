package me.sosedik.trappednewbie.impl.blockstorage;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import me.sosedik.miscme.listener.item.ImmersiveDyes;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.resourcelib.impl.item.modifier.CustomNameModifier;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.item.tinker.ArrowData;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.impl.item.modifier.TippedArrowPotionTypeModifier;
import me.sosedik.trappednewbie.impl.recipe.FletchingCrafting;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.api.storage.block.ExtraDroppableBlockStorage;
import me.sosedik.utilizer.api.storage.block.InventoryBlockDataStorageHolder;
import me.sosedik.utilizer.util.InventoryUtil;
import me.sosedik.utilizer.util.RecipeManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.SlotElement;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@NullMarked
public class FletchingTableBlockStorage extends InventoryBlockDataStorageHolder implements ExtraDroppableBlockStorage {

	private static final Component DEFAULT_TITLE = Component.translatable(Material.FLETCHING_TABLE.translationKey());
	private static final FontData FLETCHING_TABLE_FONT = ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("menu/fletching_table"));
	private static final FontData[] FLETCHING_TABLE_POTION_OVERLAY_FONTS = new FontData[]{
		ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("menu/fletching_table_potion_overlay-7")),
		ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("menu/fletching_table_potion_overlay-6")),
		ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("menu/fletching_table_potion_overlay-5")),
		ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("menu/fletching_table_potion_overlay-4")),
		ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("menu/fletching_table_potion_overlay-3")),
		ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("menu/fletching_table_potion_overlay-2")),
		ResourceLib.requireFontData(TrappedNewbie.trappedNewbieKey("menu/fletching_table_potion_overlay-1")),
	};
	private static final String INFUSER_TAG = "infuser";
	private static final String INFUSIONS_TAG = "infusions";
	private static final int HEAD_SLOT = 1;
	private static final int STICK_SLOT = 10;
	private static final int FLETCHING_SLOT = 19;
	private static final int MODIFIER_SLOT = 2;
	private static final int RESULT_SLOT = 16;
	private static final int[] INFUSION_SLOTS = new int[]{13, 22};

	static {
		ImmersiveDyes.ExtraDyeRule dyeRule = (item, dye) -> {
			if (!item.hasData(DataComponentTypes.CUSTOM_MODEL_DATA)) {
				item = item.clone();
				if (!ImmersiveDyes.tryToDye(item, dye)) return null;

				ArrowData.defaultData(Material.ARROW).saveToCustomData(item, true);

				return item;
			}

			item = item.clone();
			if (!ImmersiveDyes.tryToDye(item, dye)) return null;

			return item;
		};
		ImmersiveDyes.addExtraDyeRule(Material.ARROW, dyeRule);
		ImmersiveDyes.addExtraDyeRule(RequiemItems.FIRE_ARROW, dyeRule);
	}

	private @Nullable ItemStack infuser;
	private @Nullable TextColor infusionColor;
	private int infusions;

	public FletchingTableBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
		this.inventory = Bukkit.createInventory(this, 9 * 3, DEFAULT_TITLE);
		if (nbt.hasTag(INVENTORY_STORAGE_TAG)) {
			ItemStack[] inv = nbt.getItemStackArray(INVENTORY_STORAGE_TAG);
			assert inv != null;
			this.inventory.setItem(HEAD_SLOT, inv[0]);
			this.inventory.setItem(STICK_SLOT, inv[1]);
			this.inventory.setItem(FLETCHING_SLOT, inv[2]);
			this.inventory.setItem(MODIFIER_SLOT, inv[3]);
		}
		this.infuser = nbt.hasTag(INFUSER_TAG) ? nbt.getItemStack(INFUSER_TAG) : null;
		this.infusions = this.infuser == null ? 0 : nbt.getOrDefault(INFUSIONS_TAG, 0);
		computeInfuserColor();
		checkResult();
	}

	@Override
	public void openInventory(Player player) {
		if (this.windows == null) this.windows = new HashMap<>();

		var inv = ReferencingInventory.fromStorageContents(this.inventory);
		var gui = Gui.of(9, 3, inv);
		var window = Window.builder()
			.addCloseHandler(reason -> this.windows.remove(player.getUniqueId()))
			.setTitle(name(player))
			.setUpperGui(gui)
			.setViewer(player)
			.build();
		window.addOpenHandler(() -> this.windows.put(player.getUniqueId(), window));

		inv.addPreUpdateHandler(event -> {
			switch (event.getSlot()) {
				case HEAD_SLOT -> {
					if (!isEmptyOr(event.getNewItem(), item -> TrappedNewbieTags.ARROW_HEAD_MATERIALS.isTagged(item.getType())))
						event.setCancelled(true);
				}
				case STICK_SLOT -> {
					if (!isEmptyOr(event.getNewItem(), item -> TrappedNewbieTags.ARROW_STICK_MATERIALS.isTagged(item.getType())))
						event.setCancelled(true);
				}
				case FLETCHING_SLOT -> {
					if (!isEmptyOr(event.getNewItem(), item -> TrappedNewbieTags.ARROW_FLETCHING_MATERIALS.isTagged(item.getType())))
						event.setCancelled(true);
				}
				case MODIFIER_SLOT -> {
					if (!isEmptyOr(event.getNewItem(), item -> TrappedNewbieTags.ARROW_INFUSION_MATERIALS.isTagged(item.getType())))
						event.setCancelled(true);
				}
				case RESULT_SLOT -> {
					if (!ItemStack.isEmpty(event.getNewItem()))
						event.setCancelled(true);
				}
				default -> event.setCancelled(true);
			}
		});
		inv.addPostUpdateHandler(event -> {
			switch (event.getSlot()) {
				case HEAD_SLOT, STICK_SLOT, FLETCHING_SLOT, MODIFIER_SLOT -> checkResult();
				case RESULT_SLOT -> {
					inv.changeItem(UpdateReason.SUPPRESSED, HEAD_SLOT, item -> {
						item = getInventory().getItem(HEAD_SLOT);
						if (item != null) item.subtract();
						return item;
					});
					inv.changeItem(UpdateReason.SUPPRESSED, STICK_SLOT, item -> {
						item = getInventory().getItem(STICK_SLOT);
						if (item != null) item.subtract();
						return item;
					});
					inv.changeItem(UpdateReason.SUPPRESSED, FLETCHING_SLOT, item -> {
						item = getInventory().getItem(FLETCHING_SLOT);
						if (item != null) item.subtract();
						return item;
					});
					inv.changeItem(UpdateReason.SUPPRESSED, MODIFIER_SLOT, item -> {
						if (ItemStack.isType(getInventory().getItem(RESULT_SLOT), Material.TIPPED_ARROW)) {
							this.infusions--;
							if (this.infusions <= 0) {
								this.infuser = null;
								this.infusionColor = null;
							}
							window.updateTitle();
						}

						item = getInventory().getItem(MODIFIER_SLOT);
						if (!ItemStack.isEmpty(item)) {
							Material type = item.getType();
							if (type == Material.GLASS_BOTTLE) {
								if (this.infusions > 0) {
									this.infusions--;
									window.updateTitle();
								}
								return item;
							}

							if (type == Material.POTION || type == Material.SPLASH_POTION || type == Material.LINGERING_POTION) {
								if (this.infusions > 0 && item.equals(this.infuser)) {
									this.infusions--;
									window.updateTitle();
									return item;
								}
								this.infusions = 7;
								this.infuser = item.asOne();

								computeInfuserColor();

								window.updateTitle();
								if (item.getAmount() == 1)
									return ItemStack.of(Material.GLASS_BOTTLE);
								else
									InventoryUtil.addOrDrop(player, ItemStack.of(Material.GLASS_BOTTLE), true);
							}

							item.subtract();
						}
						return item;
					});
					checkResult();
				}
				default -> {}
			}
		});

		gui.fill(Item.simple(ItemStack.of(TrappedNewbieItems.MATERIAL_AIR)));
		gui.setSlotElement(HEAD_SLOT, new SlotElement.InventoryLink(inv, HEAD_SLOT, emptyProvider(HEAD_SLOT, () -> {
			var item = ItemStack.of(Material.GLASS_PANE);
			item.setData(DataComponentTypes.ITEM_MODEL, ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("gui/head_outline")));
			return CustomNameModifier.named(item, "item." + TrappedNewbie.NAMESPACE + ".gui_arrow_head_outline.name");
		})));
		gui.setSlotElement(STICK_SLOT, new SlotElement.InventoryLink(inv, STICK_SLOT, emptyProvider(STICK_SLOT, () -> {
			var item = ItemStack.of(Material.GLASS_PANE);
			item.setData(DataComponentTypes.ITEM_MODEL, ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("gui/stick_outline")));
			return CustomNameModifier.named(item, "item." + TrappedNewbie.NAMESPACE + ".gui_arrow_stick_outline.name");
		})));
		gui.setSlotElement(FLETCHING_SLOT, new SlotElement.InventoryLink(inv, FLETCHING_SLOT, emptyProvider(FLETCHING_SLOT, () -> {
			var item = ItemStack.of(Material.GLASS_PANE);
			item.setData(DataComponentTypes.ITEM_MODEL, ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("gui/feather_outline")));
			return CustomNameModifier.named(item, "item." + TrappedNewbie.NAMESPACE + ".gui_arrow_fletching_outline.name");
		})));
		gui.setSlotElement(MODIFIER_SLOT, new SlotElement.InventoryLink(inv, MODIFIER_SLOT, emptyProvider(MODIFIER_SLOT, () -> {
			var item = ItemStack.of(Material.GLASS_PANE);
			item.setData(DataComponentTypes.ITEM_MODEL, ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("gui/modifiers_outline")));
			return CustomNameModifier.named(item, "item." + TrappedNewbie.NAMESPACE + ".gui_arrow_modifiers_outline.name");
		})));
		gui.setSlotElement(RESULT_SLOT, new SlotElement.InventoryLink(inv, RESULT_SLOT, emptyProvider(RESULT_SLOT, () -> ItemStack.of(TrappedNewbieItems.MATERIAL_AIR))));
		for (int infusionSlot : INFUSION_SLOTS) {
			gui.setSlotElement(infusionSlot, new SlotElement.InventoryLink(inv, infusionSlot, emptyProvider(infusionSlot, () -> {
				if (ItemStack.isEmpty(this.infuser) || this.infusions == 0)
					return ItemStack.of(TrappedNewbieItems.MATERIAL_AIR);

				var item = ItemStack.of(Material.GLASS_PANE);
				PotionContents data = this.infuser.getData(DataComponentTypes.POTION_CONTENTS);
				assert data != null;
				item.setData(DataComponentTypes.POTION_CONTENTS, data);
				item.setData(DataComponentTypes.ITEM_MODEL, ResourceLib.storage().getItemModelMapping(TrappedNewbie.trappedNewbieKey("material_air")));
				return CustomNameModifier.named(item, Component.textOfChildren(this.infuser.effectiveName(), Component.text(" (" + this.infusions + "/7)")));
			})));
		}

		window.open();
	}

	private void checkResult() {
		ItemStack head = getInventory().getItem(HEAD_SLOT);
		ItemStack stick = getInventory().getItem(STICK_SLOT);
		ItemStack fletching = getInventory().getItem(FLETCHING_SLOT);
		ItemStack modifier = getInventory().getItem(MODIFIER_SLOT);
		if (modifier != null && modifier.isEmpty()) modifier = null;

		ItemStack result = RecipeManager.getResult(FletchingCrafting.class, new @Nullable ItemStack[]{
			head,
			stick,
			fletching,
			modifier
		});

		if (ItemStack.isEmpty(result)) {
			boolean noModifier = ItemStack.isEmpty(modifier) || modifier.getType() == Material.GLASS_BOTTLE;
			if (noModifier && !ItemStack.isEmpty(head) && !ItemStack.isEmpty(stick) && !ItemStack.isEmpty(fletching)) {
				result = ItemStack.of(Material.ARROW);
			} else {
				getInventory().setItem(RESULT_SLOT, null);
				return;
			}
		}

		assert head != null;
		assert stick != null;
		assert fletching != null;

		if (result.getType() == Material.SPECTRAL_ARROW) {
			getInventory().setItem(RESULT_SLOT, result);
			return;
		}

		if (result.getType() == Material.TIPPED_ARROW) {
			if (!ItemStack.isEmpty(this.infuser) && this.infuser.hasData(DataComponentTypes.POTION_CONTENTS) && this.infusions > 0) {
				if ((modifier != null && modifier.getType() == Material.GLASS_BOTTLE) || this.infuser.equals(modifier)) {
					PotionContents data = this.infuser.getData(DataComponentTypes.POTION_CONTENTS);
					assert data != null;
					result.setData(DataComponentTypes.POTION_CONTENTS, data);
					preservePotionType(result, this.infuser);
				} else if (modifier != null) {
					if (!modifier.hasData(DataComponentTypes.POTION_CONTENTS)) {
						getInventory().setItem(RESULT_SLOT, null);
						return;
					}
					PotionContents data = modifier.getData(DataComponentTypes.POTION_CONTENTS);
					assert data != null;
					result.setData(DataComponentTypes.POTION_CONTENTS, data);
					preservePotionType(result, modifier);
				}
			} else if (modifier != null && modifier.getType() == Material.GLASS_BOTTLE) {
				result = ItemStack.of(Material.ARROW);
			}
		}

		new ArrowData(result.getType(), head.getType(), stick.getType(), fletching.getType(), modifier == null ? null : modifier.getType()).saveToCustomData(result, true);

		getInventory().setItem(RESULT_SLOT, result);
	}

	private void preservePotionType(ItemStack result, ItemStack potion) {
		if (result.getType() == Material.TIPPED_ARROW) {
			if (potion.getType() == Material.SPLASH_POTION)
				TippedArrowPotionTypeModifier.ArrowPotionType.SPLASH.saveTo(result);
			else if (potion.getType() == Material.LINGERING_POTION)
				TippedArrowPotionTypeModifier.ArrowPotionType.LINGERING.saveTo(result);
		}
	}

	private void computeInfuserColor() {
		if (this.infuser != null && this.infuser.hasData(DataComponentTypes.POTION_CONTENTS)) {
			PotionContents data = this.infuser.getData(DataComponentTypes.POTION_CONTENTS);
			assert data != null;
			this.infusionColor = data.computeEffectiveColor();
		}
	}

	@Override
	public boolean dropOnExplosion() {
		return true;
	}

	@Override
	public List<ItemStack> getExtraDrops(Event event) {
		List<ItemStack> drops = new ArrayList<>();
		for (ItemStack item : getInventory().getStorageContents()) {
			if (!ItemStack.isEmpty(item))
				drops.add(item);
		}
		return drops;
	}

	@Override
	public Component getDefaultName(Player player) {
		return Component.textOfChildren(
			Mini.asIcon(FLETCHING_TABLE_FONT.offsetMapping(-8)),
			this.infusions > 0 ? FLETCHING_TABLE_POTION_OVERLAY_FONTS[this.infusions - 1].offsetMapping(71).color(this.infusionColor) : Component.empty(),
			DEFAULT_TITLE
		);
	}

	@Override
	public ReadWriteNBT save() {
		ReadWriteNBT nbt = super.save();
		nbt.setItemStackArray(INVENTORY_STORAGE_TAG, new @Nullable ItemStack[]{
			getInventory().getItem(HEAD_SLOT),
			getInventory().getItem(STICK_SLOT),
			getInventory().getItem(FLETCHING_SLOT),
			getInventory().getItem(MODIFIER_SLOT)
		});
		if (!ItemStack.isEmpty(this.infuser)) {
			nbt.setItemStack(INFUSER_TAG, this.infuser);
			nbt.setInteger(INFUSIONS_TAG, this.infusions);
		}
		return nbt;
	}

}
