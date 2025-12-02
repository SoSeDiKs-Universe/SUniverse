package me.sosedik.trappednewbie.dataset;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.Repairable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import me.sosedik.delightfulfarming.dataset.DelightfulFarmingItems;
import me.sosedik.miscme.api.event.player.PlayerIgniteExplosiveMinecartEvent;
import me.sosedik.miscme.listener.misc.WaterAwareBottleReset;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.requiem.listener.item.SoulboundNecronomicon;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.impl.item.modifier.BucketModifier;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.trappednewbie.impl.item.modifier.ScrapModifier;
import me.sosedik.trappednewbie.impl.recipe.FletchingCrafting;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.trappednewbie.listener.block.LogStrippingGivesBarks;
import me.sosedik.trappednewbie.listener.item.FillingBowlWithWater;
import me.sosedik.trappednewbie.listener.misc.AllRecipesInRecipeBook;
import me.sosedik.utilizer.api.event.recipe.ItemCraftEvent;
import me.sosedik.utilizer.api.event.recipe.ItemCraftPrepareEvent;
import me.sosedik.utilizer.api.recipe.CraftingRecipeBuilder;
import me.sosedik.utilizer.api.recipe.CustomRecipe;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.impl.recipe.BrewingCraft;
import me.sosedik.utilizer.impl.recipe.CampfireCraft;
import me.sosedik.utilizer.impl.recipe.FireCraft;
import me.sosedik.utilizer.impl.recipe.FurnaceCraft;
import me.sosedik.utilizer.impl.recipe.ShapedCraft;
import me.sosedik.utilizer.impl.recipe.ShapelessCraft;
import me.sosedik.utilizer.impl.recipe.SmokingCraft;
import me.sosedik.utilizer.impl.recipe.StonecuttingCraft;
import me.sosedik.utilizer.impl.recipe.WaterCraft;
import me.sosedik.utilizer.util.DurabilityUtil;
import me.sosedik.utilizer.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.damage.DamageSource;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

@NullMarked
public class TrappedNewbieRecipes {

	private TrappedNewbieRecipes() {
		throw new IllegalStateException("Utility class");
	}

	private static final Map<Material, Integer> REPAIR_VALUES = new HashMap<>();

