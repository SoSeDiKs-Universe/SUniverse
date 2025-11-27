package me.sosedik.limboworldgenerator;

import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Generates only void biomes
 */
@NullMarked
public class VoidBiomeGenerator extends BiomeProvider {

	public static final VoidBiomeGenerator GENERATOR = new VoidBiomeGenerator();

	@Override
	public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
		return Biome.THE_VOID;
	}

	@Override
	public List<Biome> getBiomes(WorldInfo worldInfo) {
		return List.of(Biome.THE_VOID);
	}

}
