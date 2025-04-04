package me.sosedik.trappednewbie.misc;

import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.utilizer.impl.recipe.ShapedCraft;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

public class TrappedNewbieRecipes {

	private TrappedNewbieRecipes() {
		throw new IllegalStateException("Utility class");
	}

	public static void addRecipes() {
		new ShapedCraft(new ItemStack(TrappedNewbieItems.PAPER_PLANE, 3), trappedNewbieKey("paper_plane"), "P P", " P ")
			.withGroup("paper_plane")
			.addIngredients('P', Material.PAPER)
			.register();
	}

}
