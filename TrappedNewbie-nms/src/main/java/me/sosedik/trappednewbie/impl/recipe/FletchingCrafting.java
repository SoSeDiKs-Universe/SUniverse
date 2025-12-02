package me.sosedik.trappednewbie.impl.recipe;

import com.google.common.base.Preconditions;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.utilizer.api.recipe.ShapelessRecipeBuilder;
import me.sosedik.utilizer.util.RecipeManager;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FletchingCrafting extends ShapelessRecipeBuilder<FletchingCrafting> {

	public FletchingCrafting(ItemStack result, NamespacedKey key) {
		super(result, key);
	}

	@Override
	public FletchingCrafting register() {
		Preconditions.checkArgument(this.ingredients.size() == 1, "Fletching can only have 1 ingredient");
		addIngredients(TrappedNewbieTags.ARROW_HEAD_MATERIALS.getValues());
		addIngredients(TrappedNewbieTags.ARROW_STICK_MATERIALS.getValues());
		addIngredients(TrappedNewbieTags.ARROW_FLETCHING_MATERIALS.getValues());
		RecipeManager.addRecipe(this);
		return this;
	}

}
