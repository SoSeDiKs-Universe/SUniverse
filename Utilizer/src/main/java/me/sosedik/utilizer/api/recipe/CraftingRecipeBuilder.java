package me.sosedik.utilizer.api.recipe;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
@NullMarked
public abstract class CraftingRecipeBuilder<T extends CraftingRecipeBuilder<T>> implements CustomRecipe, ExtraRecipeChecks<T> {

	private static final ItemStack AIR_ITEM = ItemStack.empty();

	protected final NamespacedKey namespacedKey;
	protected final ItemStack result;
	protected String group = "";
	protected boolean special = false;
	protected final Map<Character, List<ItemStack>> ingredients = new HashMap<>();
	protected final Map<Character, Predicate<ItemStack>> validators = new HashMap<>();

	protected CraftingRecipeBuilder(ItemStack result, NamespacedKey key) {
		this.result = result.clone();
		this.namespacedKey = key;
	}

	/**
	 * Sets the recipe's group
	 *
	 * @param group the recipe's group
	 * @return this builder
	 */
	public T withGroup(String group) {
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
	public T special() {
		return special(true);
	}

	/**
	 * Sets the special state of this recipe
	 *
	 * @param special whether the recipe is special
	 * @return this builder
	 */
	public T special(boolean special) {
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
	public T addIngredients(char key, Material ingredient) {
		return addIngredientItems(key, ItemStack.of(ingredient));
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param key ingredient key
	 * @param ingredient ingredient
	 * @return this builder
	 */
	public T addIngredientItems(char key, ItemStack ingredient) {
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
	public T addIngredients(char key, Material... materials) {
		return addIngredientItems(key, Arrays.stream(materials).map(ItemStack::of).toList());
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param key ingredient key
	 * @param ingredients ingredients
	 * @return this builder
	 */
	public T addIngredientItems(char key, ItemStack... ingredients) {
		return addIngredientItems(key, List.of(ingredients));
	}

	/**
	 * Adds recipe ingredient
	 *
	 * @param key ingredient key
	 * @param ingredients ingredient
	 * @return this builder
	 */
	public T addIngredients(char key, Collection<Material> ingredients) {
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
	public T addIngredientItems(char key, Collection<ItemStack> ingredients) {
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
	public T withValidator(char key, Predicate<ItemStack> validator) {
		this.validators.put(key, validator);
		return builder();
	}

	@Override
	public NamespacedKey getKey() {
		return this.namespacedKey;
	}

	@Override
	public String getGroup() {
		return this.group;
	}

	@Override
	public ItemStack getResult() {
		return this.result;
	}

	@Override
	public Map<Character, List<ItemStack>> getIngredients() {
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
	protected abstract T register();

	/**
	 * Returns this builder
	 * 
	 * @return this builder
	 */
	@SuppressWarnings("unchecked")
	protected T builder() {
		return (T) this;
	}

}
