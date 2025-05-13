package me.sosedik.utilizer.util;

import me.sosedik.utilizer.api.recipe.CustomRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores custom recipes
 */
@NullMarked
public class RecipeManager {

	private RecipeManager() {
		throw new IllegalStateException("Utility class");
	}

	private static final Map<NamespacedKey, CustomRecipe> recipes = new HashMap<>();

	/**
	 * Adds a new custom recipe
	 *
	 * @param recipe recipe
	 */
	public static void addRecipe(CustomRecipe recipe) {
		recipes.put(recipe.getKey(), recipe);
	}

	/**
	 * Gets all recipes for the provided recipe class
	 *
	 * @param clazz recipe class
	 * @return stored recipes for the class
	 * @param <T> recipe type
	 */
	public static <T extends CustomRecipe> @Unmodifiable List<T> getRecipesFor(Class<T> clazz) {
		return recipes.values().stream()
				.filter(clazz::isInstance)
				.map(clazz::cast)
				.toList();
	}

	/**
	 * Calculates the crafting result for the given recipe
	 *
	 * @param clazz recipe class
	 * @param items crafting grid
	 * @return crafted item, if present
	 * @param <T> recipe type
	 */
	public static <T extends CustomRecipe> @Nullable ItemStack getResult(Class<T> clazz, @Nullable ItemStack[] items) {
		T recipe = getRecipe(clazz, items);
		return recipe == null ? null : recipe.getResult();
	}

	/**
	 * Finds the matching recipe
	 *
	 * @param clazz recipe class
	 * @param items crafting grid
	 * @return crafting recipe, if present
	 * @param <T> recipe type
	 */
	public static <T extends CustomRecipe> @Nullable T getRecipe(Class<T> clazz, @Nullable ItemStack[] items) {
		List<T> recipes = getRecipesFor(clazz);
		for (T recipe : recipes) {
			if (recipe.checkMatrix(items))
				return recipe;
		}
		return null;
	}

	/**
	 * Gets the custom recipe by its key
	 *
	 * @param key recipe key
	 * @return crafting recipe
	 */
	public static @Nullable CustomRecipe getRecipe(NamespacedKey key) {
		return recipes.get(key);
	}

}
