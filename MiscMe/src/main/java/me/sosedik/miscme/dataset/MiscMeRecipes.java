package me.sosedik.miscme.dataset;

import com.destroystokyo.paper.MaterialTags;
import me.sosedik.utilizer.impl.recipe.ShapedCraft;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import static me.sosedik.miscme.MiscMe.miscMeKey;

public class MiscMeRecipes {

	private MiscMeRecipes() {
		throw new IllegalStateException("Utility class");
	}

	public static void addRecipes() {
		new ShapedCraft(new ItemStack(MiscMeItems.LUNAR_CLOCK), miscMeKey("lunar_clock"), " L ", "LCL", " L ")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('L', Material.LAPIS_LAZULI)
			.addIngredients('C', Material.CLOCK)
			.register();
		new ShapedCraft(new ItemStack(MiscMeItems.DEPTH_METER), miscMeKey("depth_meter"), " C ", "CRC", " C ")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('C', Material.COPPER_INGOT)
			.addIngredients('R', Material.REDSTONE)
			.register();
		new ShapedCraft(new ItemStack(MiscMeItems.SPEEDOMETER), miscMeKey("speedometer"), "PPP", "SQD", "IRI")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('P', MaterialTags.GLASS_PANES.getValues())
			.addIngredients('D', Material.BLACK_DYE)
			.addIngredients('Q', Material.QUARTZ)
			.addIngredients('I', Material.IRON_INGOT)
			.addIngredients('R', Material.REDSTONE)
			.addIngredients('S', Material.STICK)
			.register();
	}

}
