package me.sosedik.utilizer.util;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BundleContents;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@NullMarked
public class InventoryUtil {

	private InventoryUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static final String STORED_SLOTTED_ITEMS_TAG = "items";

	private static final int[] SHIFT_CLICK_SLOTS_PRIORITY = {
		8, 7, 6, 5, 4, 3, 2, 1, 0,
		35, 34, 33, 32, 31, 30, 29, 28, 27,
		26, 25, 24, 23, 22, 21, 20, 19, 18,
		17, 16, 15, 14, 13, 12, 11, 10, 9
	};

	private static final List<ExtraItemChecker> EXTRA_ITEM_CHECKERS = new ArrayList<>();

	private record ExtraItemChecker(Function<Player, List<@Nullable ItemStack>> finder, BiConsumer<Player, UnaryOperator<ItemStack>> modifier) {}

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
	 * Tries to finder an item within player's inventory
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

		InventoryView view = player.getOpenInventory();
		if (view.getType() == InventoryType.CRAFTING) {
			for (int i = 1; i < 5; i++) {
				ItemStack item = view.getItem(i);
				if (ItemStack.isEmpty(item)) continue;

				item = checkFolding(item, predicate);
				if (item != null)
					return item;
			}
		}

		for (ItemStack item : inventory.getArmorContents()) {
			if (ItemStack.isEmpty(item)) continue;

			item = checkFolding(item, predicate);
			if (item != null)
				return item;
		}

		ItemStack cursor = checkFolding(view.getCursor(), predicate);
		if (cursor != null)
			return cursor;

		for (ExtraItemChecker extras : EXTRA_ITEM_CHECKERS) {
			List<@Nullable ItemStack> items = extras.finder().apply(player);
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
	 * Tries to finder an item within player's inventory
	 *
	 * @param entity entity
	 * @param predicate item modifier
	 */
	public static void modifyItems(LivingEntity entity, UnaryOperator<ItemStack> predicate) {
		EntityEquipment inventory = entity.getEquipment();
		if (inventory == null) return;

		inventory.setItemInOffHand(modifyFolding(inventory.getItemInOffHand(), predicate));

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (!entity.canUseEquipmentSlot(slot)) continue;

			inventory.setItem(slot, modifyFolding(inventory.getItem(slot), predicate));
		}

		if (entity instanceof Player player) {
			@Nullable ItemStack[] storage = player.getInventory().getStorageContents();
			ItemStack item;
			for (int i = 0; i < storage.length; i++) {
				item = storage[i];
				if (ItemStack.isEmpty(item)) continue;

				storage[i] = modifyFolding(item, predicate);
			}

			InventoryView view = player.getOpenInventory();
			if (view.getType() == InventoryType.CRAFTING) {
				for (int i = 1; i < 5; i++) {
					item = view.getItem(i);
					if (ItemStack.isEmpty(item)) continue;

					view.setItem(i, modifyFolding(item, predicate));
				}
			}

			view.setCursor(modifyFolding(view.getCursor(), predicate));

			for (ExtraItemChecker extras : EXTRA_ITEM_CHECKERS)
				extras.modifier().accept(player, predicate);
		}
	}

	private static ItemStack modifyFolding(ItemStack item, UnaryOperator<ItemStack> predicate) {
		item = predicate.apply(item);

		if (item.hasData(DataComponentTypes.BUNDLE_CONTENTS)) {
			BundleContents data = item.getData(DataComponentTypes.BUNDLE_CONTENTS);
			if (data == null) return item;

			List<ItemStack> contents = new ArrayList<>(data.contents());
			contents.replaceAll(predicate);
			item.setData(DataComponentTypes.BUNDLE_CONTENTS, BundleContents.bundleContents(contents));
		} else if (item.hasData(DataComponentTypes.CONTAINER)) {
			ItemContainerContents data = item.getData(DataComponentTypes.CONTAINER);
			if (data == null) return item;

			List<ItemStack> contents = new ArrayList<>(data.contents());
			contents.replaceAll(predicate);
			item.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(contents));
		}

