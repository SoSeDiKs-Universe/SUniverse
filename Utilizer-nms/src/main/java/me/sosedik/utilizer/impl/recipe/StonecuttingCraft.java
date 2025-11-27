package me.sosedik.utilizer.impl.recipe;

import me.sosedik.utilizer.api.recipe.OneItemRecipe;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecuttingRecipe;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class StonecuttingCraft extends OneItemRecipe<StonecuttingCraft> {

	public StonecuttingCraft(ItemStack result, NamespacedKey id) {
		super(result, new NamespacedKey(id.namespace(), id.value() + "_from_stonecutting"));
	}

	@Override
	public StonecuttingCraft register() {
		var recipe = new StonecuttingRecipe(getKey(), getResult(), getRecipeChoice());
		recipe.setGroup(getGroup());
		Bukkit.addRecipe(recipe);
		return this;
	}

}
