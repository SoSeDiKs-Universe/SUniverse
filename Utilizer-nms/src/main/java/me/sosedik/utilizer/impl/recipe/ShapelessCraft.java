package me.sosedik.utilizer.impl.recipe;

import me.sosedik.utilizer.api.recipe.ShapelessRecipeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jspecify.annotations.NullMarked;

/**
 * Builder for {@link ShapelessRecipe}
 */
@NullMarked
public final class ShapelessCraft extends ShapelessRecipeBuilder<ShapelessCraft> {

	private CraftingBookCategory category;

	public ShapelessCraft(ItemStack result, NamespacedKey key) {
		super(result, key);
		this.category = CraftingBookCategory.MISC;
	}

	@Override
	public ShapelessCraft register() {
		var recipe = new ShapelessRecipe(getKey(), getResult());

		recipe.setGroup(getGroup());
		recipe.setCategory(getCategory());
		recipe.setSpecial(isSpecial());

		getIngredients().forEach((key, value) -> {
			var ingredientChoice = new RecipeChoice.ExactChoice(value);
			ingredientChoice.setPredicate(new ItemPredicate(this, key));
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
	public CraftingBookCategory getCategory() {
		return this.category;
	}

	/**
	 * Sets the recipe's crafting book category
	 *
	 * @param category the recipe's crafting book category
	 * @return this builder
	 */
	public ShapelessCraft withCategory(CraftingBookCategory category) {
		this.category = category;
		return builder();
	}

}
