package me.sosedik.utilizer.util;

import me.sosedik.kiterino.inventory.InventorySlotHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

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
	public static boolean tryToAdd(@NotNull Player player, @NotNull ItemStack item) {
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
	 * Gets the equipment slot from the provided slot.
	 * Will default to {@link EquipmentSlot#HAND} if not unique slot.
	 *
	 * @param slot raw slot
	 * @return equipment slot
	 */
	public static @NotNull EquipmentSlot getBySlot(int slot) {
		return switch (slot) {
			case 5 -> EquipmentSlot.HEAD;
			case 6 -> EquipmentSlot.CHEST;
			case 7 -> EquipmentSlot.LEGS;
			case 8 -> EquipmentSlot.FEET;
			case InventorySlotHelper.OFF_HAND -> EquipmentSlot.OFF_HAND;
			default -> EquipmentSlot.HAND;
		};
	}

	/**
	 * Gets the raw slot id in player inventory for the provided equipment slot
	 *
	 * @param player player
	 * @param slot equipment slot
	 * @return the raw slot
	 * @throws IllegalArgumentException if the provided slot is not supported by the {@link Player} entity
	 */
	public static int getSlot(@NotNull Player player, @NotNull EquipmentSlot slot) {
		return switch (slot) {
			case HEAD -> 5;
			case CHEST -> 6;
			case LEGS -> 7;
			case FEET -> 8;
			case OFF_HAND -> InventorySlotHelper.OFF_HAND;
			case HAND -> 36 + player.getInventory().getHeldItemSlot();
			default -> throw new IllegalArgumentException("Unsupported equipment slot: " + slot.name());
		};
	}

}