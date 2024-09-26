package me.sosedik.utilizer.api.recipe;

import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * Represents a custom recipe
 */
public interface CustomRecipe extends Recipe, Keyed {

	/**
	 * Checks the matrix to see whether the item can be crafted
	 *
	 * @param items matrix items
	 * @return whether the item can be crafted
	 */
	boolean checkMatrix(@Nullable ItemStack @NotNull [] items);

	/**
	 * Gets the ingredients used in the recipe
	 *
	 * @return the recipe's ingredients
	 */
	default @NotNull Map<Character, List<ItemStack>> getIngredients() {
		return Map.of();
	}

	/**
	 * Gets the recipe's group
	 *
	 * @return the recipe's group
	 */
	default @NotNull String getGroup() {
		return "";
	}

	@SuppressWarnings("deprecation")
	static @NotNull List<ItemStack> getFromChoice(@NotNull RecipeChoice recipeChoice) {
		return switch (recipeChoice) {
			case RecipeChoice.ExactChoice choice -> choice.getChoices();
			case RecipeChoice.MaterialChoice choice -> choice.getChoices().stream().map(ItemStack::new).toList();
			default -> List.of(recipeChoice.getItemStack());
		};
	}

}
