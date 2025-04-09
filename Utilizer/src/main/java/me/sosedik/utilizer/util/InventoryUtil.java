package me.sosedik.utilizer.util;

import me.sosedik.kiterino.inventory.InventorySlotHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class InventoryUtil {

	private InventoryUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final int[] SHIFT_CLICK_SLOTS_PRIORITY = {
		8, 7, 6, 5, 4, 3, 2, 1, 0,
		35, 34, 33, 32, 31, 30, 29, 28, 27,
		26, 25, 24, 23, 22, 21, 20, 19, 18,
		17, 16, 15, 14, 13, 12, 11, 10, 9
	};

	/**
	 * Tries to add the item into inventory
	 *
	 * @param player player
	 * @param item item
	 */
	public static boolean tryToAdd(Player player, ItemStack item) {
		PlayerInventory inv = player.getInventory();
		// Check all slots to see if the item fits into any
		for (int slot : SHIFT_CLICK_SLOTS_PRIORITY) {
			ItemStack current = inv.getItem(slot);
			if (ItemStack.isEmpty(current)) continue;
			if (!current.isSimilar(item)) continue;

			int amount = current.getAmount();
			int adding = item.getAmount();
			int max = current.getMaxStackSize();
			int result = amount + adding;
			if (result > max)
				continue;

			current.setAmount(result);
			return true;
		}

		// Check for empty slots
		for (int slot : SHIFT_CLICK_SLOTS_PRIORITY) {
			ItemStack current = inv.getItem(slot);
			if (!ItemStack.isEmpty(current)) continue;

			inv.setItem(slot, item);
			return true;
		}

		return false;
	}

	/**
	 * Adds items to player's inventory, or
	 * drops if not enough space
	 *
	 * @param player player
	 * @param item item
	 */
	public static void addOrDrop(Player player, ItemStack item) {
		PlayerInventory inv = player.getInventory();
		// Check all slots to see if the item fits into any
		for (int slot : SHIFT_CLICK_SLOTS_PRIORITY) {
			ItemStack current = inv.getItem(slot);
			if (ItemStack.isEmpty(current)) continue;
			if (!current.isSimilar(item)) continue;

			int amount = current.getAmount();
			int adding = item.getAmount();
			int max = current.getMaxStackSize();
			int result = Math.min(max, amount + adding);
			current.setAmount(result);
			item.subtract(result - amount);
			if (item.getAmount() <= 0)
				return;
		}
		// Check for empty slots
		for (int slot : SHIFT_CLICK_SLOTS_PRIORITY) {
			ItemStack current = inv.getItem(slot);
			if (!ItemStack.isEmpty(current)) continue;

			inv.setItem(slot, item);
			return;
		}
		// No space left, drop item
		player.dropItem(item);
	}

	/**
	 * Gets the equipment slot from the provided slot.
	 * Will default to {@link EquipmentSlot#HAND} if not unique slot.
	 *
	 * @param slot raw slot
	 * @return equipment slot
	 */
	public static EquipmentSlot getBySlot(int slot) {
		return switch (slot) {
			case InventorySlotHelper.HEAD_SLOT -> EquipmentSlot.HEAD;
			case InventorySlotHelper.CHEST_SLOT -> EquipmentSlot.CHEST;
			case InventorySlotHelper.LEGS_SLOT -> EquipmentSlot.LEGS;
			case InventorySlotHelper.FEET_SLOT -> EquipmentSlot.FEET;
			case InventorySlotHelper.OFF_HAND -> EquipmentSlot.OFF_HAND;
			default -> EquipmentSlot.HAND;
		};
	}

	/**
	 * Gets the raw slot id in player inventory for the provided equipment slot
	 *
	 * @param player player
	 * @param slot equipment slot
	 * @return the raw slot, or -1 if not existent
	 * @throws IllegalArgumentException if the provided slot is not supported by the {@link Player} entity
	 */
	public static int getSlot(Player player, EquipmentSlot slot) {
		return switch (slot) {
			case HEAD -> InventorySlotHelper.HEAD_SLOT;
			case CHEST -> InventorySlotHelper.CHEST_SLOT;
			case LEGS -> InventorySlotHelper.LEGS_SLOT;
			case FEET -> InventorySlotHelper.FEET_SLOT;
			case OFF_HAND -> InventorySlotHelper.OFF_HAND;
			case HAND -> 36 + player.getInventory().getHeldItemSlot();
			default -> -1;
		};
	}

}
