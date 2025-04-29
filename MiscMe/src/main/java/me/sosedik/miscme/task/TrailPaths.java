package me.sosedik.miscme.task;

import me.sosedik.miscme.MiscMe;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Generates trails where players walk
 */
public class TrailPaths extends BukkitRunnable {

	private final Map<UUID, Location> storedLocations = new HashMap<>();
	private final Map<Location, Integer> storedSteps = new HashMap<>();

	public TrailPaths() {
		MiscMe.scheduler().async(this, 5L, 5L);
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getServer().getOnlinePlayers())
			checkPaths(player);
	}

	private void checkPaths(Player player) {
		if (player.getGameMode().isInvulnerable()) return;
		if (player.isSneaking()) return;
		if (player.isSwimming()) return;

		Location playerLocation = player.getLocation();
		Location oldLocation = this.storedLocations.computeIfAbsent(player.getUniqueId(), k -> playerLocation);
		if (oldLocation.getWorld() == playerLocation.getWorld() && oldLocation.isBlockSame(playerLocation)) return;

		makePath(oldLocation);
		this.storedLocations.replace(player.getUniqueId(), playerLocation);
	}

	private void makePath(Location oldLoc) {
		int relY = oldLoc.getBlockY() & 0xFF;
		if (relY < oldLoc.getWorld().getMinHeight()) return;
		if (relY > oldLoc.getWorld().getMaxHeight()) return;

		int relX = oldLoc.getBlockX() & 0xF;
		int relZ = oldLoc.getBlockZ() & 0xF;
		ChunkSnapshot chunkSnapshot = oldLoc.getChunk().getChunkSnapshot(false, false, false, false);
		Material blockType = chunkSnapshot.getBlockType(relX, relY, relZ);
		if (blockType.isSolid()) return;

		blockType = chunkSnapshot.getBlockType(relX, relY - 1, relZ);
		for (Trail trail : Trail.values()) {
			if (trail.getFrom() != blockType) continue;
			if (!trail.shouldConvert()) return;

			for (Map.Entry<Location, Integer> entry : this.storedSteps.entrySet()) {
				Location loc = entry.getKey();
				if (oldLoc.getWorld() == loc.getWorld() && oldLoc.isBlockSame(loc)) {
					int walked = entry.getValue();
					if (walked < trail.getSteps()) {
						this.storedSteps.replace(loc, walked + 1);
						return;
					}

					MiscMe.scheduler().sync(() -> {
						Block block = loc.getBlock().getRelative(BlockFace.DOWN);
						if (block.getType() == trail.getFrom())
							block.setType(trail.getTo());
					});
					this.storedSteps.remove(loc);
					return;
				}
			}
			this.storedSteps.put(oldLoc, 1);
			return;
		}
	}

	public enum Trail {

		GRASS_BLOCK(Material.GRASS_BLOCK, Material.DIRT, 9, 0.9),
		PODZOL(Material.PODZOL, Material.DIRT, 10, 0.9),
		DIRT(Material.DIRT, Material.COARSE_DIRT, 13, 1),
		COARSE_DIRT(Material.COARSE_DIRT, Material.GRAVEL, 11, 1),
		GRAVEL(Material.GRAVEL, Material.CRACKED_STONE_BRICKS, 30, 0.8),
		SAND(Material.SAND, Material.SANDSTONE, 30, 0.8),
		RED_SAND(Material.RED_SAND, Material.RED_SANDSTONE, 30, 0.8);

		private final Material from;
		private final Material to;
		private final int steps;
		private final double chance;

		Trail(Material from, Material to, int steps, double chance) {
			this.from = from;
			this.to = to;
			this.steps = steps;
			this.chance = chance;
		}

		public Material getFrom() {
			return from;
		}

		public Material getTo() {
			return to;
		}

		public int getSteps() {
			return steps;
		}

		public boolean shouldConvert() {
			return chance == 1 || Math.random() < chance;
		}

	}

}
