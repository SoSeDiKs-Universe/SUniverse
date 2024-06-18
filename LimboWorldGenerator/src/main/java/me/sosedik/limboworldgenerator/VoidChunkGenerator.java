package me.sosedik.limboworldgenerator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Generates a world of nothing but air
 */
public class VoidChunkGenerator extends ChunkGenerator {

	public static final VoidChunkGenerator GENERATOR = new VoidChunkGenerator();

	@Override
	public @Nullable Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
		return new Location(world, 0, 120, 0);
	}

	@Nullable
	public BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
		return VoidBiomeGenerator.GENERATOR;
	}

}
