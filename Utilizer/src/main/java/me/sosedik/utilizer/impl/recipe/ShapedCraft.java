package me.sosedik.utilizer.impl.recipe;

import me.sosedik.utilizer.api.recipe.ShapedRecipeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

/**
 * Builder for {@link ShapedRecipe}
 */
public final class ShapedCraft extends ShapedRecipeBuilder<ShapedCraft> {

	private CraftingBookCategory category;

	public ShapedCraft(@NotNull ItemStack result, @NotNull NamespacedKey key, @NotNull String... shape) {
		super(result, key, shape);
		this.category = CraftingBookCategory.MISC;
	}

	@Override
	public @NotNull ShapedCraft register() {
		var recipe = new ShapedRecipe(getKey(), getResult()).shape(getShape());

		recipe.setGroup(getGroup());
		recipe.setCategory(getCategory());
		recipe.setSpecial(isSpecial());

		getIngredients().forEach((key, value) -> {
			var ingredientChoice = new RecipeChoice.ExactChoice(value);
			ingredientChoice.setPredicate(item -> findMatch(key, item));
			recipe.setIngredient(key, ingredientChoice);
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
	public @NotNull ShapedCraft withCategory(@NotNull CraftingBookCategory category) {
		this.category = category;
		return this;
	}

}
