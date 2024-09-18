package me.sosedik.trappednewbie.listener.player;

import com.destroystokyo.paper.MaterialTags;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.InventoryUtil;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Players have a visual second armor layer
 */
// MCCheck: 1.21.1, new equipable tag
public class VisualArmorLayer implements Listener {

	private static final String ARMOR_BUNDLE_TAG = "visual_armor";
	private static final String HELMET_TAG = "helmet";
	private static final String CHESTPLATE_TAG = "chestplate";
	private static final String LEGGINGS_TAG = "leggings";
	private static final String BOOTS_TAG = "boots";
	private static final String GLOVES_TAG = "gloves";

	private static final Map<UUID, VisualArmor> ARMOR_BUNDLES = new HashMap<>();

	public VisualArmorLayer() {
		EntityUtil.addDarknessExemptRule(entity -> {
			if (!(entity instanceof Player player)) return false;

			VisualArmor visualArmor = getVisualArmor(player);
			return visualArmor.hasHelmet() && ItemUtil.isLightSource(visualArmor.getHelmet());
		});
	}

	@EventHandler
	public void onJoin(@NotNull PlayerJoinEvent event) {
		applyVisualArmor(event.getPlayer());
	}

	@EventHandler
	public void onRespawn(@NotNull PlayerPostRespawnEvent event) {
		applyVisualArmor(event.getPlayer());
	}

