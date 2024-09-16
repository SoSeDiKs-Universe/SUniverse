package me.sosedik.utilizer.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.Nullable;

public class Durability {

	private Durability() {
		throw new IllegalStateException("Utility class");
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
	 * <br>Returns -1 if infinite / no durability
	 *
	 * @param item item
	 * @return durability value
	 */
	public static int getDurability(@Nullable ItemStack item) {
		if (item == null) return -1;
		if (!(item.getItemMeta() instanceof Damageable meta)) return -1;
		if (meta.isUnbreakable()) return -1;

		int maxDamage = meta.hasMaxDamage() ? meta.getMaxDamage() : item.getType().getMaxDurability();
		int damage = meta.hasDamage() ? meta.getDamage() : 0;
		int durability = maxDamage - damage;

		// It's possible to have more damage than max damage, treat as broken
		if (durability < 0)
			return 0;

		// Elytras are broken with 1 durability
		if (durability == 1 && item.getType() == Material.ELYTRA)
			return 0;

		return durability;
	}

}
