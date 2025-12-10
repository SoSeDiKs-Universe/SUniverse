package me.sosedik.utilizer.util;

import io.papermc.paper.registry.keys.tags.BiomeTagKeys;
import org.bukkit.block.Biome;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// TODO should use vanilla tags, really
// MCCheck: 1.21.10, new biomes
@NullMarked
public class BiomeTags {

	public static final Collection<Biome> OCEAN = MiscUtil.getTagValues(BiomeTagKeys.IS_OCEAN);
	public static final Collection<Biome> RIVER = MiscUtil.getTagValues(BiomeTagKeys.IS_RIVER);
	public static final Collection<Biome> BADLANDS = MiscUtil.getTagValues(BiomeTagKeys.IS_BADLANDS);

	public static final Collection<Biome> EXTRA_WARM = builder()
		.add(Biome.DESERT)
		.add(Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.WINDSWEPT_SAVANNA)
		.add(Biome.BADLANDS, Biome.ERODED_BADLANDS, Biome.WOODED_BADLANDS)
		.build();

	public static final Collection<Biome> SNOWY = builder()
		.add(Biome.FROZEN_PEAKS, Biome.GROVE, Biome.JAGGED_PEAKS, Biome.SNOWY_PLAINS, Biome.ICE_SPIKES, Biome.SNOWY_SLOPES, Biome.SNOWY_TAIGA, Biome.SNOWY_BEACH)
		.add(Biome.FROZEN_RIVER, Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN)
		.build();

	public static final Collection<Biome> COLD = builder()
		.add(Biome.WINDSWEPT_HILLS, Biome.WINDSWEPT_GRAVELLY_HILLS, Biome.WINDSWEPT_FOREST, Biome.TAIGA, Biome.OLD_GROWTH_PINE_TAIGA, Biome.OLD_GROWTH_SPRUCE_TAIGA, Biome.STONY_SHORE)
		.build();

	public static TagBuilder builder() {
		return new TagBuilder();
	}

	public static class TagBuilder {

		private final Set<Biome> biomes = new HashSet<>();

		public TagBuilder add(Biome biome) {
			this.biomes.add(biome);
			return this;
		}

		public TagBuilder add(Biome... biomes) {
			this.biomes.addAll(Set.of(biomes));
			return this;
		}

		public TagBuilder addTag(Collection<Biome> biomes) {
			this.biomes.addAll(biomes);
			return this;
		}

		public Set<Biome> build() {
			return Collections.unmodifiableSet(this.biomes);
		}

	}

}
