package me.sosedik.utilizer.impl.recipe;

import me.sosedik.utilizer.api.recipe.ShapelessRecipeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for {@link ShapelessRecipe}
 */
public final class ShapelessCraft extends ShapelessRecipeBuilder<ShapelessCraft> {

	private CraftingBookCategory category;

	public ShapelessCraft(@NotNull ItemStack result, @NotNull NamespacedKey key) {
		super(result, key);
		this.category = CraftingBookCategory.MISC;
	}

	@Override
	public @NotNull ShapelessCraft register() {
		var recipe = new ShapelessRecipe(getKey(), getResult());

		recipe.setGroup(getGroup());
		recipe.setCategory(getCategory());
		recipe.setSpecial(isSpecial());

		getIngredients().forEach((key, value) -> {
			var ingredientChoice = new RecipeChoice.ExactChoice(value);
			ingredientChoice.setPredicate(item -> findMatch(key, item));
			recipe.addIngredient(ingredientChoice);
		});

		Bukkit.addRecipe(recipe);
		return this;
	}

	/**
	 * Gets the recipe's crafting book category
	 *
	 * @return the recipe's crafting book category
	 */
	public @NotNull CraftingBookCategory getCategory() {
		return this.category;
	}

	/**
	 * Sets the recipe's crafting book category
	 *
	 * @param category the recipe's crafting book category
	 * @return this builder
	 */
	public @NotNull ShapelessCraft withCategory(@NotNull CraftingBookCategory category) {
		this.category = category;
		return this;
	}

}
