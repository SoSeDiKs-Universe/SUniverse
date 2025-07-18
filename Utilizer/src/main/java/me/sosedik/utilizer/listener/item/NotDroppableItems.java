package me.sosedik.utilizer.listener.item;

import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Some items shouldn't be dropped
 */
@NullMarked
public class NotDroppableItems implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onMove(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;

		Inventory clickedInventory = event.getClickedInventory();
		if (clickedInventory == null) return;

		ItemStack item;
		if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
			int slot = event.getHotbarButton();
			if (slot == -1) {
				if (event.getSlot() != event.getRawSlot()) return; // Player inv
				item = player.getInventory().getItemInOffHand();
			} else {
				if (event.getSlot() == event.getRawSlot()) return;
				item = player.getInventory().getItem(slot);
			}
		} else if (event.isShiftClick()) {
			item = event.getCurrentItem();
		} else {
			if (clickedInventory == player.getInventory()) return;
			item = event.getCursor();
		}
		if (ItemStack.isEmpty(item)) return;

		Material type = item.getType();
		if (UtilizerTags.NOT_DROPPABLE.isTagged(type))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onMove(InventoryDragEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;

		for (ItemStack item : event.getNewItems().values()) {
			Material type = item.getType();
			if (UtilizerTags.NOT_DROPPABLE.isTagged(type)) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		Material type = event.getItemDrop().getItemStack().getType();
		if (UtilizerTags.NOT_DROPPABLE.isTagged(type) && !UtilizerTags.MATERIAL_AIR.isTagged(type))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDrop(EntityDropItemEvent event) {
		Material type = event.getItemDrop().getItemStack().getType();
		if (UtilizerTags.NOT_DROPPABLE.isTagged(type) && !UtilizerTags.MATERIAL_AIR.isTagged(type))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(ItemSpawnEvent event) {
		if (UtilizerTags.NOT_DROPPABLE.isTagged(event.getEntity().getItemStack().getType()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(EntityDeathEvent event) {
		event.getDrops().removeIf(item -> UtilizerTags.NOT_DROPPABLE.isTagged(item.getType()));
	}

}
