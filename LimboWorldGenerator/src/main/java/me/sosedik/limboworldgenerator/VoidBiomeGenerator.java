package me.sosedik.limboworldgenerator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Generates only void biomes
 */
public class VoidBiomeGenerator extends BiomeProvider {

	public static final VoidBiomeGenerator GENERATOR = new VoidBiomeGenerator();

	@Override
	public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
		return Biome.THE_VOID;
	}

	@Override
	public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
		return List.of(Biome.THE_VOID);
	}

}
