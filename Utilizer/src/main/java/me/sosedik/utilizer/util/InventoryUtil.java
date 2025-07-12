package me.sosedik.utilizer.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BundleContents;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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

	private static final List<Function<Player, List<@Nullable ItemStack>>> EXTRA_ITEM_CHECKERS = new ArrayList<>();

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
	 * Tries to find an item within player's inventory
	 *
	 * @param player player
	 * @param predicate item predicate
	 * @return item, if found
	 */
	public static @Nullable ItemStack findItem(Player player, Predicate<ItemStack> predicate) {
		PlayerInventory inventory = player.getInventory();

		ItemStack offHand = checkFolding(inventory.getItemInOffHand(), predicate);
		if (offHand != null)
			return offHand;

		for (ItemStack item : inventory.getStorageContents()) {
			if (ItemStack.isEmpty(item)) continue;

			item = checkFolding(item, predicate);
			if (item != null)
				return item;
		}

		for (ItemStack item : inventory.getArmorContents()) {
			if (ItemStack.isEmpty(item)) continue;

			item = checkFolding(item, predicate);
			if (item != null)
				return item;
		}

		ItemStack cursor = checkFolding(player.getOpenInventory().getCursor(), predicate);
		if (cursor != null)
			return cursor;

		for (Function<Player, List<@Nullable ItemStack>> extras : EXTRA_ITEM_CHECKERS) {
			List<@Nullable ItemStack> items = extras.apply(player);
			for (ItemStack item : items) {
				if (ItemStack.isEmpty(item)) continue;

				item = checkFolding(item, predicate);
				if (item != null)
					return item;
			}
		}

		return null;
	}

	private static @Nullable ItemStack checkFolding(ItemStack item, Predicate<ItemStack> predicate) {
		if (predicate.test(item))
			return item;

		if (item.hasData(DataComponentTypes.BUNDLE_CONTENTS)) {
			BundleContents data = item.getData(DataComponentTypes.BUNDLE_CONTENTS);
			if (data == null) return null;

			for (ItemStack storedItem : data.contents()) {
				if (predicate.test(storedItem))
					return storedItem;
			}
		} else if (item.hasData(DataComponentTypes.CONTAINER)) {
			ItemContainerContents data = item.getData(DataComponentTypes.CONTAINER);
			if (data == null) return null;

			for (ItemStack storedItem : data.contents()) {
				if (predicate.test(storedItem))
					return storedItem;
			}
		}

		return null;
	}

	/**
	 * Adds items to player's inventory, or
	 * drops if not enough space
	 *
	 * @param player player
	 * @param item item
	 * @param shiftClickOrder whether to use the shift click item ordering
	 */
	public static void addOrDrop(Player player, ItemStack item, boolean shiftClickOrder) {
		PlayerInventory inv = player.getInventory();
		if (shiftClickOrder) {
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
		} else {
			inv.addItem(item).values().forEach(player::dropItem);
		}
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
			case HAND -> InventorySlotHelper.FIRST_HOTBAR_SLOT + player.getInventory().getHeldItemSlot();
			default -> InventorySlotHelper.CURSOR;
		};
	}

	public static void addExtraItemChecker(Function<Player, List<@Nullable ItemStack>> checker) {
		EXTRA_ITEM_CHECKERS.add(checker);
	}

}
