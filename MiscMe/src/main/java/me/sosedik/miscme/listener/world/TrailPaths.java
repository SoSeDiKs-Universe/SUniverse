package me.sosedik.miscme.listener.world;

import me.sosedik.miscme.MiscMe;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Generates trails where players walk
 */
public class TrailPaths extends BukkitRunnable implements Listener {

	private final Map<UUID, Location> storedLocations = new HashMap<>();
	private final Map<UUID, Map<Location, Integer>> storedSteps = new HashMap<>();

	public TrailPaths() {
		MiscMe.scheduler().async(this, 5L, 5L);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuit(PlayerQuitEvent event) {
		this.storedLocations.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onUnload(WorldUnloadEvent event) {
		this.storedSteps.remove(event.getWorld().getUID());
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers())
			checkPaths(player);
	}

	private void checkPaths(Player player) {
		if (player.getGameMode().isInvulnerable()) return;
		if (player.isSneaking()) return;
		if (player.isSwimming()) return;

		Location playerLocation = player.getLocation();
		Location oldLocation = this.storedLocations.computeIfAbsent(player.getUniqueId(), k -> playerLocation);
		if (oldLocation.getWorld() == playerLocation.getWorld() && oldLocation.isBlockSame(playerLocation)) return;

		MiscMe.scheduler().sync(() -> makePath(oldLocation));
		this.storedLocations.replace(player.getUniqueId(), playerLocation);
	}

	private void makePath(Location oldLoc) {
		if (!oldLoc.isWorldLoaded()) return;
		if (!oldLoc.isChunkLoaded()) return;

		int relY = oldLoc.getBlockY() & 0xFF;
		if (relY <= oldLoc.getWorld().getMinHeight()) return;
		if (relY >= oldLoc.getWorld().getMaxHeight()) return;

		int relX = oldLoc.getBlockX() & 0xF;
		int relZ = oldLoc.getBlockZ() & 0xF;
		ChunkSnapshot chunkSnapshot = oldLoc.getChunk().getChunkSnapshot(false, false, false, false);
		Material blockType = chunkSnapshot.getBlockType(relX, relY, relZ);
		if (blockType.isSolid()) return;

		Map<Location, Integer> storedSteps = this.storedSteps.computeIfAbsent(oldLoc.getWorld().getUID(), k -> new HashMap<>());
		blockType = chunkSnapshot.getBlockType(relX, relY - 1, relZ);
		for (Trail trail : Trail.values()) {
			if (trail.getFrom() != blockType) continue;
			if (!trail.shouldConvert()) return;

			for (Map.Entry<Location, Integer> entry : storedSteps.entrySet()) {
				Location loc = entry.getKey();
				if (oldLoc.getWorld() == loc.getWorld() && oldLoc.isBlockSame(loc)) {
					int walked = entry.getValue();
					if (walked < trail.getSteps()) {
						storedSteps.replace(loc, walked + 1);
						return;
					}

					MiscMe.scheduler().sync(() -> {
						Block block = loc.getBlock().getRelative(BlockFace.DOWN);
						if (block.getType() == trail.getFrom())
							block.setType(trail.getTo());
					});
					storedSteps.remove(loc);
					return;
				}
			}
			storedSteps.put(oldLoc, 1);
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
