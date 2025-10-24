package me.sosedik.utilizer.util;

import org.bukkit.block.Biome;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// TODO should use vanilla tags, really
@NullMarked
public class BiomeTags {

	public static final Set<Biome> DEEP_OCEAN = builder()
			.add(Biome.DEEP_FROZEN_OCEAN, Biome.DEEP_COLD_OCEAN, Biome.DEEP_OCEAN, Biome.DEEP_LUKEWARM_OCEAN).build();

	public static final Set<Biome> OCEAN = builder()
			.addTag(DEEP_OCEAN).add(Biome.FROZEN_OCEAN, Biome.OCEAN, Biome.COLD_OCEAN, Biome.LUKEWARM_OCEAN, Biome.WARM_OCEAN).build();

	public static final Set<Biome> EXTRA_WARM = builder()
			.add(Biome.DESERT)
			.add(Biome.SAVANNA, Biome.SAVANNA_PLATEAU, Biome.WINDSWEPT_SAVANNA)
			.add(Biome.BADLANDS, Biome.ERODED_BADLANDS, Biome.WOODED_BADLANDS).build();

	public static final Set<Biome> SNOWY = builder()
			.add(Biome.FROZEN_PEAKS, Biome.GROVE, Biome.JAGGED_PEAKS, Biome.SNOWY_PLAINS, Biome.ICE_SPIKES, Biome.SNOWY_SLOPES, Biome.SNOWY_TAIGA, Biome.SNOWY_BEACH)
			.add(Biome.FROZEN_RIVER, Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN).build();

	public static TagBuilder builder() {
		return new TagBuilder();
	}

	@SafeVarargs
	public static Set<Biome> ofTags(Set<Biome>... biomeTags) {
		TagBuilder builder = builder();
		for (Set<Biome> biomeSet : biomeTags) builder.addTag(biomeSet);
		return builder.build();
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
