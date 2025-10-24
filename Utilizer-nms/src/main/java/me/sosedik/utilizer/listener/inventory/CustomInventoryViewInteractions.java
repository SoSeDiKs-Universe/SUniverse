package me.sosedik.utilizer.listener.inventory;

import me.sosedik.utilizer.api.AInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Easier management for custom inventories
 */
@NullMarked
public class CustomInventoryViewInteractions implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onOpen(InventoryOpenEvent event) {
		if (!(event.getInventory().getHolder(false) instanceof AInventory gui)) return;

		gui.onOpen(event);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onClose(InventoryCloseEvent event) {
		if (!(event.getInventory().getHolder(false) instanceof AInventory gui)) return;

		gui.onClose(event);
	}

}
