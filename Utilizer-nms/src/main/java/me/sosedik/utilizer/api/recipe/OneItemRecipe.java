package me.sosedik.utilizer.api.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

@NullMarked
public abstract class OneItemRecipe<T extends OneItemRecipe<T>> extends ShapelessRecipeBuilder<T> {

	protected OneItemRecipe(ItemStack result, NamespacedKey key) {
		super(result, key);
	}

	protected RecipeChoice.ExactChoice getRecipeChoice() {
		for (Map.Entry<Character, List<ItemStack>> entry : getIngredients().entrySet()) {
			var ingredientChoice = new RecipeChoice.ExactChoice(entry.getValue());
			ingredientChoice.setPredicate(new ItemPredicate(this, entry.getKey()));
			return ingredientChoice;
		}
		throw new IllegalStateException("Recipe must have at least one ingredient");
	}

	@Override
	public boolean checkMatrix(@Nullable ItemStack[] items) {
		if (items.length != 1) return false;

		ItemStack inputItem = items[0];
		Map<Character, List<ItemStack>> requiredItems = getIngredients();
		for (Map.Entry<Character, List<ItemStack>> entry : requiredItems.entrySet()) {
			return findMatch(entry.getKey(), inputItem);
		}

		return false;
	}

}
