package me.sosedik.delightfulfarming.listener.block;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Berries are placed using pips
 */
public class NoBerriesPlacement implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		if (item.getType() != Material.SWEET_BERRIES && item.getType() != Material.GLOW_BERRIES) return;

		event.setCancelled(true);
	}

}
