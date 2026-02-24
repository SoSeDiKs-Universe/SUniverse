package me.sosedik.trappednewbie.dataset;

import com.destroystokyo.paper.MaterialTags;
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
import me.sosedik.utilizer.impl.recipe.BlastingCraft;
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
import org.bukkit.Registry;
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
			.addIngredients('S', TrappedNewbieTags.BRANCHES.getValues())
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

		Material[] books = new Material[] {
			Material.BOOK, Material.ENCHANTED_BOOK, Material.WRITABLE_BOOK, Material.WRITTEN_BOOK, Material.KNOWLEDGE_BOOK
		};
		new ShapedCraft(ItemStack.of(Material.BOOKSHELF), trappedNewbieKey("bookshelf"), "PPP", "BBB", "PPP")
			.addIngredients('B', books)
			.addIngredients('P', Tag.PLANKS.getValues())
			.register();

		new ShapelessCraft(ItemStack.of(TrappedNewbieItems.RAW_HIDE), trappedNewbieKey("raw_hide"))
			.addIngredients(UtilizerTags.HIDES.getValues())
			.addIngredients(UtilizerTags.KNIFES.getValues())
			.register();
		new ShapedCraft(ItemStack.of(Material.LEATHER), trappedNewbieKey("leather"), "HH", "HH") // TODO temporary recipe
			.addIngredients('H', TrappedNewbieItems.RAW_HIDE)
			.register();

		new ShapelessCraft(ItemStack.of(Material.PAPER), trappedNewbieKey("paper_from_birch_barks"))
			.withGroup("paper")
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
		new ShapelessCraft(ItemStack.of(TrappedNewbieItems.GLASS_SHARD, 2), trappedNewbieKey("glass_bottle_to_glass_shards"))
			.addIngredients(Material.GLASS_BOTTLE)
			.addIngredients(TrappedNewbieTags.HAMMERS.getValues())
			.register();
		new StonecuttingCraft(ItemStack.of(TrappedNewbieItems.GLASS_SHARD, 2), trappedNewbieKey("glass_bottle_to_glass_shards"))
			.withGroup(formatStonecutterGroup(TrappedNewbieItems.GLASS_SHARD))
			.addIngredients(Material.GLASS_BOTTLE)
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

		new ShapelessCraft(ItemStack.of(Material.BOWL), trappedNewbieKey("stew_emptying"))
			.special()
			.withGroup("bowl_emptying")
			.addIngredients('B', Material.MUSHROOM_STEW, Material.RABBIT_STEW, Material.SUSPICIOUS_STEW, Material.BEETROOT_SOUP, DelightfulFarmingItems.GLOWGURT)
			.register();

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

		for (Material carpet : Tag.WOOL_CARPETS.getValues()) {
			Material wool = Material.matchMaterial(carpet.key().value().replace("carpet", "wool"));
			assert wool != null;
			// Wool to carpets with a knife
			new ShapelessCraft(ItemStack.of(carpet, 3), trappedNewbieKey(carpet.key().value() + "_from_wool")).withGroup("carpets_from_wool")
				.addIngredients('S', UtilizerTags.KNIFES.getValues())
				.addIngredients(wool)
				.register();
			// Wool to carpets in a stonecutter
			new StonecuttingCraft(ItemStack.of(carpet, 3), trappedNewbieKey(carpet.key().value() + "_to_carpet")).withGroup("wool_to_carpet_from_stonecutting")
				.addIngredients(wool)
				.register();
			// Carpets to wool
			new ShapelessCraft(ItemStack.of(wool), trappedNewbieKey(carpet.key().value() + "_to_wool")).withGroup("carpets_to_wool")
				.addIngredients(carpet, 3)
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
		});

		new ShapedCraft(BucketModifier.BucketType.GOLDEN.save(ItemStack.of(Material.BUCKET)), trappedNewbieKey("golden_bucket"), "C C", " C ")
			.addIngredients('C', Material.GOLD_INGOT)
			.register();
		new ShapedCraft(BucketModifier.BucketType.DIAMOND.save(ItemStack.of(Material.BUCKET)), trappedNewbieKey("diamond_bucket"), "C C", " C ")
			.addIngredients('C', Material.DIAMOND)
			.register();
		Bukkit.addRecipe(new SmithingTransformRecipe(trappedNewbieKey("netherite_bucket"), BucketModifier.BucketType.NETHERITE.save(ItemStack.of(Material.BUCKET)),
			RecipeChoice.itemType(ItemType.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
			CustomRecipe.makeChoice(BucketModifier.BucketType.DIAMOND.save(ItemStack.of(Material.BUCKET)), item -> BucketModifier.BucketType.fromBucket(item) == BucketModifier.BucketType.DIAMOND),
			RecipeChoice.itemType(ItemType.NETHERITE_INGOT),
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

		Material[] toDirt = new Material[] {
			Material.GRASS_BLOCK, Material.PODZOL, Material.MYCELIUM,
			Material.DIRT_PATH, Material.FARMLAND,
			Material.COARSE_DIRT, Material.MUD
		};
		new ShapelessCraft(ItemStack.of(Material.DIRT), trappedNewbieKey("grass_block_to_dirt"))
			.addIngredients('G', toDirt)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.DIRT), trappedNewbieKey("grass_block_to_dirt"))
			.withGroup(formatStonecutterGroup(Material.DIRT))
			.addIngredients('D', toDirt)
			.register();

		new ShapelessCraft(ItemStack.of(Material.SNOW_BLOCK), trappedNewbieKey("snow_block"))
			.addIngredients(Material.SNOWBALL, 8)
			.register();
		new ShapelessCraft(ItemStack.of(Material.SNOWBALL, 8), trappedNewbieKey("snowball"))
			.withGroup("snowball")
			.addIngredients(Material.SNOW_BLOCK)
			.register();
		new ShapelessCraft(ItemStack.of(Material.SNOWBALL), trappedNewbieKey("snowball_from_snow"))
			.withGroup("snowball")
			.addIngredients(Material.SNOW)
			.register();
		new ShapelessCraft(ItemStack.of(Material.SNOW), trappedNewbieKey("snow"))
			.addIngredients(Material.SNOWBALL)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.SNOWBALL, 8), trappedNewbieKey("snow_block_to_snowball"))
			.withGroup(formatStonecutterGroup(Material.SNOWBALL))
			.addIngredients(Material.SNOW_BLOCK)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.SNOW, 8), trappedNewbieKey("snow_block_to_snow"))
			.withGroup(formatStonecutterGroup(Material.SNOW))
			.addIngredients(Material.SNOW_BLOCK)
			.register();

		// MCCheck: 1.21.11, new logs
		List.of(
			Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG, Material.JUNGLE_LOG,
			Material.ACACIA_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG, Material.CHERRY_LOG,
			Material.PALE_OAK_LOG,
			Material.BAMBOO_BLOCK,
			Material.CRIMSON_STEM, Material.WARPED_STEM
		).forEach(log -> {
			Material strippedLog = Material.matchMaterial("stripped_" + log.key().value());
			Material wood = log == Material.BAMBOO_BLOCK ? null : Material.matchMaterial(log.key().value().replace("log", "wood").replace("stem", "hyphae"));
			Material strippedWood = log == Material.BAMBOO_BLOCK ? null : Material.matchMaterial("stripped_" + log.key().value().replace("log", "wood").replace("stem", "hyphae"));
			Material planks = Material.matchMaterial(log.key().value().replaceAll("(log|stem|block)", "planks"));
			if (strippedLog == null) {
				TrappedNewbie.logger().warn("Couldn't find stripped log for a log: {}", log.key());
				return;
			}
			if (wood == null) {
				if (log != Material.BAMBOO_BLOCK)
					TrappedNewbie.logger().warn("Couldn't find wood for a log: {}", log.key());
				return;
			}
			if (planks == null) {
				TrappedNewbie.logger().warn("Couldn't find planks for a log: {}", log.key());
				return;
			}

			// Logs -> Planks
			new StonecuttingCraft(ItemStack.of(planks, 4), trappedNewbieKey(log.key().value() + "_to_" + planks.key().value()))
				.withGroup(formatStonecutterGroup(planks))
				.addIngredients(log)
				.register();
			new StonecuttingCraft(ItemStack.of(planks, 4), trappedNewbieKey(strippedLog.key().value() + "_to_" + planks.key().value()))
				.withGroup(formatStonecutterGroup(planks))
				.addIngredients(strippedLog)
				.register();
			new StonecuttingCraft(ItemStack.of(planks, 4), trappedNewbieKey(wood.key().value() + "_to_" + planks.key().value()))
				.withGroup(formatStonecutterGroup(planks))
				.addIngredients(wood)
				.register();
			new StonecuttingCraft(ItemStack.of(planks, 4), trappedNewbieKey(strippedWood.key().value() + "_to_" + planks.key().value()))
				.withGroup(formatStonecutterGroup(planks))
				.addIngredients(strippedWood)
				.register();

			// Wood stripping
			new StonecuttingCraft(ItemStack.of(log), trappedNewbieKey(wood.key().value() + "_to_" + log.key().value()))
				.withGroup(formatStonecutterGroup(log))
				.addIngredients(wood)
				.register();
			new StonecuttingCraft(ItemStack.of(strippedLog), trappedNewbieKey(wood.key().value() + "_to_" + strippedLog.key().value()))
				.withGroup(formatStonecutterGroup(strippedLog))
				.addIngredients(wood)
				.register();
			new StonecuttingCraft(ItemStack.of(strippedWood), trappedNewbieKey(wood.key().value() + "_to_" + strippedWood.key().value()))
				.withGroup(formatStonecutterGroup(strippedWood))
				.addIngredients(wood)
				.register();

			// Log stripping
			new StonecuttingCraft(ItemStack.of(strippedLog), trappedNewbieKey(log.key().value() + "_to_" + strippedLog.key().value()))
				.withGroup(formatStonecutterGroup(strippedLog))
				.addIngredients(log)
				.register();
		});

		new StonecuttingCraft(ItemStack.of(Material.CHIPPED_ANVIL), trappedNewbieKey("anvil_to_chipped_anvil"))
			.withGroup("anvil_damaging")
			.addIngredients(Material.ANVIL)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.DAMAGED_ANVIL), trappedNewbieKey("anvil_to_damaged_anvil"))
			.withGroup("anvil_damaging")
			.addIngredients(Material.ANVIL)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.DAMAGED_ANVIL), trappedNewbieKey("chipped_anvil_to_damaged_anvil"))
			.withGroup("anvil_damaging")
			.addIngredients(Material.CHIPPED_ANVIL)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.CHEST), trappedNewbieKey("trapped_chest_to_chest"))
			.withGroup(formatStonecutterGroup(Material.CHEST))
			.addIngredients(Material.TRAPPED_CHEST)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.IRON_CHAIN), trappedNewbieKey("iron_ingot_to_iron_chain"))
			.withGroup(formatStonecutterGroup(Material.IRON_CHAIN))
			.addIngredients(Material.IRON_INGOT)
			.register();
		new BlastingCraft(ItemStack.of(Material.IRON_NUGGET, 8), 5 * 20, trappedNewbieKey("iron_chain_to_iron_nuggets"))
			.withExp(2)
			.addIngredients(Material.IRON_CHAIN)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.IRON_BARS, 2), trappedNewbieKey("iron_ingot_to_iron_bars"))
			.withGroup(formatStonecutterGroup(Material.IRON_BARS))
			.addIngredients(Material.IRON_INGOT)
			.register();
		new BlastingCraft(ItemStack.of(Material.IRON_NUGGET, 4), 50, trappedNewbieKey("iron_bars_to_iron_nuggets"))
			.withExp(1)
			.addIngredients(Material.IRON_BARS)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.COPPER_CHAIN), trappedNewbieKey("copper_ingot_to_copper_chain"))
			.withGroup(formatStonecutterGroup(Material.COPPER_CHAIN))
			.addIngredients(Material.COPPER_INGOT)
			.register();
		new BlastingCraft(ItemStack.of(Material.COPPER_NUGGET, 8), 5 * 20, trappedNewbieKey("copper_chain_to_copper_nuggets"))
			.withExp(2)
			.addIngredients('C',
				Material.COPPER_CHAIN, Material.EXPOSED_COPPER_CHAIN, Material.WEATHERED_COPPER_CHAIN, Material.OXIDIZED_COPPER_CHAIN,
				Material.WAXED_COPPER_CHAIN, Material.WAXED_EXPOSED_COPPER_CHAIN, Material.WAXED_WEATHERED_COPPER_CHAIN, Material.WAXED_OXIDIZED_COPPER_CHAIN
			)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.COPPER_BARS, 2), trappedNewbieKey("copper_ingot_to_copper_bars"))
			.withGroup(formatStonecutterGroup(Material.COPPER_BARS))
			.addIngredients(Material.COPPER_INGOT)
			.register();
		new BlastingCraft(ItemStack.of(Material.COPPER_NUGGET, 4), 50, trappedNewbieKey("copper_bars_to_copper_nuggets"))
			.withExp(1)
			.addIngredients('C',
				Material.COPPER_BARS, Material.EXPOSED_COPPER_BARS, Material.WEATHERED_COPPER_BARS, Material.OXIDIZED_COPPER_BARS,
				Material.WAXED_COPPER_BARS, Material.WAXED_EXPOSED_COPPER_BARS, Material.WAXED_WEATHERED_COPPER_BARS, Material.WAXED_OXIDIZED_COPPER_BARS
			)
			.register();

		Material[] packedMudVariations = new Material[] {
			Material.PACKED_MUD,
			Material.MUD_BRICKS, Material.MUD_BRICK_STAIRS, Material.MUD_BRICK_SLAB, Material.MUD_BRICK_WALL
		};
		addStonecutterRecipesWithRemoval(packedMudVariations);
		new StonecuttingCraft(ItemStack.of(Material.MUD), trappedNewbieKey("packed_mud_to_mud"))
			.withGroup(formatStonecutterGroup(Material.MUD))
			.addIngredients('M', packedMudVariations)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.MUD), trappedNewbieKey("muddy_mungrove_roots_to_mud"))
			.withGroup(formatStonecutterGroup(Material.MUD))
			.addIngredients(Material.MUDDY_MANGROVE_ROOTS)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.MANGROVE_ROOTS), trappedNewbieKey("muddy_mungrove_roots_to_mangrove_roots"))
			.withGroup(formatStonecutterGroup(Material.MANGROVE_ROOTS))
			.addIngredients(Material.MUDDY_MANGROVE_ROOTS)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.GLOWSTONE_DUST, 4), trappedNewbieKey("glowstone_to_glowstone_dust"))
			.withGroup(formatStonecutterGroup(Material.GLOWSTONE_DUST))
			.addIngredients(Material.GLOWSTONE)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.HONEYCOMB, 4), trappedNewbieKey("honeycomb_block_to_honeycomb"))
			.withGroup(formatStonecutterGroup(Material.HONEYCOMB))
			.addIngredients(Material.HONEYCOMB_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.AMETHYST_SHARD, 4), trappedNewbieKey("amethyst_block_to_amethyst_shard"))
			.withGroup(formatStonecutterGroup(Material.AMETHYST_SHARD))
			.addIngredients(Material.AMETHYST_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.CLAY_BALL, 4), trappedNewbieKey("clay_to_clay_ball"))
			.withGroup(formatStonecutterGroup(Material.CLAY_BALL))
			.addIngredients(Material.CLAY)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.POINTED_DRIPSTONE, 4), trappedNewbieKey("dripstone_to_pointed_dripstone"))
			.withGroup(formatStonecutterGroup(Material.POINTED_DRIPSTONE))
			.addIngredients(Material.DRIPSTONE_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.PRISMARINE_CRYSTALS, 5), trappedNewbieKey("sea_lantern_to_prismarine_crystals"))
			.withGroup(formatStonecutterGroup(Material.PRISMARINE_CRYSTALS))
			.addIngredients(Material.SEA_LANTERN)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.WHEAT, 9), trappedNewbieKey("hay_block_to_wheat"))
			.withGroup(formatStonecutterGroup(Material.WHEAT))
			.addIngredients(Material.HAY_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.DRIED_KELP, 9), trappedNewbieKey("dried_kelp_block_to_dried_kelp"))
			.withGroup(formatStonecutterGroup(Material.DRIED_KELP))
			.addIngredients(Material.DRIED_KELP_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.BAMBOO, 9), trappedNewbieKey("bamboo_block_to_bamboo"))
			.withGroup(formatStonecutterGroup(Material.BAMBOO))
			.addIngredients(Material.BAMBOO_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.COAL, 9), trappedNewbieKey("coal_block_to_coal"))
			.withGroup(formatStonecutterGroup(Material.COAL))
			.addIngredients(Material.COAL_BLOCK)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.CHARCOAL, 9), trappedNewbieKey("charcoal_block_to_coal"))
			.withGroup(formatStonecutterGroup(Material.CHARCOAL))
			.addIngredients(DelightfulFarmingItems.CHARCOAL_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.DIAMOND, 9), trappedNewbieKey("diamond_block_to_diamond"))
			.withGroup(formatStonecutterGroup(Material.DIAMOND))
			.addIngredients(Material.DIAMOND_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.EMERALD, 9), trappedNewbieKey("emeral_block_to_emerald"))
			.withGroup(formatStonecutterGroup(Material.EMERALD))
			.addIngredients(Material.EMERALD_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.IRON_INGOT, 9), trappedNewbieKey("iron_block_to_iron_ingot"))
			.withGroup(formatStonecutterGroup(Material.IRON_INGOT))
			.addIngredients(Material.IRON_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.IRON_NUGGET, 9), trappedNewbieKey("iron_ingot_to_iron_nugget"))
			.withGroup(formatStonecutterGroup(Material.IRON_NUGGET))
			.addIngredients(Material.IRON_INGOT)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.RAW_IRON, 9), trappedNewbieKey("raw_iron_block_to_raw_iron"))
			.withGroup(formatStonecutterGroup(Material.IRON_INGOT))
			.addIngredients(Material.RAW_IRON_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.GOLD_INGOT, 9), trappedNewbieKey("gold_block_to_gold_ingot"))
			.withGroup(formatStonecutterGroup(Material.GOLD_INGOT))
			.addIngredients(Material.GOLD_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.GOLD_NUGGET, 9), trappedNewbieKey("gold_ingot_to_gold_nugget"))
			.withGroup(formatStonecutterGroup(Material.GOLD_NUGGET))
			.addIngredients(Material.GOLD_INGOT)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.RAW_GOLD, 9), trappedNewbieKey("raw_gold_block_to_raw_gold"))
			.withGroup(formatStonecutterGroup(Material.RAW_GOLD))
			.addIngredients(Material.RAW_GOLD_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.COPPER_INGOT, 9), trappedNewbieKey("copper_block_to_copper_ingot"))
			.withGroup(formatStonecutterGroup(Material.COPPER_INGOT))
			.addIngredients(Material.COPPER_BLOCK)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.COPPER_INGOT, 9), trappedNewbieKey("waxed_copper_block_to_copper_ingot"))
			.withGroup(formatStonecutterGroup(Material.COPPER_INGOT))
			.addIngredients(Material.WAXED_COPPER_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.COPPER_NUGGET, 9), trappedNewbieKey("copper_ingot_to_copper_nugget"))
			.withGroup(formatStonecutterGroup(Material.COPPER_NUGGET))
			.addIngredients(Material.COPPER_INGOT)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.RAW_COPPER, 9), trappedNewbieKey("raw_copper_block_to_raw_copper"))
			.withGroup(formatStonecutterGroup(Material.RAW_COPPER))
			.addIngredients(Material.RAW_COPPER_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.LAPIS_LAZULI, 9), trappedNewbieKey("lapis_block_to_lapis_lazuli"))
			.withGroup(formatStonecutterGroup(Material.LAPIS_LAZULI))
			.addIngredients(Material.LAPIS_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.REDSTONE, 9), trappedNewbieKey("redstone_block_to_redstone"))
			.withGroup(formatStonecutterGroup(Material.REDSTONE))
			.addIngredients(Material.REDSTONE_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.NETHERITE_INGOT, 9), trappedNewbieKey("netherite_block_to_netherite_ingot"))
			.withGroup(formatStonecutterGroup(Material.NETHERITE_INGOT))
			.addIngredients(Material.NETHERITE_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.RESIN_CLUMP, 9), trappedNewbieKey("resin_block_to_resin_clump"))
			.withGroup(formatStonecutterGroup(Material.RESIN_CLUMP))
			.addIngredients(Material.RESIN_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.BONE_MEAL, 9), trappedNewbieKey("bone_block_to_bone_meal"))
			.withGroup(formatStonecutterGroup(Material.BONE_MEAL))
			.addIngredients(Material.BONE_BLOCK)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.BONE_MEAL, 3), trappedNewbieKey("bone_to_bone_meal"))
			.withGroup(formatStonecutterGroup(Material.BONE_MEAL))
			.addIngredients(Material.BONE)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.PUMPKIN_SEEDS, 4), trappedNewbieKey("pumpkin_to_pumpkin_seeds"))
			.withGroup(formatStonecutterGroup(Material.PUMPKIN_SEEDS))
			.addIngredients(Material.PUMPKIN)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.MELON_SEEDS), trappedNewbieKey("melon_slice_to_melon_seeds"))
			.withGroup(formatStonecutterGroup(Material.MELON_SEEDS))
			.addIngredients(Material.MELON_SLICE)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.MELON_SLICE, 9), trappedNewbieKey("melon_to_melon_slice"))
			.withGroup(formatStonecutterGroup(Material.MELON_SLICE))
			.addIngredients(Material.MELON)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.SWEET_BERRIES, 9), trappedNewbieKey("sweet_berry_basket_to_sweet_berries"))
			.withGroup(formatStonecutterGroup(Material.SWEET_BERRIES))
			.addIngredients(DelightfulFarmingItems.SWEET_BERRY_BASKET)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.GLOW_BERRIES, 9), trappedNewbieKey("glow_berry_basket_to_glow_berries"))
			.withGroup(formatStonecutterGroup(Material.GLOW_BERRIES))
			.addIngredients(DelightfulFarmingItems.GLOW_BERRY_BASKET)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.SLIME_BALL, 9), trappedNewbieKey("slime_block_to_slime_ball"))
			.withGroup(formatStonecutterGroup(Material.SLIME_BALL))
			.addIngredients(Material.SLIME_BLOCK)
			.register();

		new ShapelessCraft(ItemStack.of(Material.NETHER_WART, 9), trappedNewbieKey("nether_wart_block_to_nether_wart"))
			.addIngredients(Material.NETHER_WART_BLOCK)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.NETHER_WART, 9), trappedNewbieKey("nether_wart_block_to_nether_wart"))
			.withGroup(formatStonecutterGroup(Material.NETHER_WART))
			.addIngredients(Material.NETHER_WART_BLOCK)
			.register();

		new StonecuttingCraft(ItemStack.of(Material.ICE, 9), trappedNewbieKey("packed_ice_to_ice"))
			.withGroup(formatStonecutterGroup(Material.ICE))
			.addIngredients(Material.PACKED_ICE)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.PACKED_ICE, 9), trappedNewbieKey("blue_ice_to_packed_ice"))
			.withGroup(formatStonecutterGroup(Material.PACKED_ICE))
			.addIngredients(Material.BLUE_ICE)
			.register();

		Material[] sandstoneVariations = new Material[] {
			Material.SANDSTONE, Material.SANDSTONE_STAIRS, Material.SANDSTONE_SLAB, Material.SANDSTONE_WALL,
			Material.SMOOTH_SANDSTONE, Material.SMOOTH_SANDSTONE_STAIRS, Material.SMOOTH_SANDSTONE_SLAB,
			Material.CUT_SANDSTONE, Material.CUT_SANDSTONE_SLAB, Material.CHISELED_SANDSTONE
		};
		addStonecutterRecipesWithRemoval(sandstoneVariations);
		new StonecuttingCraft(ItemStack.of(Material.SAND, 4), trappedNewbieKey("sandstone_to_sand"))
			.withGroup(formatStonecutterGroup(Material.SAND))
			.addIngredients('S', sandstoneVariations)
			.register();
		Material[] redSandstoneVariations = new Material[] {
			Material.RED_SANDSTONE, Material.RED_SANDSTONE_STAIRS, Material.RED_SANDSTONE_SLAB, Material.RED_SANDSTONE_WALL,
			Material.SMOOTH_RED_SANDSTONE, Material.SMOOTH_RED_SANDSTONE_STAIRS, Material.SMOOTH_RED_SANDSTONE_SLAB,
			Material.CUT_RED_SANDSTONE, Material.CUT_RED_SANDSTONE_SLAB, Material.CHISELED_RED_SANDSTONE
		};
		addStonecutterRecipesWithRemoval(redSandstoneVariations);
		new StonecuttingCraft(ItemStack.of(Material.RED_SAND, 4), trappedNewbieKey("red_sandstone_to_red_sand"))
			.withGroup(formatStonecutterGroup(Material.RED_SAND))
			.addIngredients('S', redSandstoneVariations)
			.register();

		Material[] bricksVariations = new Material[] {
			Material.BRICKS, Material.BRICK_STAIRS, Material.BRICK_SLAB, Material.BRICK_WALL
		};
		addStonecutterRecipesWithRemoval(bricksVariations);
		new StonecuttingCraft(ItemStack.of(Material.BRICK, 4), trappedNewbieKey("bricks_to_brick"))
			.withGroup(formatStonecutterGroup(Material.BRICK))
			.addIngredients('B', bricksVariations)
			.register();

		Material[] prismarineVariations = new Material[] {
			Material.PRISMARINE, Material.PRISMARINE_STAIRS, Material.PRISMARINE_SLAB, Material.PRISMARINE_WALL,
			Material.PRISMARINE_BRICKS, Material.PRISMARINE_BRICK_STAIRS, Material.PRISMARINE_BRICK_SLAB
		};
		Material[] darkPrismarineVariations = new Material[] {
			Material.DARK_PRISMARINE, Material.DARK_PRISMARINE_STAIRS, Material.DARK_PRISMARINE_SLAB
		};
		addStonecutterRecipesWithRemoval(prismarineVariations);
		addStonecutterRecipesWithRemoval(darkPrismarineVariations);
		new StonecuttingCraft(ItemStack.of(Material.PRISMARINE_SHARD, 9), trappedNewbieKey("prismarine_to_prismarine_shards"))
			.withGroup(formatStonecutterGroup(Material.PRISMARINE_SHARD))
			.addIngredients('P', MiscUtil.combineArrays(prismarineVariations, darkPrismarineVariations))
			.register();

		Material[] quartzs = new Material[] {
			Material.QUARTZ_BLOCK, Material.QUARTZ_STAIRS, Material.QUARTZ_SLAB,
			Material.SMOOTH_QUARTZ, Material.SMOOTH_QUARTZ_STAIRS, Material.SMOOTH_QUARTZ_SLAB,
			Material.QUARTZ_BRICKS, Material.QUARTZ_PILLAR,
			Material.CHISELED_QUARTZ_BLOCK
		};
		addStonecutterRecipesWithRemoval(quartzs);
		new StonecuttingCraft(ItemStack.of(Material.QUARTZ, 4), trappedNewbieKey("quartz_block_to_quartz"))
			.withGroup(formatStonecutterGroup(Material.QUARTZ))
			.addIngredients('Q', quartzs)
			.register();

		Material[] oakWoods = new Material[] {
			Material.OAK_PLANKS, Material.OAK_STAIRS, Material.OAK_SLAB,
			Material.OAK_DOOR, Material.OAK_TRAPDOOR,
			Material.OAK_SHELF, Material.OAK_FENCE, Material.OAK_FENCE_GATE,
			Material.OAK_SIGN, Material.OAK_HANGING_SIGN,
			Material.OAK_PRESSURE_PLATE, Material.OAK_BUTTON,
			Material.PETRIFIED_OAK_SLAB
		};
		Material[] spruceWoods = new Material[] {
			Material.SPRUCE_PLANKS, Material.SPRUCE_STAIRS, Material.SPRUCE_SLAB,
			Material.SPRUCE_DOOR, Material.SPRUCE_TRAPDOOR,
			Material.SPRUCE_SHELF, Material.SPRUCE_FENCE, Material.SPRUCE_FENCE_GATE,
			Material.SPRUCE_SIGN, Material.SPRUCE_HANGING_SIGN,
			Material.SPRUCE_PRESSURE_PLATE, Material.SPRUCE_BUTTON
		};
		Material[] birchWoods = new Material[] {
			Material.BIRCH_PLANKS, Material.BIRCH_STAIRS, Material.BIRCH_SLAB,
			Material.BIRCH_DOOR, Material.BIRCH_TRAPDOOR,
			Material.BIRCH_SHELF, Material.BIRCH_FENCE, Material.BIRCH_FENCE_GATE,
			Material.BIRCH_SIGN, Material.BIRCH_HANGING_SIGN,
			Material.BIRCH_PRESSURE_PLATE, Material.BIRCH_BUTTON
		};
		Material[] jungleWoods = new Material[] {
			Material.JUNGLE_PLANKS, Material.JUNGLE_STAIRS, Material.JUNGLE_SLAB,
			Material.JUNGLE_DOOR, Material.JUNGLE_TRAPDOOR,
			Material.JUNGLE_SHELF, Material.JUNGLE_FENCE, Material.JUNGLE_FENCE_GATE,
			Material.JUNGLE_SIGN, Material.JUNGLE_HANGING_SIGN,
			Material.JUNGLE_PRESSURE_PLATE, Material.JUNGLE_BUTTON
		};
		Material[] acaciaWoods = new Material[] {
			Material.ACACIA_PLANKS, Material.ACACIA_STAIRS, Material.ACACIA_SLAB,
			Material.ACACIA_DOOR, Material.ACACIA_TRAPDOOR,
			Material.ACACIA_SHELF, Material.ACACIA_FENCE, Material.ACACIA_FENCE_GATE,
			Material.ACACIA_SIGN, Material.ACACIA_HANGING_SIGN,
			Material.ACACIA_PRESSURE_PLATE, Material.ACACIA_BUTTON
		};
		Material[] darkOakWoods = new Material[] {
			Material.DARK_OAK_PLANKS, Material.DARK_OAK_STAIRS, Material.DARK_OAK_SLAB,
			Material.DARK_OAK_DOOR, Material.DARK_OAK_TRAPDOOR,
			Material.DARK_OAK_SHELF, Material.DARK_OAK_FENCE, Material.DARK_OAK_FENCE_GATE,
			Material.DARK_OAK_SIGN, Material.DARK_OAK_HANGING_SIGN,
			Material.DARK_OAK_PRESSURE_PLATE, Material.DARK_OAK_BUTTON
		};
		Material[] mangroveWoods = new Material[] {
			Material.MANGROVE_PLANKS, Material.MANGROVE_STAIRS, Material.MANGROVE_SLAB,
			Material.MANGROVE_DOOR, Material.MANGROVE_TRAPDOOR,
			Material.MANGROVE_SHELF, Material.MANGROVE_FENCE, Material.MANGROVE_FENCE_GATE,
			Material.MANGROVE_SIGN, Material.MANGROVE_HANGING_SIGN,
			Material.MANGROVE_PRESSURE_PLATE, Material.MANGROVE_BUTTON
		};
		Material[] cherryWoods = new Material[] {
			Material.CHERRY_PLANKS, Material.CHERRY_STAIRS, Material.CHERRY_SLAB,
			Material.CHERRY_DOOR, Material.CHERRY_TRAPDOOR,
			Material.CHERRY_SHELF, Material.CHERRY_FENCE, Material.CHERRY_FENCE_GATE,
			Material.CHERRY_SIGN, Material.CHERRY_HANGING_SIGN,
			Material.CHERRY_PRESSURE_PLATE, Material.CHERRY_BUTTON
		};
		Material[] paleOakWoods = new Material[] {
			Material.PALE_OAK_PLANKS, Material.PALE_OAK_STAIRS, Material.PALE_OAK_SLAB,
			Material.PALE_OAK_DOOR, Material.PALE_OAK_TRAPDOOR,
			Material.PALE_OAK_SHELF, Material.PALE_OAK_FENCE, Material.PALE_OAK_FENCE_GATE,
			Material.PALE_OAK_SIGN, Material.PALE_OAK_HANGING_SIGN,
			Material.PALE_OAK_PRESSURE_PLATE, Material.PALE_OAK_BUTTON
		};
		Material[] crimsonWoods = new Material[] {
			Material.CRIMSON_PLANKS, Material.CRIMSON_STAIRS, Material.CRIMSON_SLAB,
			Material.CRIMSON_DOOR, Material.CRIMSON_TRAPDOOR,
			Material.CRIMSON_SHELF, Material.CRIMSON_FENCE, Material.CRIMSON_FENCE_GATE,
			Material.CRIMSON_SIGN, Material.CRIMSON_HANGING_SIGN,
			Material.CRIMSON_PRESSURE_PLATE, Material.CRIMSON_BUTTON
		};
		Material[] warpedWoods = new Material[] {
			Material.WARPED_PLANKS, Material.WARPED_STAIRS, Material.WARPED_SLAB,
			Material.WARPED_DOOR, Material.WARPED_TRAPDOOR,
			Material.WARPED_SHELF, Material.WARPED_FENCE, Material.WARPED_FENCE_GATE,
			Material.WARPED_SIGN, Material.WARPED_HANGING_SIGN,
			Material.WARPED_PRESSURE_PLATE, Material.WARPED_BUTTON
		};
		Material[] bambooWoods = new Material[] {
			Material.BAMBOO_PLANKS, Material.BAMBOO_STAIRS, Material.BAMBOO_SLAB,
			Material.BAMBOO_DOOR, Material.BAMBOO_TRAPDOOR,
			Material.BAMBOO_SHELF, Material.BAMBOO_FENCE, Material.BAMBOO_FENCE_GATE,
			Material.BAMBOO_SIGN, Material.BAMBOO_HANGING_SIGN,
			Material.BAMBOO_PRESSURE_PLATE, Material.BAMBOO_BUTTON,
			Material.BAMBOO_MOSAIC, Material.BAMBOO_MOSAIC_STAIRS, Material.BAMBOO_MOSAIC_SLAB
		};
		addWoodStonecutterRecipesWithRemoval(oakWoods, TrappedNewbieItems.OAK_STICK, BucketModifier.BucketType.OAK);
		addWoodStonecutterRecipesWithRemoval(spruceWoods, TrappedNewbieItems.SPRUCE_STICK, BucketModifier.BucketType.SPRUCE);
		addWoodStonecutterRecipesWithRemoval(birchWoods, TrappedNewbieItems.BIRCH_STICK, BucketModifier.BucketType.BIRCH);
		addWoodStonecutterRecipesWithRemoval(jungleWoods, TrappedNewbieItems.JUNGLE_STICK, BucketModifier.BucketType.JUNGLE);
		addWoodStonecutterRecipesWithRemoval(acaciaWoods, TrappedNewbieItems.ACACIA_STICK, BucketModifier.BucketType.ACACIA);
		addWoodStonecutterRecipesWithRemoval(darkOakWoods, TrappedNewbieItems.DARK_OAK_STICK, BucketModifier.BucketType.DARK_OAK);
		addWoodStonecutterRecipesWithRemoval(mangroveWoods, TrappedNewbieItems.MANGROVE_STICK, BucketModifier.BucketType.MANGROVE);
		addWoodStonecutterRecipesWithRemoval(cherryWoods, TrappedNewbieItems.CHERRY_STICK, BucketModifier.BucketType.CHERRY);
		addWoodStonecutterRecipesWithRemoval(paleOakWoods, TrappedNewbieItems.PALE_OAK_STICK, BucketModifier.BucketType.PALE_OAK);
		addWoodStonecutterRecipesWithRemoval(crimsonWoods, TrappedNewbieItems.CRIMSON_STICK, BucketModifier.BucketType.CRIMSON);
		addWoodStonecutterRecipesWithRemoval(warpedWoods, TrappedNewbieItems.WARPED_STICK, BucketModifier.BucketType.WARPED);
		addWoodStonecutterRecipesWithRemoval(bambooWoods, TrappedNewbieItems.BAMBOOS_STICK, BucketModifier.BucketType.BAMBOO);

		addStonecutterRecipesWithRemoval(
			Material.STONE, Material.STONE_STAIRS, Material.STONE_SLAB,
			Material.STONE_BRICKS, Material.STONE_BRICK_STAIRS, Material.STONE_BRICK_SLAB, Material.STONE_BRICK_WALL,
			Material.CHISELED_STONE_BRICKS, Material.CRACKED_STONE_BRICKS,
			Material.COBBLESTONE, Material.COBBLESTONE_STAIRS, Material.COBBLESTONE_SLAB, Material.COBBLESTONE_WALL,
			Material.SMOOTH_STONE, Material.SMOOTH_STONE_SLAB,
			Material.STONE_PRESSURE_PLATE, Material.STONE_BUTTON
		);
		addStonecutterRecipesWithRemoval(
			Material.MOSSY_COBBLESTONE, Material.MOSSY_COBBLESTONE_STAIRS, Material.MOSSY_COBBLESTONE_SLAB, Material.MOSSY_COBBLESTONE_WALL,
			Material.MOSSY_STONE_BRICKS, Material.MOSSY_STONE_BRICK_STAIRS, Material.MOSSY_STONE_BRICK_SLAB, Material.MOSSY_STONE_BRICK_WALL
		);
		addStonecutterRecipesWithRemoval(
			Material.DIORITE, Material.DIORITE_STAIRS, Material.DIORITE_SLAB, Material.DIORITE_WALL,
			Material.POLISHED_DIORITE, Material.POLISHED_DIORITE_STAIRS, Material.POLISHED_DIORITE_SLAB
		);
		addStonecutterRecipesWithRemoval(
			Material.GRANITE, Material.GRANITE_STAIRS, Material.GRANITE_SLAB, Material.GRANITE_WALL,
			Material.POLISHED_GRANITE, Material.POLISHED_GRANITE_STAIRS, Material.POLISHED_GRANITE_SLAB
		);
		addStonecutterRecipesWithRemoval(
			Material.ANDESITE, Material.ANDESITE_STAIRS, Material.ANDESITE_SLAB, Material.ANDESITE_WALL,
			Material.POLISHED_ANDESITE, Material.POLISHED_ANDESITE_STAIRS, Material.POLISHED_ANDESITE_SLAB
		);
		addStonecutterRecipesWithRemoval(
			Material.TUFF, Material.TUFF_STAIRS, Material.TUFF_SLAB, Material.TUFF_WALL,
			Material.TUFF_BRICKS, Material.TUFF_BRICK_STAIRS, Material.TUFF_BRICK_SLAB, Material.TUFF_BRICK_WALL,
			Material.POLISHED_TUFF, Material.POLISHED_TUFF_STAIRS, Material.POLISHED_TUFF_SLAB, Material.POLISHED_TUFF_WALL,
			Material.CHISELED_TUFF, Material.CHISELED_TUFF_BRICKS
		);
		addStonecutterRecipesWithRemoval(
			Material.DEEPSLATE, Material.CHISELED_DEEPSLATE,
			Material.POLISHED_DEEPSLATE, Material.POLISHED_DEEPSLATE_STAIRS, Material.POLISHED_DEEPSLATE_SLAB, Material.POLISHED_DEEPSLATE_WALL,
			Material.COBBLED_DEEPSLATE, Material.COBBLED_DEEPSLATE_STAIRS, Material.COBBLED_DEEPSLATE_SLAB, Material.COBBLED_DEEPSLATE_WALL,
			Material.DEEPSLATE_TILES, Material.DEEPSLATE_TILE_STAIRS, Material.DEEPSLATE_TILE_SLAB, Material.DEEPSLATE_TILE_WALL,
			Material.DEEPSLATE_BRICKS, Material.DEEPSLATE_BRICK_STAIRS, Material.DEEPSLATE_BRICK_SLAB, Material.DEEPSLATE_BRICK_WALL,
			Material.CRACKED_DEEPSLATE_BRICKS, Material.CRACKED_DEEPSLATE_TILES
		);
		addStonecutterRecipesWithRemoval(
			Material.RESIN_BRICKS, Material.RESIN_BRICK_STAIRS, Material.RESIN_BRICK_SLAB, Material.RESIN_BRICK_WALL,
			Material.CHISELED_RESIN_BRICKS
		);
		addStonecutterRecipesWithRemoval(
			Material.NETHER_BRICKS, Material.NETHER_BRICK_STAIRS, Material.NETHER_BRICK_SLAB, Material.NETHER_BRICK_WALL,
			Material.NETHER_BRICK_FENCE, Material.CHISELED_NETHER_BRICKS, Material.CRACKED_NETHER_BRICKS
		);
		addStonecutterRecipesWithRemoval(
			Material.RED_NETHER_BRICKS, Material.RED_NETHER_BRICK_STAIRS, Material.RED_NETHER_BRICK_SLAB, Material.RED_NETHER_BRICK_WALL
		);
		addStonecutterRecipesWithRemoval(
			Material.BLACKSTONE, Material.BLACKSTONE_STAIRS, Material.BLACKSTONE_SLAB, Material.BLACKSTONE_WALL,
			Material.POLISHED_BLACKSTONE, Material.POLISHED_BLACKSTONE_STAIRS, Material.POLISHED_BLACKSTONE_SLAB, Material.POLISHED_BLACKSTONE_WALL,
			Material.POLISHED_BLACKSTONE_BRICKS, Material.POLISHED_BLACKSTONE_BRICK_STAIRS, Material.POLISHED_BLACKSTONE_BRICK_SLAB, Material.POLISHED_BLACKSTONE_BRICK_WALL,
			Material.CHISELED_POLISHED_BLACKSTONE, Material.CRACKED_POLISHED_BLACKSTONE_BRICKS,
			Material.POLISHED_BLACKSTONE_PRESSURE_PLATE, Material.POLISHED_BLACKSTONE_BUTTON
		);
		addStonecutterRecipesWithRemoval(
			Material.END_STONE,
			Material.END_STONE_BRICKS, Material.END_STONE_BRICK_STAIRS, Material.END_STONE_BRICK_SLAB, Material.END_STONE_BRICK_WALL
		);
		addStonecutterRecipesWithRemoval(
			Material.POPPED_CHORUS_FRUIT,
			Material.PURPUR_BLOCK, Material.PURPUR_STAIRS, Material.PURPUR_SLAB,
			Material.PURPUR_PILLAR
		);
		addStonecutterRecipesWithRemoval(
			Material.COPPER_BLOCK, Material.CHISELED_COPPER, Material.COPPER_GRATE,
			Material.CUT_COPPER, Material.CUT_COPPER_STAIRS, Material.CUT_COPPER_SLAB,
			Material.COPPER_DOOR, Material.COPPER_TRAPDOOR, Material.LIGHTNING_ROD
		);
		addStonecutterRecipesWithRemoval(
			Material.WAXED_COPPER_BLOCK, Material.WAXED_CHISELED_COPPER, Material.WAXED_COPPER_GRATE,
			Material.WAXED_CUT_COPPER, Material.WAXED_CUT_COPPER_STAIRS, Material.WAXED_CUT_COPPER_SLAB,
			Material.WAXED_COPPER_DOOR, Material.WAXED_COPPER_TRAPDOOR, Material.WAXED_LIGHTNING_ROD
		);
		addStonecutterRecipesWithRemoval(
			Material.EXPOSED_COPPER, Material.EXPOSED_CHISELED_COPPER, Material.EXPOSED_COPPER_GRATE,
			Material.EXPOSED_CUT_COPPER, Material.EXPOSED_CUT_COPPER_STAIRS, Material.EXPOSED_CUT_COPPER_SLAB,
			Material.EXPOSED_COPPER_DOOR, Material.EXPOSED_COPPER_TRAPDOOR, Material.EXPOSED_LIGHTNING_ROD
		);
		addStonecutterRecipesWithRemoval(
			Material.WAXED_EXPOSED_COPPER, Material.WAXED_EXPOSED_CHISELED_COPPER, Material.WAXED_EXPOSED_COPPER_GRATE,
			Material.WAXED_EXPOSED_CUT_COPPER, Material.WAXED_EXPOSED_CUT_COPPER_STAIRS, Material.WAXED_EXPOSED_CUT_COPPER_SLAB,
			Material.WAXED_EXPOSED_COPPER_DOOR, Material.WAXED_EXPOSED_COPPER_TRAPDOOR, Material.WAXED_EXPOSED_LIGHTNING_ROD
		);
		addStonecutterRecipesWithRemoval(
			Material.WEATHERED_COPPER, Material.WEATHERED_CHISELED_COPPER, Material.WEATHERED_COPPER_GRATE,
			Material.WEATHERED_CUT_COPPER, Material.WEATHERED_CUT_COPPER_STAIRS, Material.WEATHERED_CUT_COPPER_SLAB,
			Material.WEATHERED_COPPER_DOOR, Material.WEATHERED_COPPER_TRAPDOOR, Material.WEATHERED_LIGHTNING_ROD
		);
		addStonecutterRecipesWithRemoval(
			Material.WAXED_WEATHERED_COPPER, Material.WAXED_WEATHERED_CHISELED_COPPER, Material.WAXED_WEATHERED_COPPER_GRATE,
			Material.WAXED_WEATHERED_CUT_COPPER, Material.WAXED_WEATHERED_CUT_COPPER_STAIRS, Material.WAXED_WEATHERED_CUT_COPPER_SLAB,
			Material.WAXED_WEATHERED_COPPER_DOOR, Material.WAXED_WEATHERED_COPPER_TRAPDOOR, Material.WAXED_WEATHERED_LIGHTNING_ROD
		);
		addStonecutterRecipesWithRemoval(
			Material.OXIDIZED_COPPER, Material.OXIDIZED_CHISELED_COPPER, Material.OXIDIZED_COPPER_GRATE,
			Material.OXIDIZED_CUT_COPPER, Material.OXIDIZED_CUT_COPPER_STAIRS, Material.OXIDIZED_CUT_COPPER_SLAB,
			Material.OXIDIZED_COPPER_DOOR, Material.OXIDIZED_COPPER_TRAPDOOR, Material.OXIDIZED_LIGHTNING_ROD
		);
		addStonecutterRecipesWithRemoval(
			Material.WAXED_OXIDIZED_COPPER, Material.WAXED_OXIDIZED_CHISELED_COPPER, Material.WAXED_OXIDIZED_COPPER_GRATE,
			Material.WAXED_OXIDIZED_CUT_COPPER, Material.WAXED_OXIDIZED_CUT_COPPER_STAIRS, Material.WAXED_OXIDIZED_CUT_COPPER_SLAB,
			Material.WAXED_OXIDIZED_COPPER_DOOR, Material.WAXED_OXIDIZED_COPPER_TRAPDOOR, Material.WAXED_OXIDIZED_LIGHTNING_ROD
		);
		addStonecutterRecipesWithRemoval(
			Material.BASALT, Material.POLISHED_BASALT
		);

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
			Material base = type == TrappedNewbieItems.GLASS_SHARD ? Material.GLASS : Material.matchMaterial(type.key().value().replace("_glass_shard", "_stained_glass"));
			Material pane = type == TrappedNewbieItems.GLASS_SHARD ? Material.GLASS_PANE : Material.matchMaterial(type.key().value().replace("_glass_shard", "_stained_glass_pane"));
			if (base == null) return;
			if (pane == null) return;

			new ShapelessCraft(ItemStack.of(base), trappedNewbieKey(base.key().value() + "_from_shards"))
				.withGroup("glass_from_shards")
				.addIngredients(type, 4)
				.register();
			new ShapelessCraft(ItemStack.of(type, 4), trappedNewbieKey(base.key().value() + "_to_shards"))
				.withGroup("glass_to_shards")
				.addIngredients(base)
				.addIngredients(TrappedNewbieTags.HAMMERS.getValues())
				.register();

			new StonecuttingCraft(ItemStack.of(type, 4), trappedNewbieKey(base.key().value() + "_to_glass_shards"))
				.withGroup(formatStonecutterGroup(TrappedNewbieItems.GLASS_SHARD))
				.addIngredients(base)
				.register();

			new StonecuttingCraft(ItemStack.of(pane, 2), trappedNewbieKey(base.key().value() + "_to_" + pane.key().value()))
				.withGroup(formatStonecutterGroup(pane))
				.addIngredients(base)
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
		new ShapelessCraft(ItemStack.of(Material.PAPER, 3), trappedNewbieKey("paper"))
			.withGroup("paper")
			.addIngredients(Material.SUGAR_CANE, 3)
			.register();
		new ShapelessCraft(ItemStack.of(Material.BOWL, 3), trappedNewbieKey("bowl_from_planks")).withGroup("bowl_from_planks")
			.addIngredients('P', Tag.PLANKS.getValues())
			.addIngredients('A', Tag.ITEMS_AXES.getValues())
			.addIngredients('A', UtilizerTags.KNIFES.getValues())
			.register();

		addFuels();
		addBrews();
		removeRecipes();
		makeIngredientReplacements();
	}

	private static void addWoodStonecutterRecipesWithRemoval(Material[] woods, Material stick, BucketModifier.BucketType bucketType) {
		addStonecutterRecipesWithRemoval(woods);
		String prefix = bucketType.name().toLowerCase(Locale.US);
		new StonecuttingCraft(ItemStack.of(stick, 4), trappedNewbieKey(prefix + "_wood_to_" + stick.key().value()))
			.withGroup(formatStonecutterGroup(stick))
			.addIngredients('P', woods)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.LADDER, 2), trappedNewbieKey(prefix + "_wood_to_ladder"))
			.withGroup(formatStonecutterGroup(Material.LADDER))
			.addIngredients('P', woods)
			.register();
		new StonecuttingCraft(ItemStack.of(Material.BOWL, 3), trappedNewbieKey(prefix + "_wood_to_bowl"))
			.withGroup(formatStonecutterGroup(Material.BOWL))
			.addIngredients('P', woods)
			.register();
		new StonecuttingCraft(bucketType.save(ItemStack.of(Material.BUCKET)), bucketType.getKey())
			.withGroup("wooden_bucket")
			.addIngredients('P', woods)
			.register();
	}

	private static void addStonecutterRecipesWithRemoval(Material... materials) {
		for (Material material : materials) {
			List<Material> woods = new ArrayList<>(List.of(materials));
			woods.remove(material);
			new StonecuttingCraft(ItemStack.of(material), trappedNewbieKey(material.key().value()))
				.withGroup(formatStonecutterGroup(material))
				.addIngredients('P', woods)
				.register();
		}

		for (int i = 0; i < materials.length; i++) {
			Material material1 = materials[i];
			for (int j = i + 1; j < materials.length; j++) {
				Material material2 = materials[j];
				Bukkit.removeRecipe(NamespacedKey.minecraft(material1.key().value() + "_from_" + material2.key().value() + "_stonecutting"), false);
				Bukkit.removeRecipe(NamespacedKey.minecraft(material1.key().value() + "_from_" + material2.key().value().replace("bricks", "brick") + "_stonecutting"), false);
				Bukkit.removeRecipe(NamespacedKey.minecraft(material2.key().value() + "_from_" + material1.key().value() + "_stonecutting"), false);
				Bukkit.removeRecipe(NamespacedKey.minecraft(material2.key().value() + "_from_" + material1.key().value().replace("bricks", "brick") + "_stonecutting"), false);
			}

			Bukkit.removeRecipe(NamespacedKey.minecraft(material1.key().value()), false);
			Bukkit.removeRecipe(NamespacedKey.minecraft(material1.key().value() + "_from_stonecutting"), false);
		}
	}

	private static String formatStonecutterGroup(Material output) {
		return output.key().value() + "_from_stonecutting";
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
		recipe.withPreCheck(uniqueIngredientsCheck(2));
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
			"leather", "arrow", "spectral_arrow", "tipped_arrow",
			"paper", "bowl",
			"iron_bars", "iron_chain", "copper_bars", "copper_chain",
			"snow_block", "snow",
			"bookshelf",
			/// There aren't deleted automatically due to typos
			"chiseled_stone_bricks_stone_from_stonecutting", "stone_brick_walls_from_stone_stonecutting"
		}) {
			removeRecipe(recipe);
		}

		Tag.WOOL.getValues().forEach(r -> removeRecipe("dye_" + r.key().value()));
		Tag.WOOL_CARPETS.getValues().forEach(r -> removeRecipe("dye_" + r.key().value()));
		Tag.BEDS.getValues().forEach(r -> removeRecipe("dye_" + r.key().value()));
		Tag.TERRACOTTA.getValues().forEach(r -> {
			if (r != Material.TERRACOTTA) removeRecipe(r.key().value());
		});
		Tag.CANDLES.getValues().forEach(r -> {
			if (r != Material.CANDLE) removeRecipe(r.key().value());
		});
		Tag.SHULKER_BOXES.getValues().forEach(r -> {
			if (r != Material.SHULKER_BOX) removeRecipe(r.key().value());
		});
		MaterialTags.GLASS_PANES.getValues().forEach(r -> {
			if (r != Material.GLASS_PANE) removeRecipe(r.key().value() + "_from_glass_pane");
		});
		MaterialTags.STAINED_GLASS.getValues().forEach(r -> removeRecipe(r.key().value()));
		Tag.ITEMS_HARNESSES.getValues().forEach(r -> removeRecipe("dye_" + r.key().value()));
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
				RecipeChoice choice = entry.getValue();
				if (choice == null) continue;

				choice = updateChoice(replacements, choice, ((Keyed) recipe).getKey());
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
		switch (recipeChoice) {
			case RecipeChoice.ItemTypeChoice itemTypeChoice -> {
				for (TypedKey<ItemType> key : itemTypeChoice.itemTypes()) {
					Material material = Registry.ITEM.getOrThrow(key).asMaterial();
					if (material == null)
						throw new RuntimeException("Couldn't get material for " + key);
					IngredientReplacement replacements = map.get(material);
					if (replacements == null || replacements.exclusions().contains(recipeKey)) {
						items.add(ItemStack.of(material));
					} else {
						modified = true;
						predicate = replacements.ingredientCheck;
						items.addAll(replacements.ingredients());
					}
				}
			}
			case RecipeChoice.MaterialChoice materialChoice -> {
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
			}
			case RecipeChoice.ExactChoice exactChoice -> {
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
			default -> {}
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
