package me.sosedik.utilizer.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * General utilities around items
 */
// MCCheck: 1.21.4, item types
public class ItemUtil {

	private ItemUtil() {
		throw new IllegalStateException("Utility class");
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
	 * Tries to get items tag
	 *
	 * @param key key
	 * @return items tag
	 */
	public static @NotNull Tag<Material> blockTag(@NotNull NamespacedKey key) {
		return Objects.requireNonNull(Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class));
	}

	/**
	 * Checks whether the item is a water bottle
	 *
	 * @param item item
	 * @return whether the item is a water bottle
	 */
	public static boolean isWaterBottle(@NotNull ItemStack item) {
		if (item.getType() != Material.POTION) return false;
		if (!item.hasData(DataComponentTypes.POTION_CONTENTS)) return false;

		PotionContents potionContents = item.getData(DataComponentTypes.POTION_CONTENTS);
		assert potionContents != null;
		return potionContents.potion() == PotionType.WATER;
	}

	/**
	 * Checks whether item is considered hot
	 *
	 * @param item item
	 * @return whether item is a light source
	 */
	public static boolean isHot(@NotNull ItemStack item) {
		return UtilizerTags.HOT_ITEMS.isTagged(item.getType()) || isLitCampfire(item);
	}

	/**
	 * Checks whether item is a light source
	 *
	 * @param item item
	 * @return whether item is a light source
	 */
	public static boolean isLightSource(@NotNull ItemStack item) {
		if (item.hasEnchants()) return true;
		return UtilizerTags.LIGHT_SOURCES.isTagged(item.getType()) || isLitCampfire(item);
	}

	private static boolean isLitCampfire(@NotNull ItemStack item) {
		return Tag.CAMPFIRES.isTagged(item.getType())
				&& item.hasItemMeta()
				&& item.getItemMeta() instanceof BlockStateMeta meta
				&& meta.hasBlockState()
				&& meta.getBlockState().getBlockData() instanceof Campfire campfire
				&& campfire.isLit();
	}

}
