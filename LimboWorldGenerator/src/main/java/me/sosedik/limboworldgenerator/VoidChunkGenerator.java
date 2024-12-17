package me.sosedik.limboworldgenerator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Random;

/**
 * Generates a world of nothing but air
 */
@NullMarked
public class VoidChunkGenerator extends ChunkGenerator {

	public static final VoidChunkGenerator GENERATOR = new VoidChunkGenerator();

	@Override
	public @Nullable Location getFixedSpawnLocation(World world, Random random) {
		return new Location(world, 0, 120, 0);
	}

	public @Nullable BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo) {
		return VoidBiomeGenerator.GENERATOR;
	}

}
