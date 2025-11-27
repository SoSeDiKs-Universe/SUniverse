package me.sosedik.resourcelib.listener.block;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import me.sosedik.resourcelib.ResourceLib;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Resend chunks when custom block with light level changes in the world
 */
public class RefreshCustomBlockLightning implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		if (!block.getType().isInjected()) return;
		if (block.getBlockData().getLightEmission() == 0) return;

		refreshChunks(block.getChunk());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (!block.getType().isInjected()) return;
		if (block.getBlockData().getLightEmission() == 0) return;

		refreshChunks(block.getChunk());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDestroy(BlockDestroyEvent event) {
		Block block = event.getBlock();
		if (!block.getType().isInjected()) return;
		if (block.getBlockData().getLightEmission() == 0) return;

		refreshChunks(block.getChunk());
	}

	private void refreshChunks(Chunk chunk) {
		World world = chunk.getWorld();
		int chunkX = chunk.getX();
		int chunkZ = chunk.getZ();
		for (int xOffset = -1; xOffset < 2; xOffset++) {
			for (int zOffset = -1; zOffset < 2; zOffset++) {
				int x = chunkX + xOffset;
				int z = chunkZ + zOffset;
				ResourceLib.scheduler().sync(() -> world.refreshChunk(x, z), 1);
			}
		}
	}

}