	public static void addRecipes() {
		REPAIR_VALUES.put(TrappedNewbieItems.FLINT_AXE, (int) TrappedNewbieItems.FLINT_AXE.getMaxDurability());
		REPAIR_VALUES.put(TrappedNewbieItems.FLINT_SHOVEL, (int) TrappedNewbieItems.FLINT_SHOVEL.getMaxDurability());
		REPAIR_VALUES.put(TrappedNewbieItems.FLINT_KNIFE, (int) TrappedNewbieItems.FLINT_KNIFE.getMaxDurability());
		REPAIR_VALUES.put(TrappedNewbieItems.FLINT_SHEARS, (int) TrappedNewbieItems.FLINT_SHEARS.getMaxDurability());
		REPAIR_VALUES.put(TrappedNewbieItems.FLINT_PICKAXE, (int) TrappedNewbieItems.FLINT_PICKAXE.getMaxDurability());
		REPAIR_VALUES.put(TrappedNewbieItems.IRON_KNIFE, (int) TrappedNewbieItems.IRON_KNIFE.getMaxDurability());
		REPAIR_VALUES.put(TrappedNewbieItems.GRASS_MESH, (int) TrappedNewbieItems.GRASS_MESH.getMaxDurability());
		REPAIR_VALUES.put(TrappedNewbieItems.FIRESTRIKER, (int) TrappedNewbieItems.FIRESTRIKER.getMaxDurability());
		REPAIR_VALUES.put(TrappedNewbieItems.LEATHER_GLOVES, (int) TrappedNewbieItems.LEATHER_GLOVES.getMaxDurability());
		REPAIR_VALUES.put(TrappedNewbieItems.FLOWER_BOUQUET, (int) TrappedNewbieItems.FLOWER_BOUQUET.getMaxDurability() / 2);
		REPAIR_VALUES.keySet().forEach(type -> {
			var dummyItem = ItemStack.of(type);
			if (!dummyItem.hasData(DataComponentTypes.REPAIRABLE)) throw new IllegalArgumentException("Not repairable: " + type);
			RegistryKeySet<ItemType> repairableTypes = dummyItem.getData(DataComponentTypes.REPAIRABLE).types();
			new ShapelessCraft(ItemStack.of(TrappedNewbieItems.SCRAP), trappedNewbieKey(type.key().value() + "_inv_repair")).withGroup("inv_repair").special().ignoreTypeCheck(true).withExemptLeftovers()
				.addIngredients(type, i -> {
					ItemStack repairable = i;
					if (repairable.getType() == TrappedNewbieItems.SCRAP) {
						repairable = ScrapModifier.extractScrap(i);
						if (repairable.isEmpty())
							return false;
					}
					return repairable.getType() == type && repairable.hasData(DataComponentTypes.REPAIRABLE);
				})
				.addIngredients(TrappedNewbieItems.SCRAP, i -> repairableTypes.contains(TypedKey.create(RegistryKey.ITEM, i.getType().asItemType().key())))
				.withPreCheck(event -> {
					Player player = event.getPlayer();
					if (player == null) {
						event.setResult(null);
						return;
					}

					ItemStack scrap = null;
					ItemStack ingredient = null;
					for (ItemStack item : event.getMatrix()) {
						if (ItemStack.isEmpty(item)) continue;

						if (item.getType() == TrappedNewbieItems.SCRAP || item.hasData(DataComponentTypes.REPAIRABLE))
							scrap = item;
						else
							ingredient = item;

						if (ingredient != null && scrap != null)
							break;
					}

					if (ingredient == null || scrap == null) {
						event.setResult(null);
						return;
					}

					ItemStack brokenItem = scrap.getType() == TrappedNewbieItems.SCRAP ? ScrapModifier.extractScrap(scrap) : scrap;
					if (brokenItem.isEmpty()
						|| !brokenItem.hasData(DataComponentTypes.REPAIRABLE)
						|| !brokenItem.hasData(DataComponentTypes.DAMAGE)
						|| !brokenItem.hasData(DataComponentTypes.MAX_DAMAGE)
						|| !brokenItem.hasDamage()) {
						event.setResult(null);
						return;
					}

					Repairable data = brokenItem.getData(DataComponentTypes.REPAIRABLE);
					assert data != null;
					if (!data.types().contains(TypedKey.create(RegistryKey.ITEM, ingredient.getType().asItemType().key()))) {
						event.setResult(null);
						return;
					}

					int repair = REPAIR_VALUES.containsKey(brokenItem.getType()) ? REPAIR_VALUES.get(brokenItem.getType()) : (int) Math.floor(brokenItem.getData(DataComponentTypes.MAX_DAMAGE) * 0.25);

					brokenItem = brokenItem.clone();
					brokenItem.repair(repair);

					event.setResult(brokenItem);
				})
				.register();
		});

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.PAPER_PLANE, 3), trappedNewbieKey("paper_plane"), "P P", " P ")
			.withGroup("paper_plane")
			.addIngredients('P', Material.PAPER)
			.register();

		new ShapelessCraft(ItemStack.of(TrappedNewbieItems.TWINE), trappedNewbieKey("twine"))
			.addIngredients(TrappedNewbieItems.FIBER, 3)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_KNIFE), trappedNewbieKey("flint_knife"), "FT", "FS")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE)
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_AXE), trappedNewbieKey("flint_axe"), "FF", "ST")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE)
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_SHEARS), trappedNewbieKey("flint_shears_1"), "F ", "TF")
			.withGroup("flint_shears")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE)
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();
		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_SHEARS), trappedNewbieKey("flint_shears_2"), "FT", " F")
			.withGroup("flint_shears")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE)
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.IRON_KNIFE), trappedNewbieKey("iron_knife"), "IT", "IS")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE)
			.addIngredients('S', Material.STICK)
			.addIngredients('I', Material.IRON_INGOT)
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
		new ShapelessCraft(ItemStack.of(TrappedNewbieItems.BAMBOOS_STICK), trappedNewbieKey("bamboos_stick"))
			.addIngredients(Material.BAMBOO)
			.addIngredients(UtilizerTags.KNIFES.getValues())
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.GRASS_MESH), trappedNewbieKey("grass_mesh"), "TS", "ST")
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.COBBLESTONE_HAMMER), trappedNewbieKey("cobblestone_hammer"), "CT", "SC")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE)
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('C', Material.COBBLESTONE)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_SHOVEL), trappedNewbieKey("flint_shovel_1"), "FT", "S ")
			.withGroup("flint_shovel")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE)
			.addIngredients('F', TrappedNewbieItems.FLAKED_FLINT)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLINT_SHOVEL), trappedNewbieKey("flint_shovel_2"), "F ", "ST")
			.special()
			.withGroup("flint_shovel")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('S', Material.STICK, TrappedNewbieItems.ROUGH_STICK)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE)
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
			.special()
			.withGroup("steel_and_flint")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('F', Material.FLINT)
			.addIngredients('S', Material.IRON_INGOT)
			.register();
		new ShapedCraft(ItemStack.of(TrappedNewbieItems.STEEL_AND_FLINT), trappedNewbieKey("steel_and_flint_2"), "F", "S")
			.special()
			.withGroup("steel_and_flint")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('F', Material.FLINT)
			.addIngredients('S', Material.IRON_INGOT)
			.register();
		new ShapedCraft(ItemStack.of(TrappedNewbieItems.STEEL_AND_FLINT), trappedNewbieKey("steel_and_flint_3"), "FS")
			.special()
			.withGroup("steel_and_flint")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('F', Material.FLINT)
			.addIngredients('S', Material.IRON_INGOT)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.CLAY_KILN), trappedNewbieKey("clay_kiln"), "CCC", "C C", "CCC")
			.addIngredients('C', Material.CLAY_BALL)
			.register();

		new ShapelessCraft(ItemStack.of(TrappedNewbieItems.SLEEPING_BAG), trappedNewbieKey("sleeping_bag"))
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE)
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
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('L', Tag.LEAVES.getValues())
			.addIngredients('S', Material.STICK)
			.register();
		new ShapedCraft(ItemStack.of(TrappedNewbieItems.FLUTE), trappedNewbieKey("flute"), " LS", " S ", "S  ")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('L', Tag.LEAVES.getValues())
			.addIngredients('S', Material.STICK)
			.register();
		new ShapedCraft(ItemStack.of(TrappedNewbieItems.RATTLE), trappedNewbieKey("rattle"), " WW", " BW", "S  ")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('W', Tag.LOGS_THAT_BURN.getValues())
			.addIngredients('B', Material.STRING, TrappedNewbieItems.TWINE)
			.addIngredients('S', Material.STICK)
			.register();
		TrappedNewbieTags.DRUMS.getValues().forEach(type -> {
			new ShapedCraft(ItemStack.of(type), type.getKey(), "EEE", "LWL", "WLW")
				.withCategory(CraftingBookCategory.EQUIPMENT)
				.withGroup("drum")
				.addIngredients('E', UtilizerTags.HIDES.getValues())
				.addIngredients('E', Material.LEATHER)
				.addIngredients('L', figureOutLog(type, "drum"))
				.addIngredients('W', Tag.WOOL.getValues())
				.register();
		});

		new ShapelessCraft(ItemStack.of(TrappedNewbieItems.RAW_HIDE), trappedNewbieKey("raw_hide"))
			.addIngredients(UtilizerTags.HIDES.getValues())
			.addIngredients(UtilizerTags.KNIFES.getValues())
			.register();
		new ShapedCraft(ItemStack.of(Material.LEATHER), trappedNewbieKey("leather"), "HH", "HH") // TODO temporary recipe
			.addIngredients('H', TrappedNewbieItems.RAW_HIDE)
			.register();

		new ShapelessCraft(ItemStack.of(Material.PAPER), trappedNewbieKey("paper_from_birch_barks"))
			.addIngredients(TrappedNewbieItems.BIRCH_BARK, 3)
			.register();

		new ShapelessCraft(ItemStack.of(TrappedNewbieItems.CHARCOAL_FILTER, 3), trappedNewbieKey("charcoal_filter"))
			.addIngredients(Material.PAPER, Material.CHARCOAL, Material.PAPER)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.CACTUS_BOWL), trappedNewbieKey("cactus_bowl"), "C C", " C ")
			.addIngredients('C', DelightfulFarmingItems.CACTUS_FLESH)
			.register();

		new ShapelessCraft(ItemStack.of(Material.GLASS_BOTTLE), trappedNewbieKey("glass_bottle_from_shards"))
			.addIngredients('S', TrappedNewbieItems.TWINE, Material.STRING)
			.addIngredients(TrappedNewbieTags.GLASS_SHARDS.getValues(), 2)
			.register();

		FillingBowlWithWater.BOWLS_BOTTLES.forEach((bowl, filledBowl) -> {
			if (bowl == Material.GLASS_BOTTLE) return;
			new ShapelessCraft(ItemStack.of(bowl), trappedNewbieKey(bowl.key().value() + "_emptying"))
				.special()
				.addIngredients(filledBowl)
				.withExemptLeftovers()
				.register();
		});
		TrappedNewbieTags.CANTEENS.getValues().forEach(canteen -> {
			new ShapelessCraft(ItemStack.of(canteen), trappedNewbieKey(canteen.key().value() + "_emptying"))
				.special()
				.addIngredients(TrappedNewbieItems.CANTEEN)
				.withExemptLeftovers()
				.withPreCheck(event -> {
					ItemStack ingredient = null;
					for (ItemStack item : event.getMatrix()) {
						if (ItemStack.isEmpty(item)) continue;

						ingredient = item;
						break;
					}
					if (ingredient == null) {
						event.setResult(null);
						return;
					}
					event.setResult(ScrapModifier.makeScrap(ingredient));
				})
				.register();
		});

		new ShapedCraft(ScrapModifier.makeScrap(ItemStack.of(TrappedNewbieItems.CANTEEN)), trappedNewbieKey("canteen"), "TLT", "LIL", "LLL")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('T', Material.STRING, TrappedNewbieItems.TWINE)
			.addIngredients('L', Material.LEATHER)
			.addIngredients('I', Material.INK_SAC, Material.GLOW_INK_SAC)
			.register();

		FillingBowlWithWater.BOWLS_BOTTLES.forEach((bowl, filledBowl) -> {
			new ShapelessCraft(Objects.requireNonNull(getFilled(ItemStack.of(bowl), ThirstData.DrinkType.CACTUS_JUICE)), trappedNewbieKey("cactus_" + filledBowl.key().value()))
				.withGroup("cactus_juice")
				.addIngredients(DelightfulFarmingItems.CACTUS_FLESH)
				.addIngredients(Material.CACTUS_FLOWER)
				.addIngredients(bowl)
				.register();
		});

		new ShapedCraft(() -> {
				var item = ItemStack.of(RequiemItems.NECRONOMICON);
				item.addUnsafeEnchantment(Enchantment.VANISHING_CURSE, 1);
				return item;
			}, trappedNewbieKey("necronomicon"), "RLR", "LBF", "RLR")
			.addIngredients('R', Material.ROTTEN_FLESH)
			.addIngredients('L', Material.LEATHER)
			.addIngredients('B', Tag.ITEMS_BOOKSHELF_BOOKS.getValues())
			.addIngredients('F', Material.FEATHER)
			.withPreCheck(event -> {
				if (event.getPlayer() == null)
					event.setResult(null);
			})
			.withCraftCheck(event -> {
				Player player = event.getPlayer();
				if (player == null) {
					event.setResult(null);
					return;
				}
				event.setResult(SoulboundNecronomicon.getNecronomicon(player));

				TrappedNewbieAdvancements.GET_A_NECRONOMICON.awardAllCriteria(player);
				LivingEntity target = PossessingPlayer.getPossessed(player);
				if (target == null) target = player;
				target.damage(Math.floor(player.getHealth() / 2), DamageSource.builder(TrappedNewbieDamageTypes.SUICIDE).build());
			})
			.register();

		new ShapelessCraft(ItemStack.of(TrappedNewbieItems.WIND_IN_A_BOTTLE), trappedNewbieKey("wind_in_a_bottle"))
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients(Material.GLASS_BOTTLE)
			.addIngredients(Material.WIND_CHARGE)
			.register();

		for (DyeColor dyeColor : DyeColor.values()) {
			String colorKey = dyeColor.name().toLowerCase(Locale.US);
			Material material = Material.matchMaterial(colorKey + "_wool");
			if (material == null) continue;

			var item = ItemStack.of(TrappedNewbieItems.HANG_GLIDER);
			if (dyeColor != DyeColor.WHITE)
				item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(dyeColor.getColor()));

			new ShapedCraft(item, trappedNewbieKey(colorKey + "_wind_glider"), "PWW", "SPW", " SP").withGroup("wind_glider")
				.withCategory(CraftingBookCategory.EQUIPMENT)
				.addIngredients('P', Material.PHANTOM_MEMBRANE)
				.addIngredients('W', material)
				.addIngredients('S', Material.STICK)
				.register();
		}

		new ShapedCraft(BucketModifier.BucketType.CLAY.save(ItemStack.of(Material.BUCKET)), trappedNewbieKey("clay_bucket"), "C C", " C ")
			.addIngredients('C', Material.CLAY_BALL)
			.register();
		new CampfireCraft(BucketModifier.BucketType.CERAMIC.save(ItemStack.of(Material.BUCKET)), 20 * 20, trappedNewbieKey("ceramic_bucket"))
			.withGroup("ceramic_bucket")
			.addIngredientItems(BucketModifier.BucketType.CLAY.save(ItemStack.of(Material.BUCKET)), item -> BucketModifier.BucketType.fromBucket(item) == BucketModifier.BucketType.CLAY)
			.register();
		new FurnaceCraft(BucketModifier.BucketType.CERAMIC.save(ItemStack.of(Material.BUCKET)), 20 * 20, trappedNewbieKey("ceramic_bucket"))
			.withGroup("ceramic_bucket")
			.addIngredientItems(BucketModifier.BucketType.CLAY.save(ItemStack.of(Material.BUCKET)), item -> BucketModifier.BucketType.fromBucket(item) == BucketModifier.BucketType.CLAY)
			.register();

		Tag.PLANKS.getValues().forEach(planks -> {
			BucketModifier.BucketType bucketType = BucketModifier.BucketType.valueOf(planks.name().replace("_PLANKS", ""));

			new ShapedCraft(bucketType.save(ItemStack.of(Material.BUCKET)), bucketType.getKey(), "P P", " P ")
				.withGroup("wooden_bucket")
				.addIngredients('P', planks)
				.register();
			new StonecuttingCraft(bucketType.save(ItemStack.of(Material.BUCKET)), bucketType.getKey())
				.withGroup("wooden_bucket")
				.addIngredients('P', planks)
				.register();
		});

		new ShapedCraft(BucketModifier.BucketType.GOLDEN.save(ItemStack.of(Material.BUCKET)), trappedNewbieKey("golden_bucket"), "C C", " C ")
			.addIngredients('C', Material.GOLD_INGOT)
			.register();
		new ShapedCraft(BucketModifier.BucketType.DIAMOND.save(ItemStack.of(Material.BUCKET)), trappedNewbieKey("diamond_bucket"), "C C", " C ")
			.addIngredients('C', Material.DIAMOND)
			.register();
		Bukkit.addRecipe(new SmithingTransformRecipe(trappedNewbieKey("netherite_bucket"), BucketModifier.BucketType.NETHERITE.save(ItemStack.of(Material.BUCKET)),
			new RecipeChoice.MaterialChoice(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
			CustomRecipe.makeChoice(BucketModifier.BucketType.DIAMOND.save(ItemStack.of(Material.BUCKET)), item -> BucketModifier.BucketType.fromBucket(item) == BucketModifier.BucketType.DIAMOND),
			new RecipeChoice.MaterialChoice(Material.NETHERITE_INGOT),
			true
		));

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.LEATHER_GLOVES), trappedNewbieKey("leather_gloves"), "L L")
			.withCategory(CraftingBookCategory.EQUIPMENT)
			.addIngredients('L', Material.LEATHER)
			.register();

		new ShapelessCraft(ItemStack.of(Material.GREEN_DYE), trappedNewbieKey("green_dye"))
			.addIngredients(Material.BLUE_DYE, Material.YELLOW_DYE)
			.register();

		new ShapelessCraft(ItemStack.of(Material.BROWN_DYE), trappedNewbieKey("brown_dye"))
			.addIngredients(Material.ORANGE_DYE, Material.BLACK_DYE)
			.register();

		new ShapedCraft(ItemStack.of(TrappedNewbieItems.LETTER), trappedNewbieKey("letter"), " L ", "LLL")
			.addIngredients('L', Material.PAPER)
			.register();
		
		new ShapelessCraft(LetterModifier.getFriendshipLetter(), trappedNewbieKey("friendship_letter"))
			.addIngredients(TrappedNewbieItems.LETTER, i -> !LetterModifier.isFriendshipLetter(i))
			.addIngredients(TrappedNewbieItems.FLOWER_BOUQUET, DurabilityUtil::isNew)
			.register();

		Consumer<ItemCraftEvent> craftCheck = event -> {
			ItemStack result = event.getResult();
			if (ItemStack.isEmpty(result)) return;

			for (ItemStack item : event.getMatrix()) {
				if (!ItemStack.isType(item, Material.AXOLOTL_BUCKET)) continue;

				NBT.getComponents(item, nbt -> {
					ReadableNBT bucketEntityData = nbt.getCompound("minecraft:bucket_entity_data");
					if (bucketEntityData == null) return;

					NBT.modifyComponents(result, (Consumer<ReadWriteNBT>) itemNbt -> itemNbt.getOrCreateCompound("minecraft:bucket_entity_data").mergeCompound(bucketEntityData));
				});
				event.setResult(result);

				return;
			}
		};
		new CampfireCraft(ItemStack.of(TrappedNewbieItems.BOILED_AXOLOTL_BUCKET), 40 * 20, trappedNewbieKey("boiled_axolotl"))
			.withExemptLeftovers()
			.addIngredients(Material.AXOLOTL_BUCKET)
			.withCraftCheck(craftCheck)
			.register();
		new SmokingCraft(ItemStack.of(TrappedNewbieItems.BOILED_AXOLOTL_BUCKET), 12 * 20, trappedNewbieKey("boiled_axolotl"))
			.withExemptLeftovers()
			.addIngredients(Material.AXOLOTL_BUCKET)
			.withCraftCheck(craftCheck)
			.register();

		new FletchingCrafting(ItemStack.of(Material.SPECTRAL_ARROW), trappedNewbieKey("spectral_arrow"))
			.addIngredients(Material.GLOWSTONE_DUST)
			.register();
		new FletchingCrafting(ItemStack.of(Material.TIPPED_ARROW), trappedNewbieKey("tipped_arrow"))
			.addIngredients('B', Material.GLASS_BOTTLE, Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION)
			.register();
		new FletchingCrafting(ItemStack.of(RequiemItems.FIRE_ARROW), trappedNewbieKey("fire_arrow"))
			.addIngredients(UtilizerTags.REGULAR_TORCHES.getValues())
			.register();

		addDrinkRecipes();

		addFlowerBouquetRecipe();

		addBarkRecipes(Material.ACACIA_LOG, Material.STRIPPED_ACACIA_LOG, Material.ACACIA_WOOD, Material.STRIPPED_ACACIA_WOOD, TrappedNewbieItems.ACACIA_BARK);
		addBarkRecipes(Material.BIRCH_LOG, Material.STRIPPED_BIRCH_LOG, Material.BIRCH_WOOD, Material.STRIPPED_BIRCH_WOOD, TrappedNewbieItems.BIRCH_BARK);
		addBarkRecipes(Material.DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_LOG, Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_WOOD, TrappedNewbieItems.DARK_OAK_BARK);
		addBarkRecipes(Material.JUNGLE_LOG, Material.STRIPPED_JUNGLE_LOG, Material.JUNGLE_WOOD, Material.STRIPPED_JUNGLE_WOOD, TrappedNewbieItems.JUNGLE_BARK);
		addBarkRecipes(Material.SPRUCE_LOG, Material.STRIPPED_SPRUCE_LOG, Material.SPRUCE_WOOD, Material.STRIPPED_SPRUCE_WOOD, TrappedNewbieItems.SPRUCE_BARK);
		addBarkRecipes(Material.OAK_LOG, Material.STRIPPED_OAK_LOG, Material.OAK_WOOD, Material.STRIPPED_OAK_WOOD, TrappedNewbieItems.OAK_BARK);
		addBarkRecipes(Material.CHERRY_LOG, Material.STRIPPED_CHERRY_LOG, Material.CHERRY_WOOD, Material.STRIPPED_CHERRY_WOOD, TrappedNewbieItems.CHERRY_BARK);
		addBarkRecipes(Material.MANGROVE_LOG, Material.STRIPPED_MANGROVE_LOG, Material.MANGROVE_WOOD, Material.STRIPPED_MANGROVE_WOOD, TrappedNewbieItems.MANGROVE_BARK);
		addBarkRecipes(Material.PALE_OAK_LOG, Material.STRIPPED_PALE_OAK_LOG, Material.PALE_OAK_WOOD, Material.STRIPPED_PALE_OAK_WOOD, TrappedNewbieItems.PALE_OAK_BARK);
		addBarkRecipes(Material.CRIMSON_STEM, Material.STRIPPED_CRIMSON_STEM, Material.CRIMSON_HYPHAE, Material.STRIPPED_CRIMSON_HYPHAE, TrappedNewbieItems.CRIMSON_BARK);
		addBarkRecipes(Material.WARPED_STEM, Material.STRIPPED_WARPED_STEM, Material.WARPED_HYPHAE, Material.STRIPPED_WARPED_HYPHAE, TrappedNewbieItems.WARPED_BARK);
		addBarkRecipes(Material.BAMBOO_BLOCK, Material.STRIPPED_BAMBOO_BLOCK, null, null, TrappedNewbieItems.BAMBOO_BARK);

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

		new WaterCraft(ItemStack.of(Material.WATER_BUCKET), trappedNewbieKey("water_bucket"))
			.addIngredients(Material.BUCKET)
			.register();
		new WaterCraft(ItemStack.of(TrappedNewbieItems.FILLED_BOWL), trappedNewbieKey("filled_bowl"))
			.addIngredients(Material.BOWL)
			.withAction(itemDrop -> itemDrop.setItemStack(ThirstData.of(itemDrop.getLocation().getBlock()).saveInto(itemDrop.getItemStack())))
			.register();
		new WaterCraft(ItemStack.of(TrappedNewbieItems.FILLED_CACTUS_BOWL), trappedNewbieKey("water_cactus_bowl"))
			.addIngredients(TrappedNewbieItems.CACTUS_BOWL)
			.withAction(itemDrop -> itemDrop.setItemStack(ThirstData.of(itemDrop.getLocation().getBlock()).saveInto(itemDrop.getItemStack())))
			.register();

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
		addBrews();
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

	private static void addDrinkRecipes() {
		FillingBowlWithWater.BOWLS_BOTTLES.values().forEach(filledBowl -> registerDrinkRecipes(filledBowl == Material.POTION ? WaterAwareBottleReset.getWaterBottle(1) : ItemStack.of(filledBowl), filledBowl.key().value()));
		TrappedNewbieTags.CANTEENS.getValues().forEach(canteen -> registerDrinkRecipes(ItemStack.of(canteen), canteen.key().value()));
	}

	private static void registerDrinkRecipes(ItemStack result, String key) {
		Material ingredientType = result.getType();
		new ShapelessCraft(ThirstData.of(result).withCooled().saveInto(result), trappedNewbieKey(key + "_cooling"))
			.withGroup("drink_cooling")
			.addIngredientItems(result, item -> !ThirstData.isCooled(item))
			.addIngredients(TrappedNewbieItems.ICE_CUBE)
			.withExemptLeftovers()
			.withPreCheck(event -> {
				ItemStack ingredient = null;
				for (ItemStack item : event.getMatrix()) {
					if (!ItemStack.isType(item, ingredientType)) continue;

					ingredient = item;
					break;
				}
				if (ingredient == null) {
					event.setResult(null);
					return;
				}
				ItemStack resultItem = ThirstData.of(ingredient).withCooled().saveInto(ingredient);
				event.setResult(resultItem);
			})
			.register();

		if (result.getType() == TrappedNewbieItems.DRAGON_FLASK) return;

		new ShapelessCraft(ThirstData.of(result).withThirstChance(0).saveInto(result), trappedNewbieKey(key + "_purifying"))
			.withGroup("drink_purifying")
			.addIngredientItems(result, item -> !ThirstData.isPure(item))
			.addIngredients(TrappedNewbieItems.CHARCOAL_FILTER)
			.withExemptLeftovers()
			.withPreCheck(event -> {
				ItemStack ingredient = null;
				for (ItemStack item : event.getMatrix()) {
					if (!ItemStack.isType(item, ingredientType)) continue;

					ingredient = item;
					break;
				}
				if (ingredient == null) {
					event.setResult(null);
					return;
				}
				ItemStack resultItem = ThirstData.of(ingredient).withThirstChance(0).saveInto(ingredient);
				event.setResult(resultItem);
			})
			.register();
	}

	private static void addFlowerBouquetRecipe() {
		var recipe = new ShapelessCraft(ItemStack.of(TrappedNewbieItems.FLOWER_BOUQUET), trappedNewbieKey("flower_bouquet"))
			.addIngredients('S', Material.STRING, TrappedNewbieItems.TWINE);
		for (int i = 0; i < 7; i++)
			recipe.addIngredients(MiscUtil.rotate(i, Tag.ITEMS_FLOWERS.getValues()));
		recipe.withPreCheck(uniqueIngredientsCheck(3));
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
		Bukkit.addFuel(TrappedNewbieItems.MAGMA_CUBE_BUCKET, 1000 * 20);
		Bukkit.addFuel(TrappedNewbieItems.MAGMA_CUBE_BOTTLE, 1000 * 20);
		TrappedNewbieTags.BRANCHES.getValues().forEach(material -> Bukkit.addFuel(material, 100));
		TrappedNewbieTags.STICKS.getValues().forEach(material -> Bukkit.addFuel(material, 100));
		UtilizerTags.LAVA_BUCKETS.getValues().forEach(material -> {
			if (material != Material.LAVA_BUCKET)
				Bukkit.addFuel(material, 1000 * 20);
		});

		Bukkit.addFuel(item -> {
			BucketModifier.BucketType bucketType = BucketModifier.BucketType.fromBucket(item);
			return bucketType != null && bucketType.isWooden() && DurabilityUtil.isNew(item) ? 10 * 20 : null;
		});
	}

	private static void addBrews() {
		new BrewingCraft(ItemStack.of(TrappedNewbieItems.MAGMA_CUBE_BUCKET), true, trappedNewbieKey("magma_cube_bucket"))
			.asBrewIngredient(PotionType.WATER, PotionType.MUNDANE)
			.asBrewIngredient(PotionType.AWKWARD, PotionType.FIRE_RESISTANCE, "fire_resistance_from_magma_cream")
			.register();
		new BrewingCraft(ItemStack.of(TrappedNewbieItems.MAGMA_CUBE_BOTTLE), true, trappedNewbieKey("magma_cube_bottle"))
			.asBrewIngredient(PotionType.WATER, PotionType.MUNDANE)
			.asBrewIngredient(PotionType.AWKWARD, PotionType.FIRE_RESISTANCE, "fire_resistance_from_magma_cream")
			.register();

		AllRecipesInRecipeBook.addVanillaPotionMix("fire_resistance_from_magma_cream", PotionType.FIRE_RESISTANCE, TrappedNewbieItems.MAGMA_CUBE_BUCKET, null, PotionType.LONG_FIRE_RESISTANCE);
		AllRecipesInRecipeBook.addVanillaPotionMix("fire_resistance_from_magma_cream", PotionType.FIRE_RESISTANCE, TrappedNewbieItems.MAGMA_CUBE_BOTTLE, null, PotionType.LONG_FIRE_RESISTANCE);
	}

	private static void removeRecipes() {
		for (String recipe : new String[]{
			"repair_item",
			// Tweaked
			"stick", "stick_from_bamboo_item", "campfire",// "soul_campfire",
			"leather", "arrow", "spectral_arrow", "tipped_arrow"
		}) {
			removeRecipe(recipe);
		}

		Tag.PLANKS.getValues().forEach(r -> removeRecipe(r.key().value()));
		Tag.WOOL.getValues().forEach(r -> removeRecipe("dye_" + r.key().value()));
		Tag.WOOL_CARPETS.getValues().forEach(r -> removeRecipe("dye_" + r.key().value()));
		Tag.BEDS.getValues().forEach(r -> removeRecipe("dye_" + r.key().value()));
	}

	private static void makeIngredientReplacements() {
		Map<Material, IngredientReplacement> replacements = new HashMap<>();

		addReplacements(replacements, Material.STICK, TrappedNewbieTags.STICKS, null);
		addReplacements(replacements, Material.SHEARS, UtilizerTags.SHEARS, null);
		addReplacements(replacements, Material.STRING, List.of(Material.STRING, TrappedNewbieItems.HORSEHAIR), null, NamespacedKey.minecraft("white_wool_from_string"));
		addReplacements(replacements, Material.RABBIT_HIDE, UtilizerTags.HIDES, null);
		List<Material> slimeBalls = List.of(Material.SLIME_BALL, TrappedNewbieItems.SLIME_BUCKET, TrappedNewbieItems.SLIME_BOTTLE);
		addReplacements(replacements, Material.SLIME_BALL, slimeBalls, item -> slimeBalls.contains(item.getType()));
		List<Material> magmaCreams = List.of(Material.MAGMA_CREAM, TrappedNewbieItems.MAGMA_CUBE_BUCKET, TrappedNewbieItems.MAGMA_CUBE_BOTTLE);
		addReplacements(replacements, Material.MAGMA_CREAM, magmaCreams, item -> magmaCreams.contains(item.getType()));

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

	private record IngredientReplacement(List<ItemStack> ingredients, List<NamespacedKey> exclusions, @Nullable Predicate<ItemStack> ingredientCheck) {}

	private static void addBarkRecipes(Material log, Material strippedLog, @Nullable Material wood, @Nullable Material strippedWood, Material bark) {
		LogStrippingGivesBarks.addBark(log, strippedLog, ItemStack.of(bark, 4));
		new ShapelessCraft(ItemStack.of(log), trappedNewbieKey(log.key().value() + "_from_barks"))
			.withGroup("log_from_barks")
			.addIngredients(strippedLog)
			.addIngredients(bark, 4)
			.register();
		if (wood != null && strippedWood != null) {
			LogStrippingGivesBarks.addBark(wood, strippedWood, ItemStack.of(bark, 6));
			new ShapelessCraft(ItemStack.of(wood), trappedNewbieKey(wood.key().value() + "_from_log_and_barks"))
				.withGroup("wood_log_and_barks")
				.addIngredients(log)
				.addIngredients(bark, 2)
				.register();
			new ShapelessCraft(ItemStack.of(wood), trappedNewbieKey(wood.key().value() + "_from_barks"))
				.withGroup("wood_from_barks")
				.addIngredients(strippedWood)
				.addIngredients(bark, 6)
				.register();
		}
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

	private static IngredientReplacement addReplacements(Map<Material, IngredientReplacement> map, Material type, Tag<Material> replacements, @Nullable Predicate<ItemStack> ingredientCheck, NamespacedKey... exclusions) {
		return addReplacements(map, type, replacements.getValues(), ingredientCheck, exclusions);
	}

	private static IngredientReplacement addReplacements(Map<Material, IngredientReplacement> map, Material type, Collection<Material> replacements, @Nullable Predicate<ItemStack> ingredientCheck, NamespacedKey... exclusions) {
		IngredientReplacement replacement = map.computeIfAbsent(type, k -> new IngredientReplacement(new ArrayList<>(), new ArrayList<>(), ingredientCheck));
		replacement.ingredients().addAll(replacements.stream().map(ItemStack::of).toList());
		if (exclusions.length > 0) replacement.exclusions().addAll(List.of(exclusions));
		return replacement;
	}

	private static boolean updateRecipe(Map<Material, IngredientReplacement> replacements, Recipe recipe) {
		boolean modified = false;
		if (recipe instanceof ShapedRecipe shapedRecipe) {
			Map<Character, RecipeChoice> choiceMap = shapedRecipe.getChoiceMap();
			for (Map.Entry<Character, RecipeChoice> entry : choiceMap.entrySet()) {
				RecipeChoice choice = updateChoice(replacements, entry.getValue(), ((Keyed) recipe).getKey());
				if (choice == null) continue;

				modified = true;
				shapedRecipe.setIngredient(entry.getKey(), choice);
			}
		} else if (recipe instanceof ShapelessRecipe shapelessRecipe) {
			List<RecipeChoice> choiceList = shapelessRecipe.getChoiceList();
			for (RecipeChoice choice : choiceList) {
				RecipeChoice recipeChoice = updateChoice(replacements, choice, ((Keyed) recipe).getKey());
				if (recipeChoice == null) continue;

				modified = true;
				shapelessRecipe.removeIngredient(choice);
				shapelessRecipe.addIngredient(recipeChoice);
			}
		}

		return modified;
	}

	private static @Nullable RecipeChoice updateChoice(Map<Material, IngredientReplacement> map, RecipeChoice recipeChoice, NamespacedKey recipeKey) {
		Set<ItemStack> items = new HashSet<>(); // Avoid duplicates if recipes already account for replacements
		Predicate<ItemStack> predicate = null;
		boolean modified = false;
		if (recipeChoice instanceof RecipeChoice.MaterialChoice materialChoice) {
			for (Material material : materialChoice.getChoices()) {
				IngredientReplacement replacements = map.get(material);
				if (replacements == null || replacements.exclusions().contains(recipeKey)) {
					items.add(ItemStack.of(material));
				} else {
					modified = true;
					predicate = replacements.ingredientCheck;
					items.addAll(replacements.ingredients());
				}
			}
		} else if (recipeChoice instanceof RecipeChoice.ExactChoice exactChoice) {
			predicate = exactChoice.getPredicate();
			for (ItemStack item : exactChoice.getChoices()) {
				IngredientReplacement replacements = map.get(item.getType());
				if (replacements == null || replacements.exclusions().contains(recipeKey)) {
					items.add(item);
				} else {
					modified = true;
					if (predicate == null)
						predicate = replacements.ingredientCheck;
					items.addAll(replacements.ingredients());
				}
			}
		}

		if (!modified) return null;

		var updatedChoice = new RecipeChoice.ExactChoice(items.toArray(ItemStack[]::new));
		if (predicate != null) {
			if (predicate instanceof CraftingRecipeBuilder.ItemPredicate(CraftingRecipeBuilder<?> recipe, char key))
				recipe.getIngredients().put(key, new ArrayList<>(items));
			updatedChoice.setPredicate(predicate);
		}

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

	public static @Nullable ItemStack getFilled(ItemStack item, ThirstData.DrinkType drinkType) {
		Material filledType = FillingBowlWithWater.BOWLS_BOTTLES.get(item.getType());
		if (filledType == null) {
			if (TrappedNewbieTags.CANTEENS.isTagged(item.getType()))
				filledType = item.getType();
			else
				return null;
		}

		return new ThirstData(0, 0, 0, null, drinkType, drinkType == ThirstData.DrinkType.CACTUS_JUICE).saveInto(ItemStack.of(filledType));
	}

}
