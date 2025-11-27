package me.sosedik.utilizer.api;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface AInventory extends InventoryHolder {

	default void onOpen(InventoryOpenEvent event) {}

	default void onClose(InventoryCloseEvent event) {}

}
