package me.sosedik.utilizer.api;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;

@NullMarked
public interface AInventory extends InventoryHolder {

	default void onOpen(InventoryOpenEvent event) {}

	default void onClose(InventoryCloseEvent event) {}

	default boolean isEmptyOr(@Nullable ItemStack item, Predicate<ItemStack> check) {
		return ItemStack.isEmpty(item) || check.test(item);
	}

	default ItemStack getOr(int slot, Supplier<ItemStack> supplier) {
		ItemStack item = getInventory().getItem(slot);
		return ItemStack.isEmpty(item) ? supplier.get() : item;
	}

	default ItemProvider emptyProvider(int slot, Supplier<ItemStack> supplier) {
		return new ItemProvider() {
			@Override
			public ItemStack get(Locale locale) {
				return get();
			}

			@Override
			public ItemStack get() {
				return getOr(slot, supplier);
			}
		};
	}

}
