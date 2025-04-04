package me.sosedik.trappednewbie.entity.api;

import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PaperPlane extends Projectile {

	String STORED_ITEMS_TAG = "stored_item";
	String STORED_FIREWORKS_TAG = "stored_item";
	String STORED_POTIONS_TAG = "stored_item";
	String BLAZIFIED_TAG = "blazified";
	String FRAGILE_TAG = "fragile";

	/**
	 * Gets the {@link ItemStack} for this projectile. This stack is used
	 * for both visuals on the projectile and the stack that could be picked up.
	 *
	 * @return The ItemStack, as if a player picked up the projectile
	 */
	ItemStack getItemStack();

	/**
	 * Sets the {@link ItemStack} for this projectile. This stack is used for both
	 * visuals on the projectile and the stack that could be picked up.
	 *
	 * @param stack the projectile item stack
	 */
	void setItemStack(ItemStack stack);

}
