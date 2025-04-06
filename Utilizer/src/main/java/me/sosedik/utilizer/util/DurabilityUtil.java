package me.sosedik.utilizer.util;

import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class DurabilityUtil {

	private DurabilityUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Check whether item has durability
	 *
	 * @param item item
	 * @return whether item has durability
	 */
	public static boolean hasDurability(@Nullable ItemStack item) {
		if (ItemStack.isEmpty(item)) return false;
		return item.hasData(DataComponentTypes.MAX_DAMAGE);
	}

	/**
	 * Checks whether item is broken
	 *
	 * @param item item
	 * @return true, if item has zero durability
	 */
	public static boolean isBroken(@Nullable ItemStack item) {
		return getDurability(item) == 0;
	}

	/**
	 * Returns durability of this item
	 * <br>Returns 0 if broken
	 * <br>Returns -1 if unbreakable or has no durability
	 *
	 * @param item item
	 * @return durability value
	 */
	public static int getDurability(@Nullable ItemStack item) {
		if (item == null) return -1;
		if (item.isUnbreakable()) return -1;
		if (!item.hasData(DataComponentTypes.DAMAGE)) return -1;
		if (!item.hasData(DataComponentTypes.MAX_DAMAGE)) return -1;

		int damage = Objects.requireNonNull(item.getData(DataComponentTypes.DAMAGE));
		int maxDamage = Objects.requireNonNull(item.getData(DataComponentTypes.MAX_DAMAGE));
		int durability = Math.max(0, maxDamage - damage);

		// Elytras are broken with 1 durability
		if (durability == 1 && item.getType() == Material.ELYTRA)
			return 0;

		return durability;
	}

}
