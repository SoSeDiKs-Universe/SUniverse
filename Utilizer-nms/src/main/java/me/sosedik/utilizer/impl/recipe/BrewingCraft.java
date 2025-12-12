package me.sosedik.utilizer.impl.recipe;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.potion.PotionMix;
import me.sosedik.utilizer.api.recipe.CustomRecipe;
import me.sosedik.utilizer.api.recipe.OneItemRecipe;
import me.sosedik.utilizer.util.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NullMarked
public class BrewingCraft extends OneItemRecipe<BrewingCraft> implements CustomRecipe {

	private static final String RECIPE_SUFFIX = "_from_brewing";

	public static final Set<Material> MUNDANE_INGREDIENTS = new HashSet<>();

	// MCCheck: 1.21.11, new potion ingredients
	static {
		MUNDANE_INGREDIENTS.addAll(List.of(Material.GLISTERING_MELON_SLICE, Material.GHAST_TEAR, Material.RABBIT_FOOT, Material.BLAZE_POWDER, Material.SPIDER_EYE, Material.SUGAR, Material.MAGMA_CREAM, Material.REDSTONE));
	}

	private final NamespacedKey key;
	private boolean ingredientOnly = false;
	private @Nullable RecipeChoice ingredient;
	private @Nullable ItemStack splash;
	private @Nullable ItemStack lingering;

	public BrewingCraft(ItemStack result, boolean ingredient, NamespacedKey key) {
		super(result, new NamespacedKey(key.namespace(), key.value() + RECIPE_SUFFIX));
		this.key = key;
		this.ingredientOnly = ingredient;
		if (ingredient)
			addIngredientItems(result);
	}

	protected RecipeChoice getRecipeChoice(Material type, PotionType potionType) {
		ItemStack potionBottle = potion(type, potionType);
		var recipeChoice = new RecipeChoice.ExactChoice(potionBottle);
		recipeChoice.setPredicate(item -> {
			if (!item.hasData(DataComponentTypes.POTION_CONTENTS)) return false;

			PotionContents data = item.getData(DataComponentTypes.POTION_CONTENTS);
			assert data != null;
			return data.potion() == potionType;
		});
		return recipeChoice;
	}

	protected ItemStack potion(Material type, PotionType potionType) {
		var potionBottle = ItemStack.of(type);
		potionBottle.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(potionType).build());
		return potionBottle;
	}

	public BrewingCraft asBrewIngredient(PotionType from, PotionType to) {
		return asBrewIngredient(from, to, "");
	}

	public BrewingCraft asBrewIngredient(PotionType from, PotionType to, String group) {
		if (from == PotionType.WATER && to == PotionType.MUNDANE)
			MUNDANE_INGREDIENTS.add(this.result.getType());

		String baseKey = this.key.value() + "_ingredient_to_" + to.getKey().value();
		addIngredientBrew(baseKey, group, Material.POTION, from, to);
		addIngredientBrew(baseKey + "_splash", group, Material.SPLASH_POTION, from, to);
		addIngredientBrew(baseKey + "_lingering", group, Material.LINGERING_POTION, from, to);

		return this;
	}

	private void addIngredientBrew(String baseKey, String group, Material potionType, PotionType from, PotionType to) {
		RecipeChoice ingredient = getIngredient();
		ItemStack result = potion(Material.POTION, to);
		NamespacedKey recipeKey = new NamespacedKey(this.key.namespace(), baseKey + RECIPE_SUFFIX);

		Bukkit.getPotionBrewer().addPotionMix(new PotionMix(recipeKey, result, getRecipeChoice(potionType, from), ingredient));
	}

	public BrewingCraft splash(ItemStack result) {
		result = result.clone();
		this.splash = result;
		Bukkit.getPotionBrewer().addPotionMix(new PotionMix(new NamespacedKey(this.key.namespace(), this.key.value() + "_splash" + RECIPE_SUFFIX), result, new RecipeChoice.ExactChoice(this.result), new RecipeChoice.MaterialChoice(Material.GUNPOWDER)));
		return this;
	}

	public BrewingCraft linger(ItemStack result) {
		result = result.clone();
		this.lingering = result;
		assert this.splash != null;
		Bukkit.getPotionBrewer().addPotionMix(new PotionMix(new NamespacedKey(this.key.namespace(), this.key.value() + "_lingering" + RECIPE_SUFFIX), result, new RecipeChoice.ExactChoice(this.splash), new RecipeChoice.MaterialChoice(Material.DRAGON_BREATH)));
		return this;
	}

	public RecipeChoice getIngredient() {
		if (this.ingredient == null) this.ingredient = getRecipeChoice();
		return this.ingredient;
	}

	public boolean isIngredientOnly() {
		return this.ingredientOnly;
	}

	public boolean canSplash() {
		return this.splash != null;
	}

	public boolean canLinger() {
		return this.lingering != null;
	}

	public @Nullable ItemStack getSplash() {
		return this.splash;
	}

	public @Nullable ItemStack getLingering() {
		return this.lingering;
	}

	@Override
	public boolean checkMatrix(@Nullable ItemStack [] items) {
		return false;
	}

	public BrewingCraft register() {
		if (!this.ingredientOnly)
			RecipeManager.addRecipe(this);
		return this;
	}

	@Override
	public ItemStack getResult() {
		return this.result;
	}

}
