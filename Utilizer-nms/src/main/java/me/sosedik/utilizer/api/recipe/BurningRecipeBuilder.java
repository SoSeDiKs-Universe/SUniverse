package me.sosedik.utilizer.api.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a burning recipe, i.e, the one with time and exp reward
 *
 * @param <T> recipe type
 */
public abstract class BurningRecipeBuilder<T extends BurningRecipeBuilder<T>> extends OneItemRecipe<T> {

	private int exp;
	private int time;

	protected BurningRecipeBuilder(ItemStack result, int time, NamespacedKey key) {
		super(result, key);
		this.time = time;
	}

	/**
	 * Gets the exp reward for this recipe
	 *
	 * @return exp reward
	 */
	public int getExp() {
		return exp;
	}

	/**
	 * Sets the exp reward for this recipe
	 *
	 * @param exp exp reward
	 * @return this builder
	 */
	public T withExp(int exp) {
		this.exp = exp;
		return builder();
	}

	/**
	 * Gets the time in ticks it takes to cook/smelt this item
	 *
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * Sets the time in ticks in takes to cook/smelt this item
	 *
	 * @param time time in ticks
	 * @return this builder
	 */
	public T withTime(int time) {
		this.time = time;
		return builder();
	}

}