		return item;
	}

	/**
	 * Replaces item if air or adds to inventory otherwise
	 *
	 * @param entity entity
	 * @param hand hand
	 * @param item item
	 */
	public static void replaceOrAdd(Entity entity, EquipmentSlot hand, ItemStack item) {
		if (entity instanceof Player player) {
			EntityEquipment equipment = player.getEquipment();
			if (equipment.getItem(hand).isEmpty()) {
				equipment.setItem(hand, item);
				return;
			}
		}
		addOrDrop(entity, item, false);
	}

	/**
	 * Replaces item if air or adds to inventory otherwise
	 *
	 * @param entity entity
	 * @param item item
	 */
	public static void replaceOrAdd(Entity entity, ItemStack item) {
		if (entity instanceof Player player) {
			EntityEquipment equipment = player.getEquipment();
			if (equipment.getItemInMainHand().isEmpty()) {
				equipment.setItemInMainHand(item);
				return;
			}
			if (equipment.getItemInOffHand().isEmpty()) {
				equipment.setItemInOffHand(item);
				return;
			}
		}
		addOrDrop(entity, item, false);
	}

	/**
	 * Adds items to entity's inventory, or
	 * drops if not enough space
	 *
	 * @param entity entity
	 * @param item item
	 * @param shiftClickOrder whether to use the shift click item ordering
	 */
	public static void addOrDrop(Entity entity, ItemStack item, boolean shiftClickOrder) {
		if (!(entity instanceof Player player)) {
			if (entity instanceof LivingEntity livingEntity && livingEntity.getEquipment() != null) {
				EntityEquipment equipment = livingEntity.getEquipment();
				if (equipment.getItemInMainHand().isEmpty()) {
					equipment.setItemInMainHand(item);
					return;
				}
				if (equipment.getItemInOffHand().isEmpty()) {
					equipment.setItemInOffHand(item);
					return;
				}
			}
			entity.getWorld().dropItemNaturally(entity.getLocation(), item);
			return;
		}
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

	public static void addExtraItemChecker(Function<Player, List<@Nullable ItemStack>> finder, BiConsumer<Player, UnaryOperator<ItemStack>> modifier) {
		EXTRA_ITEM_CHECKERS.add(new ExtraItemChecker(finder, modifier));
	}

	/**
	 * Stores inventory contents into nbt while preserving slot positions
	 *
	 * @param inventory inventory
	 * @param nbt nbt
	 */
	public static void storeSlotted(Inventory inventory, ReadWriteNBT nbt, @Nullable Predicate<ItemStack> storageCheck) {
		nbt.removeKey(STORED_SLOTTED_ITEMS_TAG);
		ReadWriteNBT itemsTag = nbt.getOrCreateCompound(STORED_SLOTTED_ITEMS_TAG);
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack item = inventory.getItem(slot);
			if (ItemStack.isEmpty(item)) continue;
			if (storageCheck != null && !storageCheck.test(item)) continue;

			itemsTag.setItemStack(String.valueOf(slot), item);
		}
	}

	/**
	 * Restores inventory contents from nbt
	 *
	 * @param inventory inventory
	 * @param nbt nbt
	 * @param onLeftover action on leftover items
	 */
	public static void restoreFromSlotted(@Nullable Inventory inventory, ReadWriteNBT nbt, Consumer<List<ItemStack>> onLeftover) {
		if (!nbt.hasTag(STORED_SLOTTED_ITEMS_TAG)) return;

		ReadWriteNBT itemsTag = nbt.getCompound(STORED_SLOTTED_ITEMS_TAG);
		if (itemsTag == null) return;

		List<ItemStack> leftovers = null;
		int inventorySize = inventory == null ? 0 : inventory.getSize();
		for (String key : itemsTag.getKeys()) {
			int slot;
			try {
				slot = Integer.parseInt(key);
			} catch (NumberFormatException ignored) {
				continue;
			}

			ItemStack item = itemsTag.getItemStack(key);
			if (ItemStack.isEmpty(item)) continue;

			if (slot < 0 || slot >= inventorySize || !ItemStack.isEmpty(inventory.getItem(slot))) {
				if (leftovers == null) leftovers = new ArrayList<>();
				leftovers.add(item);
			} else {
				inventory.setItem(slot, item);
			}
		}

		if (leftovers != null)
			onLeftover.accept(leftovers);
	}

	/**
	 * Stores extra (non-slotted) items into stored slotted inventory
	 *
	 * @param nbt nbt
	 * @param items items
	 */
	public static void storeSlottedExtra(ReadWriteNBT nbt, Collection<ItemStack> items) {
		ReadWriteNBT itemsTag = nbt.getOrCreateCompound(STORED_SLOTTED_ITEMS_TAG);
		int extraSlot = -1;
		for (ItemStack item : items) {
			if (ItemStack.isEmpty(item)) continue;

			while (nbt.hasTag(String.valueOf(extraSlot)))
				extraSlot--;

			itemsTag.setItemStack(String.valueOf(extraSlot), item);
		}
	}

}
