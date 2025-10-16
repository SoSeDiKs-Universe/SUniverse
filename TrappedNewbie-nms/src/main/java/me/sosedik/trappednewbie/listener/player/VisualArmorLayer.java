package me.sosedik.trappednewbie.listener.player;

import com.destroystokyo.paper.MaterialTags;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import me.sosedik.requiem.api.event.player.PlayerTombstoneCreateEvent;
import me.sosedik.requiem.api.event.player.TombstoneDestroyEvent;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.InventoryUtil;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Players have a visual second armor layer
 */
// MCCheck: 1.21.10, new equipable items (& equip sounds)
@NullMarked
public class VisualArmorLayer implements Listener {

	private static final String ARMOR_BUNDLE_TAG = "visual_armor";
	private static final String HELMET_TAG = "helmet";
	private static final String CHESTPLATE_TAG = "chestplate";
	private static final String LEGGINGS_TAG = "leggings";
	private static final String BOOTS_TAG = "boots";
	private static final String GLOVES_TAG = "gloves";
	private static final TypedKey<EntityType> PLAYER_TYPED_KEY = TypedKey.create(RegistryKey.ENTITY_TYPE, EntityType.PLAYER.getKey());

	private static final Map<UUID, VisualArmor> ARMOR_BUNDLES = new HashMap<>();

	public VisualArmorLayer() {
		EntityUtil.addDarknessExemptRule(entity -> {
			if (!(entity instanceof Player player)) return false;

			VisualArmor visualArmor = getVisualArmor(player);
			return visualArmor.hasHelmet() && ItemUtil.isLightSource(visualArmor.getHelmet());
		});
	}

	// Shift + LMB with empty equipment slot
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onShiftEquip(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (!event.isShiftClick()) return;
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;
		if (!ItemStack.isEmpty(event.getCursor())) return;

		int rawSlot = event.getRawSlot();
		if (rawSlot < 5) return;

		ItemStack item = event.getCurrentItem();
		if (item == null) return;
		if (!TrappedNewbieTags.COSMETIC_ARMOR.isTagged(item.getType())) return;

		if (MaterialTags.HELMETS.isTagged(item) || isEquipable(item, EquipmentSlot.HEAD, false)) {
			if (tryToEquip(player, item, EquipmentSlot.HEAD)) {
				event.setCurrentItem(null);
				event.setCancelled(true);
			}
		} else if (MaterialTags.CHESTPLATES.isTagged(item) || item.getType() == Material.ELYTRA || isEquipable(item, EquipmentSlot.CHEST, false)) {
			if (tryToEquip(player, item, EquipmentSlot.CHEST)) {
				event.setCurrentItem(null);
				event.setCancelled(true);
			}
		} else if (MaterialTags.LEGGINGS.isTagged(item) || isEquipable(item, EquipmentSlot.LEGS, false)) {
			if (tryToEquip(player, item, EquipmentSlot.LEGS)) {
				event.setCurrentItem(null);
				event.setCancelled(true);
			}
		} else if (MaterialTags.BOOTS.isTagged(item) || isEquipable(item, EquipmentSlot.FEET, false)) {
			if (tryToEquip(player, item, EquipmentSlot.FEET)) {
				event.setCurrentItem(null);
				event.setCancelled(true);
			}
		} else if (TrappedNewbieTags.GLOVES.isTagged(item.getType())) {
			VisualArmor visualArmor = getVisualArmor(player);
			if (!visualArmor.isArmorPreview()) return;
			if (visualArmor.hasGloves()) return;

			visualArmor.setGloves(item);
			event.setCurrentItem(null);
			event.setCancelled(true);
		}
	}

	private boolean tryToEquip(Player player, ItemStack item, EquipmentSlot slot) {
		VisualArmor visualArmor = getVisualArmor(player);
		if (visualArmor.isArmorPreview()) {
			ItemStack currentItem = player.getInventory().getItem(slot);
			if (ItemStack.isEmpty(currentItem)) {
				player.getInventory().setItem(slot, item);
				return true;
			}
			return false;
		}
		if (visualArmor.hasItem(slot)) return false;
		visualArmor.setItem(slot, item);
		return true;
	}

