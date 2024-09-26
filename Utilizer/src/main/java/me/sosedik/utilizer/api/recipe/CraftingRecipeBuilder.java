package me.sosedik.utilizer.api.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Represents a basic crafting recipe
 *
 * @param <T> recipe class
 */
public abstract class CraftingRecipeBuilder<T extends CraftingRecipeBuilder<T>> implements CustomRecipe, ExtraRecipeChecks<T> {

	private static final ItemStack AIR_ITEM = ItemStack.empty();

	protected final NamespacedKey namespacedKey;
	protected final ItemStack result;
	protected String group = "";
	protected boolean special = false;
	protected final Map<Character, List<ItemStack>> ingredients = new HashMap<>();
	protected final Map<Character, Predicate<ItemStack>> validators = new HashMap<>();

	protected CraftingRecipeBuilder(@NotNull ItemStack result, @NotNull NamespacedKey key) {
		this.result = result.clone();
		this.namespacedKey = key;
	}

	/**
	 * Sets the recipe's group
	 *
	 * @param group the recipe's group
	 * @return this builder
	 */
	public @NotNull T withGroup(@NotNull String group) {
		this.group = group;
		return builder();
	}

	/**
	 * Checks whether this recipe is special
	 *
	 * @return whether this recipe is special
	 */
	public boolean isSpecial() {
		return this.special;
	}

	/**
	 * Marks this recipe as special
	 *
	 * @return this builder
	 */
	public @NotNull T special() {
		return special(true);
	}

	/**
	 * Sets the special state of this recipe
	 *
	 * @param special whether the recipe is special
	 * @return this builder
	 */
	public @NotNull T special(boolean special) {
		this.special = special;
		return builder();
	}

	/**
	 * Adds recipe ingredient
	 * 
	 * @param key ingredient key
	 * @param ingredient ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredients(char key, @NotNull Material ingredient) {
		return addIngredientItems(key, ItemStack.of(ingredient));
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param key ingredient key
	 * @param ingredient ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredientItems(char key, @NotNull ItemStack ingredient) {
		this.ingredients.computeIfAbsent(key, k -> new ArrayList<>()).add(ingredient);
		return builder();
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param key ingredient key
	 * @param materials ingredients
	 * @return this builder
	 */
	public @NotNull T addIngredients(char key, @NotNull Material... materials) {
		return addIngredientItems(key, Arrays.stream(materials).map(ItemStack::of).toList());
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param key ingredient key
	 * @param ingredients ingredients
	 * @return this builder
	 */
	public @NotNull T addIngredientItems(char key, @NotNull ItemStack... ingredients) {
		return addIngredientItems(key, List.of(ingredients));
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param key ingredient key
	 * @param ingredients ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredients(char key, @NotNull Collection<Material> ingredients) {
		List<ItemStack> ingredientVariants = this.ingredients.computeIfAbsent(key, k -> new ArrayList<>());
		for (Material ingredient : ingredients)
			ingredientVariants.add(ItemStack.of(ingredient));
		return builder();
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param key ingredient key
	 * @param ingredients ingredient
	 * @return this builder
	 */
	public @NotNull T addIngredientItems(char key, @NotNull Collection<ItemStack> ingredients) {
		List<ItemStack> ingredientVariants = this.ingredients.computeIfAbsent(key, k -> new ArrayList<>());
		for (ItemStack ingredient : ingredients)
			ingredientVariants.add(ingredient.clone());
		return builder();
	}

	/**
	 * Adds a special ingredient validator
	 * 
	 * @param key ingredient key
	 * @param validator ingredient validator
	 * @return this builder
	 */
	public @NotNull T withValidator(char key, @NotNull Predicate<ItemStack> validator) {
		this.validators.put(key, validator);
		return builder();
	}

	@Override
	public @NotNull NamespacedKey getKey() {
		return this.namespacedKey;
	}

	@Override
	public @NotNull String getGroup() {
		return this.group;
	}

	@Override
	public @NotNull ItemStack getResult() {
		return this.result;
	}

	@Override
	public @NotNull Map<Character, List<ItemStack>> getIngredients() {
		return this.ingredients;
	}

	/**
	 * Checks for a matching item in a list of ingredient variants
	 *
	 * @param key ingredients key
	 * @param item item
	 * @return whether the item matches any variant
	 */
	protected boolean findMatch(char key, @Nullable ItemStack item) {
		List<ItemStack> ingredients = this.ingredients.get(key);
		if (ingredients == null) return false;

		if (item == null) item = AIR_ITEM;

		Predicate<ItemStack> validator = this.validators.get(key);
		for (ItemStack variant : ingredients) {
			if (variant.getType() == item.getType()) {
				return validator == null || validator.test(item);
			}
		}

		return false;
	}

	/**
	 * Registers the recipe
	 *
	 * @return this builder
	 */
	protected abstract @NotNull T register();

	/**
	 * Returns this builder
	 * 
	 * @return this builder
	 */
	@SuppressWarnings("unchecked")
	protected @NotNull T builder() {
		return (T) this;
	}

}
