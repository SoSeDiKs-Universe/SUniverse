package me.sosedik.utilizer.util;

import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// TODO should use vanilla tags, really
public class BiomeTags {

	public static final Set<Biome> DEEP_OCEAN = builder()
			.add(Biome.DEEP_FROZEN_OCEAN).add(Biome.DEEP_COLD_OCEAN).add(Biome.DEEP_OCEAN).add(Biome.DEEP_LUKEWARM_OCEAN).build();

	public static final Set<Biome> OCEAN = builder()
			.addTag(DEEP_OCEAN).add(Biome.FROZEN_OCEAN).add(Biome.OCEAN).add(Biome.COLD_OCEAN).add(Biome.LUKEWARM_OCEAN).add(Biome.WARM_OCEAN).build();

	public static @NotNull TagBuilder builder() {
		return new TagBuilder();
	}

	@SafeVarargs
	public static @NotNull Set<@NotNull Biome> ofTags(@NotNull Set<Biome>... biomeTags) {
		TagBuilder builder = builder();
		for (Set<Biome> biomeSet : biomeTags) builder.addTag(biomeSet);
		return builder.build();
	}

	public static class TagBuilder {

		private final Set<Biome> biomes = new HashSet<>();

		public @NotNull TagBuilder add(@NotNull Biome biome) {
			this.biomes.add(biome);
			return this;
		}

		public @NotNull TagBuilder add(@NotNull Biome... biomes) {
			this.biomes.addAll(Set.of(biomes));
			return this;
		}

		public @NotNull TagBuilder addTag(@NotNull Collection<@NotNull Biome> biomes) {
			this.biomes.addAll(biomes);
			return this;
		}

		public @NotNull Set<Biome> build() {
			return Collections.unmodifiableSet(this.biomes);
		}

	}

}
