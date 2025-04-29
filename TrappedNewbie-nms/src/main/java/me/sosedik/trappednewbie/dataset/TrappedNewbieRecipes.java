package me.sosedik.trappednewbie.dataset;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.impl.recipe.ShapedCraft;
import me.sosedik.utilizer.impl.recipe.ShapelessCraft;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

@NullMarked
public class TrappedNewbieRecipes {

	private TrappedNewbieRecipes() {
		throw new IllegalStateException("Utility class");
	}

	public static void addRecipes() {
		new ShapedCraft(new ItemStack(TrappedNewbieItems.PAPER_PLANE, 3), trappedNewbieKey("paper_plane"), "P P", " P ")
			.withGroup("paper_plane")
			.addIngredients('P', Material.PAPER)
			.register();

		new ShapelessCraft(new ItemStack(TrappedNewbieItems.TWINE), trappedNewbieKey("twine"))
			.addIngredients(TrappedNewbieItems.FIBER, 3)
			.register();

		new ShapedCraft(new ItemStack(TrappedNewbieItems.FLINT_KNIFE), trappedNewbieKey("flint_knife"), "FT", "FS")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE) // HORSEHAIR
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();

		new ShapedCraft(new ItemStack(TrappedNewbieItems.FLINT_SHEARS), trappedNewbieKey("flint_shears"), "FT", "FF")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE) // HORSEHAIR
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();

		new ShapelessCraft(new ItemStack(TrappedNewbieItems.ROUGH_STICK), trappedNewbieKey("rough_stick"))
			.addIngredients(TrappedNewbieTags.BRANCHES.getValues())
			.addIngredients('S', UtilizerTags.SHEARS.getValues())
			.addIngredients('S', UtilizerTags.KNIFES.getValues())
			.register();

		Bukkit.addFuel(TrappedNewbieItems.ROUGH_STICK, 100);

		Map<Material, List<ItemStack>> replacements = new HashMap<>();

		addReplacements(replacements, Material.STICK, TrappedNewbieTags.STICKS);
		addReplacements(replacements, Material.SHEARS, UtilizerTags.SHEARS);

		TrappedNewbie.scheduler().sync(() -> {
			List<Recipe> toReAdd = new ArrayList<>();
			Bukkit.recipeIterator().forEachRemaining(recipe -> {
				if (!(recipe instanceof Keyed)) return;

				if (updateRecipe(replacements, recipe))
					toReAdd.add(recipe);
			});
			toReAdd.forEach(recipe -> {
				Bukkit.removeRecipe(((Keyed) recipe).getKey(), false);
				Bukkit.addRecipe(recipe, false);
			});
		}, 1L);
	}

	private static void addReplacements(Map<Material, List<ItemStack>> map, Material type, Tag<Material> replacements) {
		map.computeIfAbsent(type, k -> new ArrayList<>()).addAll(replacements.getValues().stream().map(ItemStack::new).toList());
	}

	private static boolean updateRecipe(Map<Material, List<ItemStack>> replacements, Recipe recipe) {
		boolean modified = false;
		if (recipe instanceof ShapedRecipe shapedRecipe) {
			Map<Character, RecipeChoice> choiceMap = shapedRecipe.getChoiceMap();
			for (Map.Entry<Character, RecipeChoice> entry : choiceMap.entrySet()) {
				RecipeChoice choice = updateChoice(replacements, entry.getValue());
				if (choice == null) continue;

				modified = true;
				shapedRecipe.setIngredient(entry.getKey(), choice);
			}
		} else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
			List<RecipeChoice> choiceList = shapelessRecipe.getChoiceList();
			for (RecipeChoice choice : choiceList) {
				RecipeChoice recipeChoice = updateChoice(replacements, choice);
				if (recipeChoice == null) continue;

				modified = true;
				shapelessRecipe.removeIngredient(choice);
				shapelessRecipe.addIngredient(recipeChoice);
			}
		}

		return modified;
	}

	private static @Nullable RecipeChoice updateChoice(Map<Material, List<ItemStack>> map, RecipeChoice recipeChoice) {
		List<ItemStack> items = new ArrayList<>();
		boolean modified = false;
		if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
			for (Material material : materialChoice.getChoices()) {
				List<ItemStack> replacements = map.get(material);
				if (replacements == null) {
					items.add(new ItemStack(material));
				} else {
					modified = true;
					items.addAll(replacements);
				}
			}
		} else if (recipeChoice instanceof RecipeChoice.ExactChoice exactChoice) {
			for (ItemStack item : exactChoice.getChoices()) {
				List<ItemStack> replacements = map.get(item.getType());
				if (replacements == null) {
					items.add(item);
				} else {
					modified = true;
					items.addAll(replacements);
				}
			}
		}

		if (!modified) return null;

		return new RecipeChoice.ExactChoice(items.toArray(ItemStack[]::new));
	}

}
