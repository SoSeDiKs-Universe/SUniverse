package me.sosedik.utilizer.util;

import me.sosedik.utilizer.Utilizer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

// MCCheck: 1.21.1, item types
public class ItemUtil {

	private ItemUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final Tag<Material> LIGHT_SOURCES = itemTag("light_sources");

	private static @NotNull Tag<Material> itemTag(@NotNull String key) {
		return itemTag(Utilizer.utilizerKey(key));
	}

	/**
	 * Tries to get items tag
	 *
	 * @param key key
	 * @return items tag
	 */
	public static @NotNull Tag<Material> itemTag(@NotNull NamespacedKey key) {
		return Objects.requireNonNull(Bukkit.getTag(Tag.REGISTRY_ITEMS, key, Material.class));
	}

	/**
	 * Checks whether item is a light source
	 *
	 * @param item item
	 * @return whether item is a light source
	 */
	public static boolean isLightSource(@NotNull ItemStack item) {
		if (item.hasEnchants()) return true;
		if (Tag.CAMPFIRES.isTagged(item.getType()) && item.getItemMeta() instanceof BlockStateMeta meta) {
			return meta.hasBlockState() && meta.getBlockState().getBlockData() instanceof Campfire campfire && campfire.isLit();
		}
		return LIGHT_SOURCES.isTagged(item.getType());
	}

}
