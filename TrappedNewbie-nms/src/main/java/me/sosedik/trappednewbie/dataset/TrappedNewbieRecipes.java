package me.sosedik.trappednewbie.dataset;

import me.sosedik.miscme.api.event.player.PlayerIgniteExplosiveMinecartEvent;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.api.event.recipe.ItemCraftPrepareEvent;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.impl.recipe.FireCraft;
import me.sosedik.utilizer.impl.recipe.ShapedCraft;
import me.sosedik.utilizer.impl.recipe.ShapelessCraft;
import me.sosedik.utilizer.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.ExplosiveMinecart;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

@NullMarked
public class TrappedNewbieRecipes {

	private TrappedNewbieRecipes() {
		throw new IllegalStateException("Utility class");
	}

	public static void addRecipes() {
		new ShapedCraft(ItemStack.of(TrappedNewbieItems.PAPER_PLANE, 3), trappedNewbieKey("paper_plane"), "P P", " P ")
			.withGroup("paper_plane")
			.addIngredients('P', Material.PAPER)
			.register();

		new ShapelessCraft(ItemStack.of(TrappedNewbieItems.TWINE), trappedNewbieKey("twine"))
			.addIngredients(TrappedNewbieItems.FIBER, 3)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_KNIFE), trappedNewbieKey("flint_knife"), "FT", "FS")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE) // HORSEHAIR
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_AXE), trappedNewbieKey("flint_axe"), "FF", "ST")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE) // HORSEHAIR
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_SHEARS), trappedNewbieKey("flint_shears"), "FT", "FF")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE) // HORSEHAIR
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();

		new ShapelessCraft(ItemStack.of(TrappedNewbieItems.ROUGH_STICK), trappedNewbieKey("rough_stick"))
			.addIngredients(TrappedNewbieTags.BRANCHES.getValues())
			.addIngredients('S', UtilizerTags.SHEARS.getValues())
			.addIngredients('S', UtilizerTags.KNIFES.getValues())
			.register();
		new ShapelessCraft(ItemStack.of(Material.STICK), trappedNewbieKey("stick"))
			.addIngredients(TrappedNewbieItems.ROUGH_STICK)
			.addIngredients(UtilizerTags.KNIFES.getValues())
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.GRASS_MESH), trappedNewbieKey("grass_mesh"), "TS", "ST")
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE) // HORSEHAIR
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.COBBLESTONE_HAMMER), trappedNewbieKey("cobblestone_hammer"), "CT", "SC")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE) // HORSEHAIR
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('C', Material.COBBLESTONE)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_SHOVEL), trappedNewbieKey("flint_shovel_1"), "FT", "S ")
			.withGroup("flint_shovel")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE) // HORSEHAIR
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_SHOVEL), trappedNewbieKey("flint_shovel_2"), "F ", "ST")
			.withGroup("flint_shovel")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE) // HORSEHAIR
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_PICKAXE), trappedNewbieKey("flint_pickaxe"), "FP", "SF")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('F', Material.FLINT)
			.addIngredients('P', Tag.PLANKS.getValues())
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FIRESTRIKER), trappedNewbieKey("firestriker"), "CS", "SF")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('F', Material.FLINT)
			.addIngredients('C', Tag.ITEMS_COALS.getValues())
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.STEEL_AND_FLINT), trappedNewbieKey("steel_and_flint_1"), "F ", " S")
			.withGroup("steel_and_flint")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('F', Material.FLINT)
			.addIngredients('S', Material.IRON_INGOT)
			.register();
		new ShapedCraft(ItemStack.of(TrappedNewbieItems.STEEL_AND_FLINT), trappedNewbieKey("steel_and_flint_2"), "F", "S")
			.withGroup("steel_and_flint")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('F', Material.FLINT)
			.addIngredients('S', Material.IRON_INGOT)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.CLAY_KILN), trappedNewbieKey("clay_kiln"), "CCC", "C C", "CCC")
			.addIngredients('C', Material.CLAY_BALL)
			.register();

		new ShapelessCraft(ItemStack.of(TrappedNewbieItems.SLEEPING_BAG), trappedNewbieKey("sleeping_bag"))
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE) // HORSEHAIR
			.addIngredients(Tag.WOOL_CARPETS.getValues(), 3)
			.withPreCheck(event -> {
				Material carpet1 = null;
				Material carpet2 = null;
				for (ItemStack item : event.getMatrix()) {
					if (ItemStack.isEmpty(item)) continue;

					Material type = item.getType();
					if (!Tag.WOOL_CARPETS.isTagged(type)) continue;

					if (carpet1 == null) {
						carpet1 = type;
						continue;
					}
					if (carpet1 == type) {
						event.setResult(ItemStack.of(TrappedNewbieItems.SLEEPING_BAG).withColor(MiscUtil.getDyeColor(type, "CARPET")));
						return;
					}
					if (carpet2 == null) {
						carpet2 = type;
						continue;
					}
					if (carpet2 == type) {
						event.setResult(ItemStack.of(TrappedNewbieItems.SLEEPING_BAG).withColor(MiscUtil.getDyeColor(type, "CARPET")));
						return;
					}
				}
				if (carpet1 != null)
					event.setResult(ItemStack.of(TrappedNewbieItems.SLEEPING_BAG).withColor(MiscUtil.getDyeColor(carpet1, "CARPET")));
			})
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.TOTEMIC_STAFF), trappedNewbieKey("totemic_staff"), " LS", " S ", "S L")
			.addIngredients('L', Tag.LEAVES.getValues())
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.register();
		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLUTE), trappedNewbieKey("flute"), " LS", " S ", "S  ")
			.addIngredients('L', Tag.LEAVES.getValues())
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.register();
		new ShapedCraft(ItemStack.of(TrappedNewbieItems.RATTLE), trappedNewbieKey("rattle"), " WW", " BW", "S  ")
			.addIngredients('W', Tag.LOGS_THAT_BURN.getValues())
			.addIngredients('B', Material.STRING, TrappedNewbieItems.TWINE)
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.register();
		TrappedNewbieTags.DRUMS.getValues().forEach(type -> {
			new ShapedCraft(ItemStack.of(type), type.getKey(), "EEE", "LWL", "WLW")
				.withGroup("drum")
				.addIngredients('E', TrappedNewbieTags.HIDES.getValues())
				.addIngredients('E', Material.LEATHER)
				.addIngredients('L', figureOutLog(type, "drum"))
				.addIngredients('W', Tag.WOOL.getValues())
				.register();
		});

		addFlowerBouquetRecipe();

		addBranchRecipe(TrappedNewbieItems.ACACIA_BRANCH, Material.ACACIA_SAPLING);
		addBranchRecipe(TrappedNewbieItems.BIRCH_BRANCH, Material.BIRCH_SAPLING);
		addBranchRecipe(TrappedNewbieItems.CHERRY_BRANCH, Material.CHERRY_SAPLING);
		addBranchRecipe(TrappedNewbieItems.DARK_OAK_BRANCH, Material.DARK_OAK_SAPLING);
		addBranchRecipe(TrappedNewbieItems.JUNGLE_BRANCH, Material.JUNGLE_SAPLING);
		addBranchRecipe(TrappedNewbieItems.MANGROVE_BRANCH, Material.MANGROVE_PROPAGULE);
		addBranchRecipe(TrappedNewbieItems.OAK_BRANCH, Material.OAK_SAPLING);
		addBranchRecipe(TrappedNewbieItems.PALE_OAK_BRANCH, Material.PALE_OAK_SAPLING);
		addBranchRecipe(TrappedNewbieItems.SPRUCE_BRANCH, Material.SPRUCE_SAPLING);
		addBranchRecipe(TrappedNewbieItems.DEAD_BRANCH, Material.DEAD_BUSH);

		addRockRecipe(TrappedNewbieItems.ROCK, Material.COBBLESTONE);
		addRockRecipe(TrappedNewbieItems.PEBBLE, Material.COBBLESTONE);
		addRockRecipe(TrappedNewbieItems.ANDESITE_ROCK, Material.ANDESITE);
		addRockRecipe(TrappedNewbieItems.ANDESITE_PEBBLE, Material.ANDESITE);
		addRockRecipe(TrappedNewbieItems.DIORITE_ROCK, Material.DIORITE);
		addRockRecipe(TrappedNewbieItems.DIORITE_PEBBLE, Material.DIORITE);
		addRockRecipe(TrappedNewbieItems.GRANITE_ROCK, Material.GRANITE);
		addRockRecipe(TrappedNewbieItems.GRANITE_PEBBLE, Material.GRANITE);
		addRockRecipe(TrappedNewbieItems.SANDSTONE_ROCK, Material.SANDSTONE);
		addRockRecipe(TrappedNewbieItems.SANDSTONE_PEBBLE, Material.SANDSTONE);
		addRockRecipe(TrappedNewbieItems.RED_SANDSTONE_ROCK, Material.RED_SANDSTONE);
		addRockRecipe(TrappedNewbieItems.RED_SANDSTONE_PEBBLE, Material.RED_SANDSTONE);
		addRockRecipe(TrappedNewbieItems.END_STONE_ROCK, Material.END_STONE);
		addRockRecipe(TrappedNewbieItems.END_STONE_PEBBLE, Material.END_STONE);
		addRockRecipe(TrappedNewbieItems.NETHERRACK_ROCK, Material.NETHERRACK);
		addRockRecipe(TrappedNewbieItems.NETHERRACK_PEBBLE, Material.NETHERRACK);
		addRockRecipe(TrappedNewbieItems.BALL_OF_MUD, Material.MUD);
		addRockRecipe(TrappedNewbieItems.SOUL_SOIL_PEBBLE, Material.SOUL_SOIL);
		addRockRecipe(TrappedNewbieItems.ICE_CUBE, Material.ICE);
		addRockRecipe(TrappedNewbieItems.ICE_PEBBLE, Material.ICE);

		TrappedNewbieTags.CHOPPING_BLOCKS.getValues().forEach(type -> {
			new ShapelessCraft(ItemStack.of(type), type.getKey())
				.withGroup("chopping_block")
				.addIngredients(figureOutLog(type, "chopping_block"))
				.register();
		});

		TrappedNewbieTags.WORK_STATIONS.getValues().forEach(type -> {
			new ShapelessCraft(ItemStack.of(type), type.getKey())
				.withGroup("work_station")
				.addIngredients(figureOutLog(type, "work_station"))
				.addIngredients(Tag.ITEMS_AXES.getValues())
				.register();
		});

		TrappedNewbieTags.GLASS_SHARDS.getValues().forEach(type -> {
			Material base = type == TrappedNewbieItems.GLASS_SHARD ? Material.GLASS : Material.getMaterial(type.name().replace("_GLASS_SHARD", "_STAINED_GLASS"));
			if (base == null) return;

			new ShapelessCraft(ItemStack.of(base), trappedNewbieKey(base.key().value() + "_from_shards"))
				.withGroup("glass_from_shards")
				.addIngredients(type, 4)
				.register();
			new ShapelessCraft(ItemStack.of(type, 4), trappedNewbieKey(base.key().value() + "_to_shards"))
				.withGroup("glass_to_shards")
				.addIngredients(base)
				.addIngredients(TrappedNewbieTags.HAMMERS.getValues())
				.register();
		});

		new FireCraft(ItemStack.of(Material.BRICK), trappedNewbieKey("clay_ball_to_brick"))
			.withBurnChance(0.1)
			.addIngredients(Material.CLAY_BALL)
			.register();
		new FireCraft(ItemStack.of(Material.BAKED_POTATO), trappedNewbieKey("potato_to_baked_potato"))
			.withBurnChance(0.2)
			.addIngredients(Material.POTATO)
			.register();
		new FireCraft(ItemStack.of(Material.CHARCOAL), trappedNewbieKey("logs_to_charcoal"))
			.withBurnChance(0.2)
			.addIngredients(Tag.LOGS.getValues())
			.register();
		new FireCraft(ItemStack.empty(), trappedNewbieKey("tnt_exploding"))
			.withAction((player, loc) -> loc.getWorld().spawn(loc, TNTPrimed.class, tnt -> tnt.setSource(player)))
			.addIngredients(Material.TNT)
			.register();
		new FireCraft(ItemStack.empty(), trappedNewbieKey("tnt_minecart_exploding"))
			.withAction((player, loc) -> loc.getWorld().spawn(loc, ExplosiveMinecart.class, minecart -> {
				if (player != null) new PlayerIgniteExplosiveMinecartEvent(player, minecart).callEvent();
				minecart.explode();
			}))
			.addIngredients(Material.TNT_MINECART)
			.register();
		new FireCraft(ItemStack.empty(), trappedNewbieKey("creeper_heart_exploding"))
			.withAction((player, loc) -> loc.getWorld().createExplosion(loc, 7, true, true))
			.addIngredients(RequiemItems.CREEPER_HEART)
			.register();

		// Tweaked vanilla crafts
		new ShapedCraft(ItemStack.of(Material.CAMPFIRE), trappedNewbieKey("campfire"), "SS", "LL")
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('L', Tag.LOGS.getValues())
			.register();

		addFuels();
		removeRecipes();
		makeIngredientReplacements();
	}

	private static Material figureOutLog(Material type, String suffix) {
		Material logType = Material.matchMaterial(type.key().value().replace(suffix, "log"));
		if (logType == null) logType = Material.matchMaterial(type.key().value().replace(suffix, "stem"));
		if (logType == null && type.key().value().startsWith("bamboo_")) logType = Material.BAMBOO_BLOCK;
		if (logType == null) throw new IllegalArgumentException("Couldn't find log item for " + type.key());
		return logType;
	}

	private static void addFlowerBouquetRecipe() {
		var recipe = new ShapelessCraft(ItemStack.of(TrappedNewbieItems.FLOWER_BOUQUET), trappedNewbieKey("flower_bouquet"))
			.addIngredients('S', Material.STRING, TrappedNewbieItems.TWINE); // HORSEHAIR
		for (int i = 0; i < 7; i++)
			recipe.addIngredients(MiscUtil.rotate(i, Tag.ITEMS_FLOWERS.getValues()));
		recipe.withPreCheck(uniqueIngredientsCheck(4));
		recipe.register();
	}

	private static Consumer<ItemCraftPrepareEvent> uniqueIngredientsCheck(int minCount) {
		return event -> {
			Set<Material> ingredients = new HashSet<>();
			for (ItemStack item : event.getMatrix()) {
				if (ItemStack.isEmpty(item)) continue;
				if (ingredients.add(item.getType()) && ingredients.size() >= minCount)
					return;
			}
			if (minCount > ingredients.size())
				event.setResult(null);
		};
	}

	private static void addFuels() {
		Bukkit.addFuel(TrappedNewbieItems.ROUGH_STICK, 100);
		TrappedNewbieTags.BRANCHES.getValues().forEach(material -> Bukkit.addFuel(material, 100));
		TrappedNewbieTags.STICKS.getValues().forEach(material -> Bukkit.addFuel(material, 100));
	}

	private static void removeRecipes() {
		for (String recipe : new String[]{
			// Tweaked
			"stick", "stick_from_bamboo_item", "campfire"//, "soul_campfire"
		}) {
			removeRecipe(recipe);
		}

		Tag.PLANKS.getValues().forEach(r -> removeRecipe(r.key().value()));
	}

	private static void makeIngredientReplacements() {
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

	private static void addBranchRecipe(Material branch, Material sapling) {
		new ShapelessCraft(ItemStack.of(branch), trappedNewbieKey(sapling.key().value() + "_to_" + branch.key().value()))
			.withGroup("sapling_to_branches")
			.addIngredients(sapling)
			.register();
	}

	private static void addRockRecipe(Material rock, Material stone) {
		new ShapedCraft(ItemStack.of(stone), trappedNewbieKey(rock.key().value() + "_to_" + stone.key().value()), "RR", "RR")
			.withGroup("shards_to_block")
			.addIngredients('R', rock)
			.register();
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
		Set<ItemStack> items = new HashSet<>(); // Avoid duplicates if recipes already account for replacements
		Predicate<ItemStack> predicate = null;
		boolean modified = false;
		if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
			for (Material material : materialChoice.getChoices()) {
				List<ItemStack> replacements = map.get(material);
				if (replacements == null) {
					items.add(ItemStack.of(material));
				} else {
					modified = true;
					items.addAll(replacements);
				}
			}
		} else if (recipeChoice instanceof RecipeChoice.ExactChoice exactChoice) {
			predicate = exactChoice.getPredicate();
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

		var updatedChoice = new RecipeChoice.ExactChoice(items.toArray(ItemStack[]::new));
		if (predicate != null) updatedChoice.setPredicate(predicate);
		return updatedChoice;
	}

	private static void removeRecipe(String key, String... exempts) {
		for (String ignored : exempts) {
			if (!key.equals(ignored)) continue;
			if (Bukkit.getRecipe(NamespacedKey.minecraft(key)) != null)
				TrappedNewbie.logger().error("Ignored recipe now exists: {}", key);
			return;
		}
		if (!Bukkit.removeRecipe(NamespacedKey.minecraft(key)))
			TrappedNewbie.logger().error("Could not find vanilla recipe with key {}", key);
	}

}
