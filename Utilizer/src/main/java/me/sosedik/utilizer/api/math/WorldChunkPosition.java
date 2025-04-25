package me.sosedik.utilizer.api.math;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a position for a chunk in a world
 *
 * @param world world
 * @param chunkX chunk X position
 * @param chunkZ chunk Z position
 */
@NullMarked
public record WorldChunkPosition(
	World world,
	int chunkX,
	int chunkZ
) {

	/**
	 * Gets the chunk at this position
	 *
	 * @return the chunk at this position
	 */
	public Chunk getChunk() {
		return this.world.getChunkAt(this.chunkX, this.chunkZ);
	}

	/**
	 * Checks whether this chunk is loaded
	 *
	 * @return whether this chunk is loaded
	 */
	public boolean isLoaded() {
		return this.world.isChunkLoaded(this.chunkX, this.chunkZ);
	}

	/**
	 * Create a new world chunk position from a location
	 *
	 * @param location location
	 * @return world chunk position
	 */
	public static WorldChunkPosition of(Location location) {
		return of(location.getChunk());
	}

	/**
	 * Create a new world chunk position from a chunk
	 *
	 * @param chunk chunk
	 * @return world chunk position
	 */
	public static WorldChunkPosition of(Chunk chunk) {
		return new WorldChunkPosition(chunk.getWorld(), chunk.getX(), chunk.getZ());
	}

}
