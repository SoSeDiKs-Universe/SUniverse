package me.sosedik.miscme.dataset;

import com.destroystokyo.paper.MaterialTags;
import me.sosedik.utilizer.impl.recipe.ShapedCraft;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import static me.sosedik.miscme.MiscMe.miscMeKey;

public final class MiscMeRecipes {

	private MiscMeRecipes() {
		throw new IllegalStateException("Utility class");
	}

	public static void addRecipes() {
		new ShapedCraft(ItemStack.of(MiscMeItems.LUNAR_CLOCK), miscMeKey("lunar_clock"), " L ", "LCL", " L ")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('L', Material.LAPIS_LAZULI)
			.addIngredients('C', Material.CLOCK)
			.register();
		new ShapedCraft(ItemStack.of(MiscMeItems.DEPTH_METER), miscMeKey("depth_meter"), " C ", "CRC", " C ")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('C', Material.COPPER_INGOT)
			.addIngredients('R', Material.REDSTONE)
			.register();
		new ShapedCraft(ItemStack.of(MiscMeItems.SPEEDOMETER), miscMeKey("speedometer"), "PPP", "SQD", "IRI")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('P', MaterialTags.GLASS_PANES.getValues())
			.addIngredients('D', Material.BLACK_DYE)
			.addIngredients('Q', Material.QUARTZ)
			.addIngredients('I', Material.IRON_INGOT)
			.addIngredients('R', Material.REDSTONE)
			.addIngredients('S', Material.STICK)
			.register();
		new ShapedCraft(ItemStack.of(MiscMeItems.BAROMETER), miscMeKey("barometer"), " C ", "CRC", " C ")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('C', Material.COPPER_INGOT)
			.addIngredients('R', Material.REDSTONE_TORCH)
			.register();
		new ShapedCraft(ItemStack.of(MiscMeItems.LUXMETER), miscMeKey("luxmeter"), " G ", "GDG", " G ")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('G', Material.GOLD_INGOT)
			.addIngredients('D', Material.DAYLIGHT_DETECTOR)
			.register();
	}

}
