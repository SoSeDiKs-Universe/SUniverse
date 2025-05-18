package me.sosedik.delightfulfarming.dataset;

import me.sosedik.utilizer.impl.recipe.CampfireCraft;
import me.sosedik.utilizer.impl.recipe.ShapelessCraft;
import me.sosedik.utilizer.impl.recipe.SmokingCraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.delightfulfarming.DelightfulFarming.delightfulFarmingKey;

@NullMarked
public class DelightfulFarmingRecipes {

	public static void addRecipes() {
		new ShapelessCraft(new ItemStack(DelightfulFarmingItems.SWEET_BERRY_MINCE), delightfulFarmingKey("sweet_berry_mince"))
			.addIngredients(Material.PORKCHOP, Material.SWEET_BERRIES)
			.register();

		new ShapelessCraft(new ItemStack(DelightfulFarmingItems.GLOWGURT), delightfulFarmingKey("glowgurt"))
			.addIngredients(Material.BOWL)
			.addIngredients(Material.GLOW_BERRIES, 2)
			.addIngredients(Material.SUGAR)
			.addIngredients(DelightfulFarmingTags.MILK_CONTAINERS.getValues())
			.register();

		new ShapelessCraft(new ItemStack(DelightfulFarmingItems.SWEET_BERRY_PIPS), delightfulFarmingKey("sweet_berry_pips_from_sweet_berries"))
			.addIngredients(Material.SWEET_BERRIES)
			.register();
		new ShapelessCraft(new ItemStack(DelightfulFarmingItems.GLOW_BERRY_PIPS), delightfulFarmingKey("glow_berry_pips_from_glow_berries"))
			.addIngredients(Material.GLOW_BERRIES)
			.register();

		new ShapelessCraft(new ItemStack(Material.SWEET_BERRIES, 9), delightfulFarmingKey("sweet_berries"))
			.addIngredients(DelightfulFarmingItems.SWEET_BERRY_BASKET)
			.register();
		new ShapelessCraft(new ItemStack(DelightfulFarmingItems.SWEET_BERRY_BASKET), delightfulFarmingKey("sweet_berry_basket"))
			.addIngredients(Material.SWEET_BERRIES, 9)
			.register();
		new ShapelessCraft(new ItemStack(Material.GLOW_BERRIES, 9), delightfulFarmingKey("glow_berries"))
			.addIngredients(DelightfulFarmingItems.GLOW_BERRY_BASKET)
			.register();
		new ShapelessCraft(new ItemStack(DelightfulFarmingItems.GLOW_BERRY_BASKET), delightfulFarmingKey("glow_berry_basket"))
			.addIngredients(Material.GLOW_BERRIES, 9)
			.register();

		new CampfireCraft(new ItemStack(DelightfulFarmingItems.SWEET_BERRY_MEATBALLS), 30 * 20, delightfulFarmingKey("sweet_berry_mince_to_sweet_berry_meatballs"))
			.addIngredients(DelightfulFarmingItems.SWEET_BERRY_MINCE)
			.register();
		new SmokingCraft(new ItemStack(DelightfulFarmingItems.SWEET_BERRY_MEATBALLS), 10 * 20, delightfulFarmingKey("sweet_berry_mince_to_sweet_berry_meatballs"))
			.addIngredients(DelightfulFarmingItems.SWEET_BERRY_MINCE)
			.register();

		new ShapelessCraft(new ItemStack(DelightfulFarmingItems.CHARCOAL_BLOCK), delightfulFarmingKey("charcoal_block"))
			.addIngredients(Material.CHARCOAL, 9)
			.register();
		new ShapelessCraft(new ItemStack(Material.CHARCOAL, 9), delightfulFarmingKey("charcoal"))
			.addIngredients(DelightfulFarmingItems.CHARCOAL_BLOCK)
			.register();

		Bukkit.addFuel(DelightfulFarmingItems.CHARCOAL_BLOCK, 80 * 200);
	}

}