	@EventHandler
	public void onArmorChange(@NotNull PlayerArmorChangeEvent event) {
		applyVisualArmor(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onShiftEquip(@NotNull InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (!event.isShiftClick()) return;
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;
		if (!ItemStack.isEmpty(event.getCursor())) return;

		int rawSlot = event.getRawSlot();
		if (rawSlot < 5) return;

		ItemStack item = event.getCurrentItem();
		if (item == null) return;

		if (MaterialTags.HELMETS.isTagged(item)) {
			if (tryToEquip(player, item, EquipmentSlot.HEAD)) {
				event.setCurrentItem(null);
				event.setCancelled(true);
			}
		} else if (MaterialTags.CHESTPLATES.isTagged(item) || item.getType() == Material.ELYTRA) {
			if (tryToEquip(player, item, EquipmentSlot.CHEST)) {
				event.setCurrentItem(null);
				event.setCancelled(true);
			}
		} else if (MaterialTags.LEGGINGS.isTagged(item)) {
			if (tryToEquip(player, item, EquipmentSlot.LEGS)) {
				event.setCurrentItem(null);
				event.setCancelled(true);
			}
		} else if (MaterialTags.BOOTS.isTagged(item)) {
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

	private boolean tryToEquip(@NotNull Player player, @NotNull ItemStack item, @NotNull EquipmentSlot slot) {
		VisualArmor visualArmor = getVisualArmor(player);
		if (visualArmor.isArmorPreview()) {
			ItemStack currentItem = player.getInventory().getItem(slot);
			if (isEmpty(currentItem)) {
				player.getInventory().setItem(slot, item);
				return true;
			}
			return false;
		}
		if (visualArmor.hasItem(slot)) return false;
		visualArmor.setItem(slot, item);
		return true;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onItemEquip(@NotNull InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;
		if (event.getRawSlot() != 5) return;
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

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onArmorSwap(@NotNull InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;

		int rawSlot = event.getRawSlot();
		if (rawSlot < 5) return;
		if (rawSlot > 8 && rawSlot != InventorySlotHelper.OFF_HAND) return;

		event.setCancelled(true);
		InventoryAction action = event.getAction();
		if (action == InventoryAction.DROP_ONE_SLOT || action == InventoryAction.DROP_ALL_SLOT || (action == InventoryAction.NOTHING && isMissingArmor(player, rawSlot))) {
			if (rawSlot == InventorySlotHelper.OFF_HAND) return;
			getVisualArmor(player).toggleArmorPreview();
			player.updateInventory();
			return;
		}

		VisualArmor visualArmor = getVisualArmor(player);
		if (rawSlot == InventorySlotHelper.OFF_HAND) {
			if (!visualArmor.isArmorPreview()) {
				event.setCancelled(false);
				return;
			}
			if (!player.hasPotionEffect(PotionEffectType.WEAKNESS)
					|| Objects.requireNonNull(player.getPotionEffect(PotionEffectType.WEAKNESS)).getAmplifier() < 1) {
				if (visualArmor.hasGloves() && visualArmor.getGloves().containsEnchantment(Enchantment.BINDING_CURSE)) return;
			}

			ClickType clickType = event.getClick();
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
				if (visualArmor.hasItem(equipmentSlot) && visualArmor.getItem(equipmentSlot).containsEnchantment(Enchantment.BINDING_CURSE)) return;
			}
		}

		if (player.getItemOnCursor().getType() != Material.AIR) {
			ItemStack cursor = event.getCursor();
			boolean canEquip = switch (equipmentSlot) {
				case HEAD -> MaterialTags.HEAD_EQUIPPABLE.isTagged(cursor);
				case CHEST -> MaterialTags.CHEST_EQUIPPABLE.isTagged(cursor);
				case LEGS -> MaterialTags.LEGGINGS.isTagged(cursor);
				case FEET -> MaterialTags.BOOTS.isTagged(cursor);
				case OFF_HAND -> TrappedNewbieTags.GLOVES.isTagged(cursor.getType());
				default -> false;
			};
			if (!canEquip) return;

			if (visualArmor.isArmorPreview()) {
				player.setItemOnCursor(isEmpty(event.getCurrentItem()) ? null : event.getCurrentItem());
				player.getInventory().setItem(equipmentSlot, cursor);
			} else {
				player.setItemOnCursor(visualArmor.getItem(equipmentSlot));
				visualArmor.setItem(equipmentSlot, cursor);
			}
			player.updateInventory();
			return;
		}

		if (visualArmor.isArmorPreview() ? isEmpty(event.getCurrentItem()) : !visualArmor.hasItem(equipmentSlot)) return;

		ClickType clickType = event.getClick();
		if (clickType.isShiftClick()) {
			if (visualArmor.isArmorPreview()) {
				if (!InventoryUtil.tryToAdd(player, Objects.requireNonNull(event.getCurrentItem()))) return;
				player.getInventory().setItem(equipmentSlot, new ItemStack(TrappedNewbieItems.MATERIAL_AIR));
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
				player.getInventory().setItem(equipmentSlot, new ItemStack(TrappedNewbieItems.MATERIAL_AIR));
			} else {
				player.getInventory().setItem(slot, visualArmor.getItem(equipmentSlot));
				visualArmor.setItem(equipmentSlot, null);
			}
		} else if (clickType == ClickType.LEFT || clickType == ClickType.RIGHT) {
			if (visualArmor.isArmorPreview()) {
				player.setItemOnCursor(event.getCurrentItem());
				player.getInventory().setItem(equipmentSlot, new ItemStack(TrappedNewbieItems.MATERIAL_AIR));
			} else {
				player.setItemOnCursor(visualArmor.getItem(equipmentSlot));
				visualArmor.setItem(equipmentSlot, null);
			}
		}

		player.updateInventory();
	}

	private boolean isMissingArmor(@NotNull Player player, int slot) {
		boolean missing = ItemStack.isEmpty(player.getOpenInventory().getItem(slot));
		if (missing) applyVisualArmor(player);
		return missing;
	}

	@EventHandler
	public void onInventoryClose(@NotNull InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player player)) return;
		if (player.getOpenInventory().getTopInventory().getType() != InventoryType.CRAFTING) return;

		getVisualArmor(player).setArmorPreview(false);
		player.updateInventory();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(@NotNull PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!event.getAction().isRightClick()) return;
		if (event.isBlockInHand() && event.getAction() == Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		if (event.getClickedBlock() != null && !player.isSneaking() && event.getClickedBlock().getType().isInteractable()) return;

		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getAmount() > 1) return;

		if (MaterialTags.HEAD_EQUIPPABLE.isTagged(item)) {
			swapItems(player, item, EquipmentSlot.HEAD);
			event.setCancelled(true);
		} else if (MaterialTags.CHEST_EQUIPPABLE.isTagged(item)) {
			swapItems(player, item, EquipmentSlot.CHEST);
			event.setCancelled(true);
		} else if (MaterialTags.LEGGINGS.isTagged(item)) {
			swapItems(player, item, EquipmentSlot.LEGS);
			event.setCancelled(true);
		} else if (MaterialTags.BOOTS.isTagged(item)) {
			swapItems(player, item, EquipmentSlot.FEET);
			event.setCancelled(true);
		} else if (TrappedNewbieTags.GLOVES.isTagged(item.getType())) {
			swapItems(player, item, EquipmentSlot.OFF_HAND);
			event.setCancelled(true);
		}
	}

	private void swapItems(@NotNull Player player, @NotNull ItemStack hand, @NotNull EquipmentSlot slot) {
		hand = hand.clone();

		VisualArmor visualArmor = getVisualArmor(player);
		if (slot == EquipmentSlot.OFF_HAND) {
			player.getInventory().setItemInMainHand(visualArmor.getGloves());
			visualArmor.setGloves(hand);
		} else {
			boolean cosmetic = TrappedNewbieTags.COSMETIC_ARMOR.isTagged(hand.getType());
			boolean bundle = cosmetic != player.isSneaking();
			if (bundle) {
				player.getInventory().setItemInMainHand(visualArmor.getItem(slot));
				visualArmor.setItem(slot, hand);
			} else {
				ItemStack currentItem = player.getInventory().getItem(slot);
				player.getInventory().setItemInMainHand(currentItem.getType() == TrappedNewbieItems.MATERIAL_AIR ? null : currentItem);
				player.getInventory().setItem(slot, hand);
			}
		}

		player.swingMainHand();
		player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 2F, 0.3F);
		player.updateInventory();
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(@NotNull PlayerDeathEvent event) {
		Player player = event.getPlayer();
		List<ItemStack> drops = event.getDrops();
		VisualArmor visualArmor = getVisualArmor(player);
		for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
			if (!player.canUseEquipmentSlot(equipmentSlot)) continue;

			if (visualArmor.hasItem(equipmentSlot)) {
				drops.add(visualArmor.getItem(equipmentSlot));
				visualArmor.setItem(equipmentSlot, null);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(@NotNull PlayerDataLoadedEvent event) {
		ReadWriteNBT nbt = event.getData();
		if (!nbt.hasTag(ARMOR_BUNDLE_TAG)) return;

		nbt = nbt.getOrCreateCompound(ARMOR_BUNDLE_TAG);
		Player player = event.getPlayer();
		ARMOR_BUNDLES.put(player.getUniqueId(), load(player, nbt));
		player.updateInventory();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSave(@NotNull PlayerDataSaveEvent event) {
		Player player = event.getPlayer();
		VisualArmor visualArmor = event.isQuit() ? ARMOR_BUNDLES.remove(player.getUniqueId()) : getVisualArmor(player);
		if (visualArmor == null) return;

		ReadWriteNBT nbt = event.getData();
		save(visualArmor, nbt.getOrCreateCompound(ARMOR_BUNDLE_TAG));
	}

	private void save(@NotNull VisualArmor visualArmor, @NotNull ReadWriteNBT nbt) {
		if (visualArmor.hasHelmet()) nbt.setItemStack(HELMET_TAG, visualArmor.getHelmet());
		if (visualArmor.hasChestplate()) nbt.setItemStack(CHESTPLATE_TAG, visualArmor.getChestplate());
		if (visualArmor.hasLeggings()) nbt.setItemStack(LEGGINGS_TAG, visualArmor.getLeggings());
		if (visualArmor.hasBoots()) nbt.setItemStack(BOOTS_TAG, visualArmor.getBoots());
		if (visualArmor.hasGloves()) nbt.setItemStack(GLOVES_TAG, visualArmor.getGloves());
	}

	private boolean isEmpty(@Nullable ItemStack item) {
		return ItemStack.isEmpty(item) || item.getType() == TrappedNewbieItems.MATERIAL_AIR;
	}

	/**
	 * Apply ghost items in empty armor slots
	 * to allow drop action as well as show extra lore
	 *
	 * @param player player
	 */
	public static void applyVisualArmor(@NotNull Player player) {
		PlayerInventory inv = player.getInventory();
		if (ItemStack.isEmpty(inv.getHelmet())) inv.setHelmet(new ItemStack(TrappedNewbieItems.MATERIAL_AIR));
		if (ItemStack.isEmpty(inv.getChestplate())) inv.setChestplate(new ItemStack(TrappedNewbieItems.MATERIAL_AIR));
		if (ItemStack.isEmpty(inv.getLeggings())) inv.setLeggings(new ItemStack(TrappedNewbieItems.MATERIAL_AIR));
		if (ItemStack.isEmpty(inv.getBoots())) inv.setBoots(new ItemStack(TrappedNewbieItems.MATERIAL_AIR));
	}

	private static @NotNull VisualArmor empty(@NotNull Player player) {
		return new VisualArmor(player, null, null, null, null, null);
	}

	private static @NotNull VisualArmor load(@NotNull Player player, @NotNull ReadableNBT nbt) {
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
	public static @NotNull VisualArmor getVisualArmor(@NotNull Player player) {
		return ARMOR_BUNDLES.computeIfAbsent(player.getUniqueId(), k -> empty(player));
	}

}
