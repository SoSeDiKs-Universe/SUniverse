package me.sosedik.utilizer.api.math;

import com.google.common.base.Preconditions;
import io.papermc.paper.math.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * Represents block position within a world
 */
public interface WorldBlockPosition extends BlockPosition {

	/**
	 * Gets the world
	 *
	 * @return world
	 */
	@NotNull World world();

	/**
	 * Creates a new location object
	 *
	 * @return a new location
	 */
	default @NotNull Location toLocation() {
		return toLocation(world());
	}

	/**
	 * Creates a world position from block's location
	 *
	 * @param block block
	 * @return a new world position
	 */
	static @NotNull WorldBlockPosition worldPosition(@NotNull Block block) {
		return worldPosition(block.getLocation());
	}

	/**
	 * Creates a world position from location
	 *
	 * @param loc location
	 * @return a new world position
	 */
	static @NotNull WorldBlockPosition worldPosition(@NotNull Location loc) {
		Preconditions.checkArgument(loc.getWorld() != null, "World can't be null");
		return worldPosition(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	/**
	 * Creates a world position from coordinates
	 *
	 * @param world world
	 * @param blockX block X coord
	 * @param blockY block Y coord
	 * @param blockZ block Z coord
	 * @return a new world position
	 */
	static @NotNull WorldBlockPosition worldPosition(@NotNull World world, int blockX, int blockY, int blockZ) {
		return new WorldBlockPositionImpl(world, blockX, blockY, blockZ);
	}

}
