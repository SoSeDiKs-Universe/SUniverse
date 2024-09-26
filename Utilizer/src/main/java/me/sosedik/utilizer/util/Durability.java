package me.sosedik.utilizer.util;

import com.destroystokyo.paper.MaterialTags;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Durability {

	private Durability() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Reduce item's durability by 1.
	 * Accounts for {@link Enchantment#UNBREAKING} enchantment.
	 *
	 * @param item item
	 * @return damaged item or null if broken
	 */
	public static @Nullable ItemStack damageItem(@Nullable ItemStack item) {
		return damageItem(item, 1);
	}

	/**
	 * Reduce item's durability by specified amount.
	 * Accounts for {@link Enchantment#UNBREAKING} enchantment.
	 *
	 * @param item   item
	 * @param damage damage
	 * @return damaged item or null if broken
	 */
	public static @Nullable ItemStack damageItem(@Nullable ItemStack item, int damage) {
		if (ItemStack.isEmpty(item)) return null;

		item = item.clone();
		if (damage == 0) return item;
		if (!(item.getItemMeta() instanceof Damageable meta)) return item;
		if (meta.isUnbreakable()) return item;

		if (damage > 0 && meta.hasEnchant(Enchantment.UNBREAKING)) {
			int unbreaking = item.getEnchantmentLevel(Enchantment.UNBREAKING);
			int uses = damage;
			double chance = MaterialTags.ALL_EQUIPPABLE.isTagged(item) ? 100D / (60D + (40D / (unbreaking + 1))) : 100D / (unbreaking + 1);
			for (int i = 0; i < uses; i++) {
				if (Math.random() > chance) damage--;
			}
			if (damage < 1) return item;
		}

		int maxDamage = meta.hasMaxDamage() ? meta.getMaxDamage() : item.getType().getMaxDurability();
		int newDamage = (meta.hasDamageValue() ? meta.getDamage() : 0) + damage;
		newDamage = Math.clamp(newDamage, 0, maxDamage);
		if (newDamage == 0) {
			meta.resetDamage();
		} else {
			meta.setDamage(newDamage);
		}
		item.setItemMeta(meta);

		return item;
	}

	/**
	 * Check whether item has durability
	 *
	 * @param item item
	 * @return whether item has durability
	 */
	public static boolean hasDurability(@NotNull ItemStack item) {
		if (ItemStack.isEmpty(item)) return false;
		if (item.getType().getMaxDurability() > 0) return true;
		if (!(item.getItemMeta() instanceof Damageable meta)) return false;
		return meta.hasMaxDamage() || meta.hasDamageValue();
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