	// LMB/RMB on visual head slot
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onItemEquip(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;
		if (event.getRawSlot() != InventorySlotHelper.HEAD_SLOT) return;
		if (event.getClick() != ClickType.LEFT && event.getClick() != ClickType.RIGHT) return;

		ItemStack item = event.getCursor();
		if (ItemStack.isEmpty(item)) return;

		VisualArmor visualArmor = getVisualArmor(player);
		if (visualArmor.isArmorPreview()) return;
		if (visualArmor.hasHelmet()) return;

		event.setCancelled(true);
		visualArmor.setHelmet(item.asOne());
		item.subtract();
		player.updateInventory();
	}

	// Toggling visual layer with Q
	// Swapping visual gloves with F
	// Manually handling all remaining click types to account for air in armor slots
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onArmorSwap(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;

		int rawSlot = event.getRawSlot();
		if (rawSlot < 5) return;
		if (rawSlot > 8 && rawSlot != InventorySlotHelper.OFF_HAND) return;

		event.setCancelled(true);
		ClickType clickType = event.getClick();
		InventoryAction action = event.getAction();

		// Oof, empty equipment slot click, calculate action manually
		if (action == InventoryAction.NOTHING && isMissingArmor(player, rawSlot)) {
			action = switch (clickType) {
				case LEFT -> InventoryAction.PICKUP_ALL;
				case RIGHT -> InventoryAction.PICKUP_HALF;
				case SHIFT_LEFT, SHIFT_RIGHT -> InventoryAction.MOVE_TO_OTHER_INVENTORY;
				case DROP -> InventoryAction.DROP_ONE_SLOT;
				case CONTROL_DROP -> InventoryAction.DROP_ALL_SLOT;
				case SWAP_OFFHAND, NUMBER_KEY -> InventoryAction.HOTBAR_SWAP;
				default -> action;
			};
		}

		if (action == InventoryAction.DROP_ONE_SLOT) {
			if (rawSlot != InventorySlotHelper.OFF_HAND) getVisualArmor(player).toggleArmorPreview();
			player.updateInventory();
			return;
		}

		VisualArmor visualArmor = getVisualArmor(player);

		if (action == InventoryAction.DROP_ALL_SLOT) {
			if (visualArmor.isArmorPreview()) {
				event.setCancelled(false);
			} else {
				EquipmentSlot equipmentSlot = InventoryUtil.getBySlot(rawSlot);
				if (visualArmor.hasItem(equipmentSlot)) {
					ItemStack item = visualArmor.getItem(equipmentSlot);
					visualArmor.setItem(equipmentSlot, null);
					player.dropItem(item);
				}
			}
			player.updateInventory();
			return;
		}

		if (rawSlot == InventorySlotHelper.OFF_HAND) {
			if (!visualArmor.isArmorPreview()) {
				event.setCancelled(false);
				return;
			}

			if (!player.hasPotionEffect(PotionEffectType.WEAKNESS)
					|| Objects.requireNonNull(player.getPotionEffect(PotionEffectType.WEAKNESS)).getAmplifier() < 1) {
				if (visualArmor.hasGloves() && visualArmor.getGloves().containsEnchantment(Enchantment.BINDING_CURSE)) return;
			}

			if (clickType.isShiftClick()) {
				if (!InventoryUtil.tryToAdd(player, visualArmor.getGloves())) return;
				visualArmor.setGloves(null);
			} else if (clickType == ClickType.SWAP_OFFHAND) {
				ItemStack current = player.getInventory().getItemInOffHand();
				if (current.getType() != Material.AIR) return;

				player.getInventory().setItemInOffHand(visualArmor.getGloves());
				visualArmor.setGloves(null);
			} else if (clickType == ClickType.NUMBER_KEY) {
				int slot = event.getHotbarButton();
				ItemStack current = player.getInventory().getItem(slot);
				if (!ItemStack.isEmpty(current)) return;

				player.getInventory().setItem(slot, visualArmor.getGloves());
				visualArmor.setGloves(null);
			} else if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
				player.setItemOnCursor(visualArmor.getGloves());
				visualArmor.setGloves(null);
			}

			player.updateInventory();
			return;
		}

