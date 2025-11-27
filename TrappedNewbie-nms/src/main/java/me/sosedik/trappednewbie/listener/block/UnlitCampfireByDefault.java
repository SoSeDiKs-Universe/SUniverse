package me.sosedik.trappednewbie.listener.block;

import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Campfires are placed unlit by default
 */
public class UnlitCampfireByDefault implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		if (!Tag.CAMPFIRES.isTagged(item.getType())) return;

		if (item.hasBlockData()) {
			BlockData blockData = item.getBlockData(item.getType());
			if (blockData instanceof Lightable lightable && lightable.isLit()) return;
		}

		Block block = event.getBlockPlaced();
		if (!Tag.CAMPFIRES.isTagged(block.getType())) return;
		if (!(block.getBlockData() instanceof Lightable lightable)) return;
		if (!lightable.isLit()) return;

		lightable.setLit(false);
		block.setBlockData(lightable);
	}

}
