package me.sosedik.trappednewbie.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.ItemModifierContext;
import me.sosedik.kiterino.modifier.item.context.ItemModifierContextType;
import me.sosedik.kiterino.modifier.item.context.SlottedItemModifierContext;
import me.sosedik.kiterino.modifier.item.context.packet.BaseItemContext;
import me.sosedik.kiterino.modifier.item.context.packet.EntityEquipmentPacketContext;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.InventoryUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
public class VisualArmorModifier extends ItemModifier {

	public static final ItemModifierContextType VISUAL_ARMOR = ItemModifierContextType.context(BaseItemContext.class).withName().withLore().build();
	private static final NamespacedKey SADDLE_HEAD_MODEL = TrappedNewbie.trappedNewbieKey("saddle_head");

	public VisualArmorModifier(NamespacedKey key) {
		super(key);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		Player target;
		if (contextBox.getContext() instanceof EntityEquipmentPacketContext ctx) {
			if (!(ctx.getEntity() instanceof Player other)) return ModificationResult.PASS;
			target = other;
		} else {
			target = contextBox.getViewer();
		}
		if (target == null) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		int slot = resolveSlot(target, contextBox.getContext());
		if (!isArmorSlot(target, slot)) return ModificationResult.PASS;

		var messenger = Messenger.messenger(player);
		EquipmentSlot equipmentSlot = InventoryUtil.getBySlot(slot);
		var visualArmor = VisualArmor.of(target);
		if (!visualArmor.canUseVisualArmor()) return ModificationResult.PASS;

		if (visualArmor.isArmorPreview()) {
			if (equipmentSlot == EquipmentSlot.OFF_HAND) {
				contextBox.setItem(
					visualArmor.hasGloves()
						? parseItem(contextBox.getContext(), player, contextBox.getLocale(), visualArmor.getGloves().clone())
						: getEmpty(contextBox.getContext(), messenger, contextBox.getLocale(), player, target, EquipmentSlot.OFF_HAND, true)
				);
				return ModificationResult.RETURN;
			}

			ItemStack item = target.getInventory().getItem(equipmentSlot); // This item might've been sent by a plugin's packet, so we get the real item just in case
			if (ItemStack.isEmpty(item) || item.getType() == TrappedNewbieItems.MATERIAL_AIR) {
				ItemStack fakedEmptyItem = getEmpty(contextBox.getContext(), messenger, contextBox.getLocale(), player, target, equipmentSlot, true);
				contextBox.setItem(applyLore(messenger, fakedEmptyItem, equipmentSlot, true, false));
				return ModificationResult.RETURN;
			}

			ItemStack parsedArmorItem = parseItem(contextBox.getContext(), player, contextBox.getLocale(), item.clone());
			contextBox.setItem(applyLore(messenger, parsedArmorItem, equipmentSlot, true, true));
			return ModificationResult.RETURN;
		}

		if (!equipmentSlot.isArmor())
			return ModificationResult.PASS;

		if (visualArmor.hasItem(equipmentSlot)) {
			ItemStack visualItem = visualArmor.getItem(equipmentSlot);

			// Show elytras when gliding
			if (equipmentSlot == EquipmentSlot.CHEST && player.isGliding() && visualItem.getType() != Material.ELYTRA) {
				ItemStack underwear = player.getInventory().getItem(slot);
				if (underwear.getType() == Material.ELYTRA) {
					ItemStack parseUnderwear = parseUnderwear(contextBox.getContext(), messenger, player, contextBox.getLocale(), underwear.clone());
					contextBox.setItem(applyLore(messenger, parseUnderwear, equipmentSlot, false, false));
					return ModificationResult.RETURN;
				}
			}

			visualItem = visualItem.clone();
			visualItem.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
			ItemStack parsedVisualItem = parseItem(contextBox.getContext(), player, contextBox.getLocale(), visualItem);
			contextBox.setItem(applyLore(messenger, parsedVisualItem, equipmentSlot, false, true));
			return ModificationResult.RETURN;
		}

		// Not wearing visual, show underwear
		ItemStack underwear = target.getInventory().getItem(equipmentSlot);
		if (!ItemStack.isEmpty(underwear)) {
			ItemStack parsedUnderwear = parseUnderwear(contextBox.getContext(), messenger, player, contextBox.getLocale(), underwear.clone());
			contextBox.setItem(parsedUnderwear);
			return ModificationResult.RETURN;
		}

		ItemStack empty = getEmpty(contextBox.getContext(), messenger, contextBox.getLocale(), player, target, equipmentSlot, false);
		contextBox.setItem(applyLore(messenger, empty, equipmentSlot, false, false));

		return ModificationResult.RETURN;
	}

	@Override
	public boolean skipAir() {
		return false;
	}

