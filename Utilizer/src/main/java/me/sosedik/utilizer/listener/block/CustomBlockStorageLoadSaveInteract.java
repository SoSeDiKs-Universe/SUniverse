package me.sosedik.utilizer.listener.block;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import me.sosedik.utilizer.api.math.WorldChunkPosition;
import me.sosedik.utilizer.api.storage.block.BlockDataStorage;
import me.sosedik.utilizer.listener.BlockStorage;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handling custom block storage
 */
@NullMarked
public class CustomBlockStorageLoadSaveInteract implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChunkLoad(ChunkLoadEvent event) {
		BlockStorage.loadInfo(event.getChunk(), event.isNewChunk());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkUnload(ChunkUnloadEvent event) {
		BlockStorage.saveChunk(WorldChunkPosition.of(event.getChunk()), true, true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldUnload(WorldUnloadEvent event) {
		for (Chunk chunk : event.getWorld().getLoadedChunks())
			BlockStorage.saveChunk(WorldChunkPosition.of(chunk), true, true);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRetract(BlockPistonRetractEvent event) {
		handlePiston(event, event.getBlocks());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onExtend(BlockPistonExtendEvent event) {
		handlePiston(event, event.getBlocks());
	}

	private void handlePiston(BlockPistonEvent event, List<Block> blocks) {
		List<Location> needMoving = new ArrayList<>();
		for (int i = blocks.size() - 1; i >= 0; i--) {
			Block block = blocks.get(i);
			Location loc = block.getLocation();
			BlockDataStorage storage = BlockStorage.getByLoc(loc);
			if (storage == null) continue;

			needMoving.add(loc);
		}
		BlockStorage.moveData(needMoving, event.getDirection());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onExplode(BlockExplodeEvent event) {
		handleExplosion(event, event.blockList());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onExplode(EntityExplodeEvent event) {
		handleExplosion(event, event.blockList());
	}

	private void handleExplosion(Event event, List<Block> blocks) {
		Iterator<Block> iterator = blocks.iterator();
		while (iterator.hasNext()) {
			Block block = iterator.next();
			BlockDataStorage storage = BlockStorage.removeInfo(block.getLocation());
			if (storage == null) continue;

			if (storage.onExplode(event))
				iterator.remove();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBurn(BlockBurnEvent event) {
		BlockDataStorage storage = BlockStorage.removeInfo(event.getBlock().getLocation());
		if (storage == null) return;

		storage.onBurn(event);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		BlockDataStorage storage = BlockStorage.removeInfo(event.getBlock().getLocation());
		if (storage == null) return;

		storage.onBreak(event);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDestroy(BlockDestroyEvent event) {
		BlockDataStorage storage = BlockStorage.removeInfo(event.getBlock().getLocation());
		if (storage == null) return;

		storage.onDestroy(event);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		BlockDataStorage storage = BlockStorage.getByLoc(block);
		if (storage != null) return;

		BlockStorage.initBlock(block, event);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (block == null) return;

		BlockDataStorage storage = BlockStorage.getByLoc(block);
		if (storage == null) return;

		storage.onInteract(event);
	}

}