		EquipmentSlot equipmentSlot = InventoryUtil.getBySlot(rawSlot);
		if (!player.hasPotionEffect(PotionEffectType.WEAKNESS) || Objects.requireNonNull(player.getPotionEffect(PotionEffectType.WEAKNESS)).getAmplifier() < 1) {
			if (visualArmor.isArmorPreview()) {
				ItemStack item = event.getCurrentItem();
				if (item != null && item.containsEnchantment(Enchantment.BINDING_CURSE)) return;
			} else {
				if (visualArmor.hasItem(equipmentSlot)) {
					ItemStack item = visualArmor.getItem(equipmentSlot);
					if (item.containsEnchantment(Enchantment.BINDING_CURSE)) return;
					if (item.getType() == Material.ENCHANTED_BOOK && item.hasData(DataComponentTypes.STORED_ENCHANTMENTS)) {
						ItemEnchantments data = item.getData(DataComponentTypes.STORED_ENCHANTMENTS);
						assert data != null;
						if (data.enchantments().containsKey(Enchantment.BINDING_CURSE)) return; // Well, you did this to yourself
					}
				}
			}
		}

		if (player.getItemOnCursor().getType() != Material.AIR) {
			ItemStack cursor = event.getCursor();
			boolean canEquip = switch (equipmentSlot) {
				case HEAD -> MaterialTags.HEAD_EQUIPPABLE.isTagged(cursor) || isEquipable(cursor, EquipmentSlot.HEAD, false);
				case CHEST -> MaterialTags.CHEST_EQUIPPABLE.isTagged(cursor) || isEquipable(cursor, EquipmentSlot.CHEST, false);
				case LEGS -> MaterialTags.LEGGINGS.isTagged(cursor) || isEquipable(cursor, EquipmentSlot.LEGS, false);
				case FEET -> MaterialTags.BOOTS.isTagged(cursor) || isEquipable(cursor, EquipmentSlot.BODY, false);
				case OFF_HAND -> TrappedNewbieTags.GLOVES.isTagged(cursor.getType());
				default -> false;
			};
			if (!canEquip) return;

			if (visualArmor.isArmorPreview()) {
				player.setItemOnCursor(event.getCurrentItem());
				player.getInventory().setItem(equipmentSlot, cursor);
			} else {
				player.setItemOnCursor(visualArmor.getItem(equipmentSlot));
				visualArmor.setItem(equipmentSlot, cursor);
			}
			player.updateInventory();
			return;
		}

		if (visualArmor.isArmorPreview() ? ItemStack.isEmpty(event.getCurrentItem()) : !visualArmor.hasItem(equipmentSlot)) {
			player.updateInventory();
			return;
		}

