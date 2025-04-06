package me.sosedik.utilizer.impl.recipe;

import me.sosedik.utilizer.api.recipe.ShapedRecipeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jspecify.annotations.NullMarked;

/**
 * Builder for {@link ShapedRecipe}
 */
@NullMarked
public final class ShapedCraft extends ShapedRecipeBuilder<ShapedCraft> {

	private CraftingBookCategory category;

	public ShapedCraft(ItemStack result, NamespacedKey key, String... shape) {
		super(result, key, shape);
		this.category = CraftingBookCategory.MISC;
	}

	@Override
	public ShapedCraft register() {
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
	public CraftingBookCategory getCategory() {
		return this.category;
	}

	/**
	 * Sets the recipe's crafting book category
	 *
	 * @param category the recipe's crafting book category
	 * @return this builder
	 */
	public ShapedCraft withCategory(CraftingBookCategory category) {
		this.category = category;
		return this;
	}

}
