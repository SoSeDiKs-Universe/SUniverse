package me.sosedik.utilizer.impl.recipe;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.potion.PotionMix;
import me.sosedik.utilizer.api.recipe.CustomRecipe;
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
public class BrewingCraft implements CustomRecipe {

	private static final String RECIPE_SUFFIX = "_from_brewing";

	public static final Set<Material> MUNDANE_INGREDIENTS = new HashSet<>();

	// MCCheck: 1.21.10, new potion ingredients
	static {
		MUNDANE_INGREDIENTS.addAll(List.of(Material.GLISTERING_MELON_SLICE, Material.GHAST_TEAR, Material.RABBIT_FOOT, Material.BLAZE_POWDER, Material.SPIDER_EYE, Material.SUGAR, Material.MAGMA_CREAM, Material.REDSTONE));
	}

	private final NamespacedKey key;
	private final ItemStack result;
	private RecipeChoice ingredient;
	private @Nullable ItemStack splash;
	private @Nullable ItemStack lingering;

	public BrewingCraft(ItemStack result, NamespacedKey key) {
		this.key = key;
		this.result = result.clone();
	}

	public BrewingCraft asIngredient() {
		MUNDANE_INGREDIENTS.add(this.result.getType());

		var waterBottle = ItemStack.of(Material.POTION);
		waterBottle.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(PotionType.WATER).build());

		var recipeChoice = new RecipeChoice.ExactChoice(waterBottle);
		recipeChoice.setPredicate(item -> {
			if (!item.hasData(DataComponentTypes.POTION_CONTENTS)) return false;

			PotionContents data = item.getData(DataComponentTypes.POTION_CONTENTS);
			assert data != null;
			return data.potion() == PotionType.WATER;
		});

		var mundaneBottle = ItemStack.of(Material.POTION);
		mundaneBottle.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(PotionType.MUNDANE).build());

		var ingredientChoice = new RecipeChoice.ExactChoice(this.result);
		Bukkit.getPotionBrewer().addPotionMix(new PotionMix(new NamespacedKey(this.key.namespace(), this.key.value() + RECIPE_SUFFIX), mundaneBottle, recipeChoice, ingredientChoice));
		return this;
	}

	public BrewingCraft withWater(ItemStack ingredient) {
		return withWater(new RecipeChoice.ExactChoice(ingredient.clone()));
	}

	public BrewingCraft withWater(RecipeChoice ingredient) {
		this.ingredient = ingredient;

		var potion = ItemStack.of(Material.POTION);
		potion.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(PotionType.AWKWARD).build());

		Bukkit.getPotionBrewer().addPotionMix(new PotionMix(new NamespacedKey(this.key.namespace(), this.key.value() + RECIPE_SUFFIX), this.result, new RecipeChoice.ExactChoice(potion), ingredient));
		return this;
	}

	public BrewingCraft splash(ItemStack result) {
		result = result.clone();
		this.splash = result;
		Bukkit.getPotionBrewer().addPotionMix(new PotionMix(new NamespacedKey(this.key.namespace(), this.key + "_splash" + RECIPE_SUFFIX), result, new RecipeChoice.ExactChoice(this.result), new RecipeChoice.MaterialChoice(Material.GUNPOWDER)));
		return this;
	}

	public BrewingCraft linger(ItemStack result) {
		result = result.clone();
		this.lingering = result;
		assert this.splash != null;
		Bukkit.getPotionBrewer().addPotionMix(new PotionMix(new NamespacedKey(this.key.namespace(), this.key + "_lingering" + RECIPE_SUFFIX), result, new RecipeChoice.ExactChoice(this.splash), new RecipeChoice.MaterialChoice(Material.DRAGON_BREATH)));
		return this;
	}

	public RecipeChoice getIngredient() {
		return ingredient;
	}

	public boolean canSplash() {
		return splash != null;
	}

	public boolean canLinger() {
		return lingering != null;
	}

	public @Nullable ItemStack getSplash() {
		return splash;
	}

	public @Nullable ItemStack getLingering() {
		return lingering;
	}

	@Override
	public boolean checkMatrix(@Nullable ItemStack [] items) {
		return false;
	}

	public CustomRecipe register() {
		RecipeManager.addRecipe(this);
		return this;
	}

	@Override
	public NamespacedKey getKey() {
		return new NamespacedKey(this.key.namespace(), this.key + RECIPE_SUFFIX);
	}

	@Override
	public ItemStack getResult() {
		return this.result;
	}

}