		if (clickType.isShiftClick()) {
			if (visualArmor.isArmorPreview()) {
				if (!InventoryUtil.tryToAdd(player, Objects.requireNonNull(event.getCurrentItem()))) return;
				player.getInventory().setItem(equipmentSlot, ItemStack.empty());
			} else {
				if (!InventoryUtil.tryToAdd(player, visualArmor.getItem(equipmentSlot))) return;
				visualArmor.setItem(equipmentSlot, null);
			}
		} else if (clickType == ClickType.SWAP_OFFHAND) {
			ItemStack current = player.getInventory().getItemInOffHand();
			if (current.getType() != Material.AIR) return;
			if (visualArmor.isArmorPreview()) {
				// Since it'll just disappear…
				return;
			} else {
				player.getInventory().setItemInOffHand(visualArmor.getItem(equipmentSlot));
				visualArmor.setItem(equipmentSlot, null);
			}
		} else if (clickType == ClickType.NUMBER_KEY) {
			int slot = event.getHotbarButton();
			ItemStack current = player.getInventory().getItem(slot);
			if (!ItemStack.isEmpty(current)) return;
			if (visualArmor.isArmorPreview()) {
				player.getInventory().setItem(slot, event.getCurrentItem());
				player.getInventory().setItem(equipmentSlot, ItemStack.empty());
			} else {
				player.getInventory().setItem(slot, visualArmor.getItem(equipmentSlot));
				visualArmor.setItem(equipmentSlot, null);
			}
		} else if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
			if (visualArmor.isArmorPreview()) {
				player.setItemOnCursor(event.getCurrentItem());
				player.getInventory().setItem(equipmentSlot, ItemStack.empty());
			} else {
				player.setItemOnCursor(visualArmor.getItem(equipmentSlot));
				visualArmor.setItem(equipmentSlot, null);
			}
		}

		player.updateInventory();
	}

	private boolean isMissingArmor(Player player, int slot) {
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return false;
		return ItemStack.isEmpty(player.getOpenInventory().getItem(slot));
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player player)) return;
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;

		getVisualArmor(player).setArmorPreview(false);
		player.updateInventory();
	}

	// Quick equip with RMB
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().isRightClick()) return;
		if (event.isBlockInHand() && event.getAction() == Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		if (event.getClickedBlock() != null && !player.isSneaking() && event.getClickedBlock().getType().isInteractable()) return;

		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getAmount() > 1) return;

		if (MaterialTags.HEAD_EQUIPPABLE.isTagged(item) || isEquipable(item, EquipmentSlot.HEAD, true)) {
			if (swapItems(player, item, EquipmentSlot.HEAD))
				event.setCancelled(true);
		} else if (MaterialTags.CHEST_EQUIPPABLE.isTagged(item) || isEquipable(item, EquipmentSlot.CHEST, true)) {
			if (swapItems(player, item, EquipmentSlot.CHEST))
				event.setCancelled(true);
		} else if (MaterialTags.LEGGINGS.isTagged(item) || isEquipable(item, EquipmentSlot.LEGS, true)) {
			if (swapItems(player, item, EquipmentSlot.LEGS))
				event.setCancelled(true);
		} else if (MaterialTags.BOOTS.isTagged(item) || isEquipable(item, EquipmentSlot.FEET, true)) {
			if (swapItems(player, item, EquipmentSlot.FEET))
				event.setCancelled(true);
		} else if (TrappedNewbieTags.GLOVES.isTagged(item.getType())) {
			if (swapItems(player, item, EquipmentSlot.OFF_HAND))
				event.setCancelled(true);
		}
	}

	private boolean isEquipable(ItemStack item, EquipmentSlot slot, boolean quickSwap) {
		if (!item.hasData(DataComponentTypes.EQUIPPABLE)) return false;

		Equippable equippable = item.getData(DataComponentTypes.EQUIPPABLE);
		assert equippable != null;
		if (quickSwap && !equippable.swappable()) return false;
		if (equippable.slot() != slot) return false;

		RegistryKeySet<EntityType> allowedEntities = equippable.allowedEntities();
		return allowedEntities == null || allowedEntities.contains(PLAYER_TYPED_KEY);
	}

	private boolean swapItems(Player player, ItemStack item, EquipmentSlot slot) {
		boolean cosmetic = TrappedNewbieTags.COSMETIC_ARMOR.isTagged(item.getType());
		boolean bundle = cosmetic != player.isSneaking();

		if (!bundle && MaterialTags.ARMOR.isTagged(item.getType())) return false;

		ItemStack oldItem = item.clone();

		// Without delay RMB on a block triggers interact event for the second time
		TrappedNewbie.scheduler().sync(() -> {
			if (!player.isValid()) return;

			ItemStack hand = player.getInventory().getItemInMainHand();
			if (hand.getAmount() > 1) return;
			if (!hand.equals(oldItem)) return;

			VisualArmor visualArmor = getVisualArmor(player);
			if (slot == EquipmentSlot.OFF_HAND) {
				player.getInventory().setItemInMainHand(visualArmor.getGloves());
				visualArmor.setGloves(hand);
			} else {
				if (bundle) {
					player.getInventory().setItemInMainHand(visualArmor.getItem(slot));
					visualArmor.setItem(slot, hand);
				} else {
					ItemStack currentItem = player.getInventory().getItem(slot);
					player.getInventory().setItemInMainHand(currentItem);
					player.getInventory().setItem(slot, hand);
				}
			}

			player.swingMainHand();
			player.emitSound(getEquipSound(hand), 2F, 0.3F);
			player.updateInventory();
		}, 1L);

		return true;
	}

	private Sound getEquipSound(ItemStack item) {
		return switch (item.getType()) {
			case ELYTRA -> Sound.ITEM_ARMOR_EQUIP_ELYTRA;
			case TURTLE_HELMET -> Sound.ITEM_ARMOR_EQUIP_TURTLE;
			case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS -> Sound.ITEM_ARMOR_EQUIP_LEATHER;
			case CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS -> Sound.ITEM_ARMOR_EQUIP_CHAIN;
			case IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS -> Sound.ITEM_ARMOR_EQUIP_IRON;
			case GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS -> Sound.ITEM_ARMOR_EQUIP_GOLD;
			case DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS -> Sound.ITEM_ARMOR_EQUIP_DIAMOND;
			case NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS -> Sound.ITEM_ARMOR_EQUIP_NETHERITE;
			default -> Sound.ITEM_ARMOR_EQUIP_GENERIC;
		};
	}

	@EventHandler
	public void onTombstone(TombstoneDestroyEvent event) {
		Player player = event.getPlayer();
		VisualArmor visualArmor = player == null ? null : getVisualArmor(player);
		for (ReadWriteNBT data : event.getStorages()) {
			if (!data.hasTag(ARMOR_BUNDLE_TAG)) continue;

			ReadWriteNBT bundle = data.getCompound(ARMOR_BUNDLE_TAG);
			if (bundle == null) continue;
			for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
				if (!bundle.hasTag(equipmentSlot.name())) continue;

				ItemStack item = bundle.getItemStack(equipmentSlot.name());
				if (item == null) continue;

				if (visualArmor == null || !player.canUseEquipmentSlot(equipmentSlot) || visualArmor.hasItem(equipmentSlot)) {
					event.getToDrop().add(item);
				} else {
					visualArmor.setItem(equipmentSlot, item);
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerTombstoneCreateEvent event) {
		Player player = event.getPlayer();
		ReadWriteNBT data = event.getData();
		VisualArmor visualArmor = getVisualArmor(player);
		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			if (!player.canUseEquipmentSlot(equipmentSlot)) continue;
			if (!visualArmor.hasItem(equipmentSlot)) continue;

			data.getOrCreateCompound(ARMOR_BUNDLE_TAG).setItemStack(equipmentSlot.name(), visualArmor.getItem(equipmentSlot));
			visualArmor.setItem(equipmentSlot, null);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getPlayer();
		List<ItemStack> drops = event.getDrops();
		VisualArmor visualArmor = getVisualArmor(player);
		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			if (!player.canUseEquipmentSlot(equipmentSlot)) continue;
			if (!visualArmor.hasItem(equipmentSlot)) continue;

			drops.add(visualArmor.getItem(equipmentSlot));
			visualArmor.setItem(equipmentSlot, null);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(PlayerDataLoadedEvent event) {
		ReadWriteNBT nbt = event.getData();
		if (!nbt.hasTag(ARMOR_BUNDLE_TAG)) return;

		nbt = nbt.getOrCreateCompound(ARMOR_BUNDLE_TAG);
		Player player = event.getPlayer();
		ARMOR_BUNDLES.put(player.getUniqueId(), load(player, nbt));
		player.updateInventory();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSave(PlayerDataSaveEvent event) {
		Player player = event.getPlayer();
		VisualArmor visualArmor = event.isQuit() ? ARMOR_BUNDLES.remove(player.getUniqueId()) : getVisualArmor(player);
		if (visualArmor == null) return;

		ReadWriteNBT nbt = event.getData();
		save(visualArmor, nbt.getOrCreateCompound(ARMOR_BUNDLE_TAG));
	}

	private void save(VisualArmor visualArmor, ReadWriteNBT nbt) {
		if (visualArmor.hasHelmet()) nbt.setItemStack(HELMET_TAG, visualArmor.getHelmet());
		if (visualArmor.hasChestplate()) nbt.setItemStack(CHESTPLATE_TAG, visualArmor.getChestplate());
		if (visualArmor.hasLeggings()) nbt.setItemStack(LEGGINGS_TAG, visualArmor.getLeggings());
		if (visualArmor.hasBoots()) nbt.setItemStack(BOOTS_TAG, visualArmor.getBoots());
		if (visualArmor.hasGloves()) nbt.setItemStack(GLOVES_TAG, visualArmor.getGloves());
	}

	private static VisualArmor empty(Player player) {
		return new VisualArmor(player, null, null, null, null, null);
	}

	private static VisualArmor load(Player player, ReadableNBT nbt) {
		return new VisualArmor(
				player,
				nbt.hasTag(HELMET_TAG) ? nbt.getItemStack(HELMET_TAG) : null,
				nbt.hasTag(CHESTPLATE_TAG) ? nbt.getItemStack(CHESTPLATE_TAG) : null,
				nbt.hasTag(LEGGINGS_TAG) ? nbt.getItemStack(LEGGINGS_TAG) : null,
				nbt.hasTag(BOOTS_TAG) ? nbt.getItemStack(BOOTS_TAG) : null,
				nbt.hasTag(GLOVES_TAG) ? nbt.getItemStack(GLOVES_TAG) : null
		);
	}

	/**
	 * Gets the player's visual armor
	 *
	 * @param player player
	 * @return visual armor
	 */
	public static VisualArmor getVisualArmor(Player player) {
		return ARMOR_BUNDLES.computeIfAbsent(player.getUniqueId(), k -> empty(player));
	}

}