	@Override
	public boolean skipContext(ItemModifierContext context) {
		return context.getContextType() == VISUAL_ARMOR;
	}

	private ItemStack parseItem(ItemModifierContext context, Player player, Locale locale, ItemStack item) {
		ItemStack newItem = modifyItem(new ItemContextBox(player, locale, new BaseItemContext(VISUAL_ARMOR, context), item));
		return newItem == null ? item : newItem;
	}

	private int resolveSlot(Player player, ItemModifierContext context) {
		if (context instanceof EntityEquipmentPacketContext ctx) return player.canUseEquipmentSlot(ctx.getSlot()) ? InventoryUtil.getSlot(player, ctx.getSlot()) : -1;
		if (context instanceof SlottedItemModifierContext ctx) return ctx.getSlot();
		return -1;
	}

	private ItemStack getEmpty(ItemModifierContext context, Messenger messenger, Locale locale, Player player, Player target, EquipmentSlot slot, boolean armorPreview) {
		ItemStack outline = getOutline(messenger, slot, armorPreview);
		return parseItem(context, player, locale, outline);
	}

	private ItemStack getOutline(Messenger messenger, EquipmentSlot slot, boolean armorPreview) {
		ItemStack item = switch (slot) {
			case HEAD -> ItemStack.of(TrappedNewbieItems.HELMET_OUTLINE);
			case CHEST -> ItemStack.of(TrappedNewbieItems.CHESTPLATE_OUTLINE);
			case LEGS -> ItemStack.of(TrappedNewbieItems.LEGGINGS_OUTLINE);
			case FEET -> ItemStack.of(TrappedNewbieItems.BOOTS_OUTLINE);
			case OFF_HAND -> ItemStack.of(TrappedNewbieItems.GLOVES_OUTLINE);
			default -> throw new IllegalArgumentException("Unsupported equipment slot: " + slot.name());
		};
		String localeKey = "equipment." + slot.name().toLowerCase(Locale.ROOT) + "." + (armorPreview ? "armor" : "cosmetic");
		item.setData(DataComponentTypes.ITEM_NAME, messenger.getMessage(localeKey));
		return item;
	}

	private ItemStack applyLore(Messenger messenger, ItemStack item, EquipmentSlot slot, boolean armorPreview, boolean cosmetic) {
		// Visual armor
		ItemLore.Builder itemLore = ItemLore.lore();
		if (item.hasData(DataComponentTypes.LORE))
			itemLore.lines(item.getData(DataComponentTypes.LORE).lines());
		if (!armorPreview && cosmetic) itemLore.addLine(0, messenger.getMessage("equipment.cosmetic"));
		if (slot.isArmor()) {
			if (itemLore.getLines().size() > 1 || (armorPreview && itemLore.getLines().size() == 1)) itemLore.addLine(Component.empty());
			itemLore.addLine(messenger.getMessage("equipment.layer_switch." + (armorPreview ? "underwear" : "cosmetic")).applyFallbackStyle(Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.withState(false))));
		}
		item.setData(DataComponentTypes.LORE, itemLore);

		// Using custom model for saddles on head
		if (!armorPreview && slot == EquipmentSlot.HEAD && item.getType() == Material.SADDLE && !item.isDataOverridden(DataComponentTypes.ITEM_MODEL)) {
			item.setData(DataComponentTypes.ITEM_MODEL, SADDLE_HEAD_MODEL);
		}

		return item;
	}

	private static ItemStack parseUnderwear(ItemModifierContext context, Messenger messenger, Player player, Locale locale, ItemStack underwear) {
		ItemStack newUnderwear = modifyItem(new ItemContextBox(player, locale, new BaseItemContext(VISUAL_ARMOR, context), underwear));
		if (newUnderwear != null) underwear = newUnderwear;
		else underwear = underwear.clone();

		ItemLore.Builder itemLore = ItemLore.lore();
		if (underwear.hasData(DataComponentTypes.LORE))
			itemLore.lines(underwear.getData(DataComponentTypes.LORE).lines());
		itemLore.addLine(0, messenger.getMessage("equipment.underwear"));
		if (itemLore.getLines().size() > 1) itemLore.addLine(Component.empty());
		itemLore.addLine(messenger.getMessage("equipment.layer_switch.cosmetic").applyFallbackStyle(Style.style(NamedTextColor.GRAY, TextDecoration.ITALIC.withState(false))));
		underwear.setData(DataComponentTypes.LORE, itemLore);

		return underwear;
	}

	public static boolean isArmorSlot(Player player, int slot) {
		if (slot < 5) return false;
		if (slot > 8 && slot != InventorySlotHelper.OFF_HAND) return false;
		InventoryType inventoryType = player.getOpenInventory().getTopInventory().getType();
		return inventoryType == InventoryType.PLAYER || inventoryType == InventoryType.CRAFTING;
	}

}
