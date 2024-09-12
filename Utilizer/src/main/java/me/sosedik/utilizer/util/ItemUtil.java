package me.sosedik.utilizer.util;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

// MCCheck: 1.21.1, item types
public class ItemUtil {

	private ItemUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final Set<Material> ITEM_LIGHT_SOURCES = new HashSet<>(); // TODO should be datapack tag

	static {
		ITEM_LIGHT_SOURCES.addAll(Set.of(
			Material.TORCH, Material.SOUL_TORCH, Material.REDSTONE_TORCH,
			Material.LANTERN, Material.SOUL_LANTERN, Material.SEA_LANTERN,
			Material.GLOWSTONE, Material.JACK_O_LANTERN,
			Material.MAGMA_BLOCK,
			Material.LAVA_BUCKET,
			Material.BLAZE_ROD, Material.GLOW_BERRIES
		));
	}

	/**
	 * Adds item light sources
	 *
	 * @param types item types
	 */
	public static void addItemLightSources(@NotNull Material @NotNull ... types) {
		ITEM_LIGHT_SOURCES.addAll(Set.of(types));
	}

	/**
	 * Checks whether item is a light source
	 *
	 * @param type type
	 * @return whether item is a light source
	 */
	public static boolean isLightSource(@NotNull Material type) {
		return ITEM_LIGHT_SOURCES.contains(type);
	}

}
