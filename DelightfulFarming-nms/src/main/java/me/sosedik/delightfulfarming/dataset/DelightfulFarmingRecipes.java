package me.sosedik.delightfulfarming.dataset;

import me.sosedik.utilizer.api.recipe.CraftingRecipeBuilder;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.impl.recipe.CampfireCraft;
import me.sosedik.utilizer.impl.recipe.ShapelessCraft;
import me.sosedik.utilizer.impl.recipe.SmokingCraft;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static me.sosedik.delightfulfarming.DelightfulFarming.delightfulFarmingKey;

@NullMarked
public class DelightfulFarmingRecipes {

	private static final List<Map.Entry<ItemStack, Predicate<ItemStack>>> MILK_PREDICATES = new ArrayList<>();

	public static void addRecipes() {
		addMilkPredicate(ItemStack.of(Material.MILK_BUCKET), item -> true);

		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.SWEET_BERRY_MINCE), delightfulFarmingKey("sweet_berry_mince"))
			.addIngredients(Material.PORKCHOP, Material.SWEET_BERRIES)
			.register();

		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.GLOWGURT), delightfulFarmingKey("glowgurt"))
			.addIngredients(Material.BOWL)
			.addIngredients(Material.GLOW_BERRIES, 2)
			.addIngredients(Material.SUGAR)
			.apply(DelightfulFarmingRecipes::addMilkIngredient)
			.register();

		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.BROKEN_EGG), delightfulFarmingKey("broken_egg"))
			.withGroup("broken_egg")
			.addIngredients(Material.EGG)
			.register();
		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.BROKEN_BROWN_EGG), delightfulFarmingKey("broken_brown_egg"))
			.withGroup("broken_egg")
			.addIngredients(Material.BROWN_EGG)
			.register();
		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.BROKEN_BLUE_EGG), delightfulFarmingKey("broken_blue_egg"))
			.withGroup("broken_egg")
			.addIngredients(Material.BLUE_EGG)
			.register();
		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.BROKEN_TURTLE_EGG), delightfulFarmingKey("broken_turtle_egg"))
			.withGroup("broken_egg")
			.addIngredients(Material.TURTLE_EGG)
			.register();

		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.SWEET_BERRY_PIPS), delightfulFarmingKey("sweet_berry_pips_from_sweet_berries"))
			.addIngredients(Material.SWEET_BERRIES)
			.register();
		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.GLOW_BERRY_PIPS), delightfulFarmingKey("glow_berry_pips_from_glow_berries"))
			.addIngredients(Material.GLOW_BERRIES)
			.register();

		new ShapelessCraft(ItemStack.of(Material.SWEET_BERRIES, 9), delightfulFarmingKey("sweet_berries"))
			.addIngredients(DelightfulFarmingItems.SWEET_BERRY_BASKET)
			.register();
		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.SWEET_BERRY_BASKET), delightfulFarmingKey("sweet_berry_basket"))
			.addIngredients(Material.SWEET_BERRIES, 9)
			.register();
		new ShapelessCraft(ItemStack.of(Material.GLOW_BERRIES, 9), delightfulFarmingKey("glow_berries"))
			.addIngredients(DelightfulFarmingItems.GLOW_BERRY_BASKET)
			.register();
		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.GLOW_BERRY_BASKET), delightfulFarmingKey("glow_berry_basket"))
			.addIngredients(Material.GLOW_BERRIES, 9)
			.register();

		new CampfireCraft(ItemStack.of(DelightfulFarmingItems.SWEET_BERRY_MEATBALLS), 30 * 20, delightfulFarmingKey("sweet_berry_mince_to_sweet_berry_meatballs"))
			.addIngredients(DelightfulFarmingItems.SWEET_BERRY_MINCE)
			.register();
		new SmokingCraft(ItemStack.of(DelightfulFarmingItems.SWEET_BERRY_MEATBALLS), 10 * 20, delightfulFarmingKey("sweet_berry_mince_to_sweet_berry_meatballs"))
			.addIngredients(DelightfulFarmingItems.SWEET_BERRY_MINCE)
			.register();

		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.CHARCOAL_BLOCK), delightfulFarmingKey("charcoal_block"))
			.addIngredients(Material.CHARCOAL, 9)
			.register();
		new ShapelessCraft(ItemStack.of(Material.CHARCOAL, 9), delightfulFarmingKey("charcoal"))
			.addIngredients(DelightfulFarmingItems.CHARCOAL_BLOCK)
			.register();

		// TODO cutting board recipe
		new ShapelessCraft(ItemStack.of(DelightfulFarmingItems.CACTUS_FLESH, 2), delightfulFarmingKey("cactus_flesh_from_cutting"))
			.addIngredients(Material.CACTUS)
			.addIngredients(UtilizerTags.KNIFES.getValues())
			.register();

		new CampfireCraft(ItemStack.of(DelightfulFarmingItems.CACTUS_STEAK), 30 * 20, delightfulFarmingKey("cactus_steak"))
			.addIngredients(DelightfulFarmingItems.CACTUS_FLESH)
			.register();
		new SmokingCraft(ItemStack.of(DelightfulFarmingItems.CACTUS_STEAK), 10 * 20, delightfulFarmingKey("cactus_steak"))
			.addIngredients(DelightfulFarmingItems.CACTUS_FLESH)
			.register();

		new CampfireCraft(ItemStack.of(DelightfulFarmingItems.ROASTED_SPIDER_EYE), 12 * 20, delightfulFarmingKey("roasted_spider_eye"))
			.addIngredients(Material.SPIDER_EYE)
			.register();
		new SmokingCraft(ItemStack.of(DelightfulFarmingItems.ROASTED_SPIDER_EYE), 4 * 20, delightfulFarmingKey("roasted_spider_eye"))
			.addIngredients(Material.SPIDER_EYE)
			.register();

		Bukkit.addFuel(DelightfulFarmingItems.CHARCOAL_BLOCK, 80 * 200);
	}

	private static void addMilkIngredient(CraftingRecipeBuilder<?> recipe) {
		MILK_PREDICATES.forEach(entry -> recipe.addIngredientItems('M', entry.getKey(), entry.getValue()));
	}

	public static void addMilkPredicate(ItemStack preview, Predicate<ItemStack> predicate) {
		MILK_PREDICATES.add(Map.entry(preview, predicate));
	}

}
