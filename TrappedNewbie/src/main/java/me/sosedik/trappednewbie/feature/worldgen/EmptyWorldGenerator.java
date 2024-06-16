package me.sosedik.trappednewbie.feature.worldgen;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Generates a world of nothing but air
 */
public class EmptyWorldGenerator extends ChunkGenerator {

	public static final EmptyWorldGenerator GENERATOR = new EmptyWorldGenerator();

	@Override
	public @Nullable Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
		return new Location(world, 0, 120, 0);
	}

}
