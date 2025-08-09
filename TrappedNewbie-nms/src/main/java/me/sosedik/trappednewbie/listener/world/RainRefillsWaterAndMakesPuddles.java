package me.sosedik.trappednewbie.listener.world;

import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.api.math.WorldChunkPosition;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Rain slowly refills water and creates puddles
 */
@NullMarked
public class RainRefillsWaterAndMakesPuddles implements Listener {

	private static final Random RANDOM = new Random();
	private static final Set<UUID> REFILLING_WORLDS = new HashSet<>();
	private static final Map<UUID, Map<WorldChunkPosition, Set<BlockPosition>>> PUDDLES = new HashMap<>();
	private static final int PUDDLE_LEVEL = 7;

	public RainRefillsWaterAndMakesPuddles() {
		Bukkit.getWorlds().forEach(world -> {
			if (world.hasStorm())
				goThroughWorld(world);
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWeatherChange(WeatherChangeEvent event) {
		World world = event.getWorld();
		if (!event.toWeatherState()) {
			UUID worldUuid = world.getUID();
			TrappedNewbie.scheduler().sync(() -> {
				Map<WorldChunkPosition, Set<BlockPosition>> worldPosition = PUDDLES.get(worldUuid);
				if (worldPosition == null) return;

				worldPosition.forEach((chunkLoc, blockLocs) -> removePuddles(worldUuid, chunkLoc, false));
			}, 8 * 20L);
			return;
		}

		goThroughWorld(world);
	}

	private void goThroughWorld(World world) {
		if (world.getEnvironment() != World.Environment.NORMAL) return;
		if (!REFILLING_WORLDS.add(world.getUID())) return;

		var refillTask = new RainRefillTask(world);
		TrappedNewbie.scheduler().sync(refillTask, 5 * 20L, 2L);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChunkUnload(ChunkUnloadEvent event) {
		UUID worldUuid = event.getWorld().getUID();
		Map<WorldChunkPosition, Set<BlockPosition>> worldPosition = PUDDLES.get(worldUuid);
		if (worldPosition == null) return;

		if (worldPosition.isEmpty()) {
			PUDDLES.remove(worldUuid);
			return;
		}

		var chunkLoc = WorldChunkPosition.of(event.getChunk());
		Set<BlockPosition> blockLocs = worldPosition.get(chunkLoc);
		if (blockLocs == null) return;

		if (blockLocs.isEmpty()) {
			worldPosition.remove(chunkLoc);
			return;
		}

		removePuddles(worldUuid, chunkLoc, true);
	}

	/**
	 * Checks whether this block is a known rain puddle
	 *
	 * @param block block
	 * @return whether this block is a rain puddle
	 */
	public static boolean isWaterPuddle(Block block) {
		if (block.getType() != Material.WATER) return false;
		if (!(block.getBlockData() instanceof Levelled levelled)) return false;
		if (levelled.getLevel() != PUDDLE_LEVEL) return false;

		Map<WorldChunkPosition, Set<BlockPosition>> worldPosition = PUDDLES.get(block.getWorld().getUID());
		if (worldPosition == null) return false;

		Location location = block.getLocation();
		Set<BlockPosition> blockLocs = worldPosition.get(WorldChunkPosition.of(location));
		if (blockLocs == null) return false;

		return blockLocs.contains(Position.block(location));
	}

	/**
	 * Removes all created puddles
	 */
	public static void removeAllPuddles() {
		PUDDLES.forEach((worldUuid, worldPosition) -> {
			Map<WorldChunkPosition, Set<BlockPosition>> worldPositionTemp = new HashMap<>(worldPosition);
			worldPositionTemp.forEach((chunkLoc, blockLocs) -> removePuddles(worldUuid, chunkLoc, true));
		});
		PUDDLES.clear();
	}

	private static void removePuddles(UUID worldUuid, WorldChunkPosition chunkLoc, boolean force) {
		if (!chunkLoc.isLoaded()) return;

		Map<WorldChunkPosition, Set<BlockPosition>> worldPosition = PUDDLES.get(worldUuid);
		if (worldPosition == null) return;

		Set<BlockPosition> blockLocs = worldPosition.get(chunkLoc);
		if (blockLocs == null) return;

		blockLocs = new HashSet<>(blockLocs);

		for (BlockPosition blockLoc : blockLocs) {
			if (force) {
				removePuddle(worldUuid, chunkLoc, blockLoc);
			} else {
				TrappedNewbie.scheduler().sync(() -> removePuddle(worldUuid, chunkLoc, blockLoc), 20L + RANDOM.nextInt(16 * 20));
			}
		}
	}

	/**
	 * Removes puddle if it exists
	 *
	 * @param block block
	 */
	public static void removePuddle(Block block) {
		removePuddle(block.getWorld().getUID(), WorldChunkPosition.of(block.getLocation()), Position.block(block.getLocation()));
	}

	private static void removePuddle(UUID worldUuid, WorldChunkPosition chunkLoc, BlockPosition blockLoc) {
		if (!chunkLoc.isLoaded()) return;

		Map<WorldChunkPosition, Set<BlockPosition>> worldPosition = PUDDLES.get(worldUuid);
		if (worldPosition == null) return;

		Set<BlockPosition> blockLocs = worldPosition.get(chunkLoc);
		if (blockLocs == null) return;
		if (!blockLocs.remove(blockLoc)) return;

		if (blockLocs.isEmpty())
			worldPosition.remove(chunkLoc);

		Location loc = blockLoc.toLocation(chunkLoc.world());
		Block block = loc.getBlock();
		if (block.getType() != Material.WATER) return;
		if (!(block.getBlockData() instanceof Levelled levelled)) return;
		if (levelled.getLevel() != PUDDLE_LEVEL) return;

		block.setType(Material.AIR);
	}

	/**
	 * Task to refill water and create puddles
	 */
	public static class RainRefillTask extends BukkitRunnable {

		private long lastPuddleTry = -1L;

		private final World world;
		private List<Chunk> pendingChunks;
		private int workedChunks;
		private int maxWorkedChunks;

		public RainRefillTask(World world) {
			this.world = world;
			refreshLoadedChunks();
		}

		@Override
		public void run() {
			if (!this.world.hasStorm()) {
				cancel();
				REFILLING_WORLDS.remove(this.world.getUID());
				return;
			}

			long stop = System.currentTimeMillis() + 2;
			while (stop > System.currentTimeMillis() && Bukkit.getCurrentTick() % 20 < 8) {
				if (this.pendingChunks.isEmpty()) {
					refreshLoadedChunks();
					return;
				}
				if (++this.workedChunks == this.maxWorkedChunks) {
					refreshLoadedChunks();
					continue;
				}
				if (RANDOM.nextDouble() > 0.4) continue;

				Chunk nextChunk = this.pendingChunks.remove(RANDOM.nextInt(this.pendingChunks.size()));
				if (!nextChunk.isLoaded()) continue;

				goThroughChunk(nextChunk);
			}
		}

		/**
		 * Tries to refill water or create a puddle in the chunk
		 *
		 * @param chunk chunk
		 */
		public void goThroughChunk(Chunk chunk) {
			Block highestBlock = this.world.getHighestBlockAt(
					(chunk.getX() << 4) + RANDOM.nextInt(16),
					(chunk.getZ() << 4) + RANDOM.nextInt(16),
					HeightMap.MOTION_BLOCKING_NO_LEAVES);
			if (highestBlock.getTemperature() > 0.95) return; // Too hot
			if (highestBlock.getTemperature() < 0.15) return; // Snowy

			if (highestBlock.getType() != Material.WATER) {
				if (tryToCreatePuddle(highestBlock))
					return; // Puddles can't be located near water
			}

			// Increase the chances of filling by checking a 3x3 square
			if (!tryToRefillWaterLevel(highestBlock)) {
				for (BlockFace blockFace : LocationUtil.SURROUNDING_BLOCKS) {
					Block relativeBlock = highestBlock.getRelative(blockFace);
					if (!relativeBlock.getChunk().isLoaded()) continue;
					if (tryToRefillWaterLevel(relativeBlock))
						return;
				}
			}
		}

		private boolean tryToCreatePuddle(Block block) {
			if (!block.isSolid()) return false;
			if (!canHoldPuddle(block)) return false;

			block = block.getRelative(BlockFace.UP);
			if (!block.isEmpty()) return false;

			for (Block nearbyBlock : LocationUtil.getBlocksAround(block, 1, 1)) {
				if (nearbyBlock.isLiquid())
					return false;
			}

			long currentTimeMs = System.currentTimeMillis();
			if (currentTimeMs - this.lastPuddleTry < (this.world.isThundering() ? 30 : 100)) return true;

			this.lastPuddleTry = currentTimeMs;

			Location loc = block.getLocation();
			PUDDLES.computeIfAbsent(loc.getWorld().getUID(), k -> new HashMap<>()).computeIfAbsent(WorldChunkPosition.of(loc), k -> new HashSet<>()).add(Position.block(loc));
			block.setType(Material.WATER);
			if (block.getBlockData() instanceof Levelled levelled) {
				levelled.setLevel(PUDDLE_LEVEL);
				block.setBlockData(levelled, false); // We don't want puddle to immediately erase
			}
			return true;
		}

		private boolean canHoldPuddle(Block block) {
			if (!LocationUtil.isCube(block)) return false;
			if (block.getBlockData() instanceof Stairs stairs)
				return stairs.getHalf() == Bisected.Half.TOP;
			return true;
		}

		private boolean tryToRefillWaterLevel(Block block) {
			if (block.getType() != Material.WATER) return false;
			if (!(block.getBlockData() instanceof Levelled levelled)) return false;
			if (levelled.getLevel() == 0) return false; // Source block
			if (levelled.getLevel() > PUDDLE_LEVEL) return false; // Flowing down water

			int sourcesNearby = 0;
			for (BlockFace blockFace : LocationUtil.SURROUNDING_BLOCKS_XZ) {
				Block nearbyBlock = block.getRelative(blockFace);
				if (nearbyBlock.getType() != Material.WATER) continue;
				if (!(nearbyBlock.getBlockData() instanceof Levelled nearbyLevelled)) continue;
				if (nearbyLevelled.getLevel() != 0) continue; // Not a source

				sourcesNearby++;
				if (sourcesNearby == 2) break;
			}

			if (sourcesNearby != 2) return false;

			levelled.setLevel(levelled.getLevel() - 1); // Water level is inverted
			block.setBlockData(levelled);
			return true;
		}

		private void refreshLoadedChunks() {
			this.pendingChunks = new ArrayList<>(List.of(this.world.getLoadedChunks()));
			this.workedChunks = 0;
			this.maxWorkedChunks = (int) (0.8 * this.pendingChunks.size());
		}

	}

}
