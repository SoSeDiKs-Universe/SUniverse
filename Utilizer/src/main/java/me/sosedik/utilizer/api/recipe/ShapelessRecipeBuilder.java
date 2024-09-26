package me.sosedik.utilizer.api.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Basic shapeless recipe builder
 * 
 * @param <T> recipe class
 */
public abstract class ShapelessRecipeBuilder<T extends ShapelessRecipeBuilder<T>> extends CraftingRecipeBuilder<T> {

	private char key = 'Z';

	protected ShapelessRecipeBuilder(@NotNull ItemStack result, @NotNull NamespacedKey key) {
		super(result, key);
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredient ingredient
	 * @param amount the amount of ingredients
	 * @return this builder
	 */
	public @NotNull T addIngredients(@NotNull Material ingredient, int amount) {
		return addIngredients(ingredient, amount, null);
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredient ingredient
	 * @param amount the amount of ingredients
	 * @return this builder
	 */
	public @NotNull T addIngredients(@NotNull Material ingredient, int amount, @Nullable Predicate<ItemStack> validator) {
		for (int i = 0; i < amount; i++)
			addIngredients(ingredient, validator);
		return builder();
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredient ingredient
	 * @param amount the amount of ingredients
	 * @return this builder
	 */
	public @NotNull T addIngredientItems(@NotNull ItemStack ingredient, int amount) {
		return addIngredientItems(ingredient, amount, null);
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredient ingredient
	 * @param amount the amount of ingredients
	 * @return this builder
	 */
	public @NotNull T addIngredientItems(@NotNull ItemStack ingredient, int amount, @Nullable Predicate<ItemStack> validator) {
		for (int i = 0; i < amount; i++)
			addIngredientItems(ingredient, validator);
		return builder();
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredient ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredients(@NotNull Material ingredient, @Nullable Predicate<ItemStack> validator) {
		return addIngredientItems(ItemStack.of(ingredient), validator);
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredient ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredientItems(@NotNull ItemStack ingredient, @Nullable Predicate<ItemStack> validator) {
		char nextKey = ++key;
		addIngredientItems(nextKey, ingredient);
		if (validator != null) withValidator(nextKey, validator);
		return builder();
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredients ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredients(@NotNull Material... ingredients) {
		for (Material material : ingredients)
			addIngredients(++key, material);
		return builder();
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredients ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredientItems(@NotNull ItemStack... ingredients) {
		for (ItemStack ingredient : ingredients)
			addIngredientItems(++key, ingredient);
		return builder();
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredients ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredients(@NotNull Collection<Material> ingredients) {
		return addIngredients(++key, ingredients);
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredients ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredients(@NotNull Collection<Material> ingredients, @Nullable Predicate<ItemStack> validator) {
		char nextKey = ++key;
		addIngredients(nextKey, ingredients);
		if (validator != null) withValidator(nextKey, validator);
		return builder();
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredients ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredientItems(@NotNull Collection<ItemStack> ingredients) {
		return addIngredientItems(++key, ingredients);
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredients ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredientItems(@NotNull Collection<ItemStack> ingredients, @Nullable Predicate<ItemStack> validator) {
		char nextKey = ++key;
		addIngredientItems(nextKey, ingredients);
		if (validator != null) withValidator(nextKey, validator);
		return builder();
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredients ingredient
	 * @param amount the amount of ingredients
	 * @return this builder
	 */
	public @NotNull T addIngredients(@NotNull Collection<Material> ingredients, int amount) {
		for (int i = 0; i < amount; i++)
			addIngredients(ingredients);
		return builder();
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param ingredients ingredient
	 * @param amount the amount of ingredients
	 * @return this builder
	 */
	public @NotNull T addIngredientItems(@NotNull Collection<ItemStack> ingredients, int amount) {
		for (int i = 0; i < amount; i++)
			addIngredientItems(ingredients);
		return builder();
	}

	@Override
	public boolean checkMatrix(@Nullable ItemStack @NotNull [] matrix) {
		List<ItemStack> items = new ArrayList<>();
		for (ItemStack item : matrix) {
			if (!ItemStack.isEmpty(item))
				items.add(item);
		}

		Map<Character, List<ItemStack>> requiredItems = getIngredients();
		if (items.size() != requiredItems.size()) return false;

		for (Map.Entry<Character, List<ItemStack>> entry : requiredItems.entrySet()) {
			boolean found = false;
			for (int i = 0; i < items.size(); i++) {
				ItemStack item = items.get(i);
				if (!findMatch(entry.getKey(), item)) continue;

				found = true;
				items.remove(i);
				break;
			}
			if (!found) return false;
		}

		return true;
	}

}
