package me.sosedik.utilizer.impl.recipe;

import me.sosedik.utilizer.api.recipe.BurningRecipeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.jspecify.annotations.NullMarked;

/**
 * Builder for {@link BlastingRecipe}
 */
@NullMarked
public class BlastingCraft extends BurningRecipeBuilder<BlastingCraft> {

	private CookingBookCategory category;

	public BlastingCraft(ItemStack result, int time, NamespacedKey key) {
		super(result, time, new NamespacedKey(key.getNamespace(), key.value() + "_from_blasting"));
		this.category = CookingBookCategory.MISC;
	}

	@Override
	public BlastingCraft register() {
		var recipe = new BlastingRecipe(getKey(), getResult(), getRecipeChoice(), getExp(), getTime());
		recipe.setGroup(getGroup());
		recipe.setCategory(getCategory());
		recipe.setSpecial(isSpecial());
		Bukkit.addRecipe(recipe);
		return this;
	}

	/**
	 * Gets the recipe's crafting book category
	 *
	 * @return the recipe's crafting book category
	 */
	public CookingBookCategory getCategory() {
		return this.category;
	}

	/**
	 * Sets the recipe's crafting book category
	 *
	 * @param category the recipe's crafting book category
	 * @return this builder
	 */
	public BlastingCraft withCategory(CookingBookCategory category) {
		this.category = category;
		return builder();
	}

}
