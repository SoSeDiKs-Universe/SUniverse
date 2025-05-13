package me.sosedik.trappednewbie.dataset;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.PotionContents;
import me.sosedik.miscme.dataset.MoreMobHeads;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.advancement.root.IRootAdvancement;
import me.sosedik.packetadvancements.api.tab.AdvancementManager;
import me.sosedik.packetadvancements.api.tab.AdvancementTab;
import me.sosedik.packetadvancements.imlp.display.FancyAdvancementDisplay;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData;
import me.sosedik.packetadvancements.util.storage.JsonStorage;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.impl.advancement.MereMortalAdvancement;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.utilizer.util.ItemUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.alwaysDone;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.requirements;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.simple;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.vanilla;
import static me.sosedik.packetadvancements.api.tab.AdvancementTab.buildTab;
import static me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement.buildBase;
import static me.sosedik.packetadvancements.imlp.advancement.fake.FakeAdvancement.buildFake;
import static me.sosedik.packetadvancements.imlp.advancement.linking.LinkingAdvancement.buildLinking;
import static me.sosedik.packetadvancements.imlp.advancement.multi.MultiParentAdvancement.buildMulti;
import static me.sosedik.packetadvancements.imlp.advancement.root.RootAdvancement.buildRoot;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.grandParentGranted;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.hidden;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.ifDone;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.ifVisible;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.parentGranted;

@NullMarked
public class TrappedNewbieAdvancements {

	private static final ItemStack WANDERING_TRADER_HEAD = ItemUtil.texturedHead(MoreMobHeads.WANDERING_TRADER);
	private static final ItemStack SHIFTING_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ4NmQwOTZiOGMwOGFkNWRhNmVhY2E5YTEwMTc1MWZiYzY4NzcwZDY3ZDUzMWU4Y2ZhNTYzYmE3M2Y5N2EzYyJ9fX0=");
	private static final ItemStack PIG_HOG_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNmZTNlYzU3YjYwMDkxMzg1MWVjMDMyODBjOTc5MTZmYWU5ZTA4NmFlYWFhNDcwMzM5NDE2ZmRmNGEyYTMwZiJ9fX0=");
	private static final ItemStack HAPPY_1000_DAYS_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA1YTZiZTIyZmJjNGRhZWU2YzAyZDJlYzUyNjI2NTE1Mjg3N2Y3ZDc1YWQzN2FjZjI5YTMzNWU5ZGFiODEzMCJ9fX0=");
	private static final ItemStack LADDER_ACE_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2MwOTQwNjRlMGNlOGI2ODAxOTlkMTA5MmQ0Mjg5NDUxMzM4N2NlYzA1MGY2NGQ3YTkyNzk3YmIyYTY0NGJjNyJ9fX0=");
	private static final ItemStack CHEST_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2UyZWI0NzUxZTNjNTBkNTBmZjE2MzUyNTc2NjYzZDhmZWRmZTNlMDRiMmYwYjhhMmFhODAzYjQxOTM2M2NhMSJ9fX0=");
	private static final ItemStack CHRISTMAS_CHEST_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZkZjQ3YzMwYjFlM2RiNTJlNDFmNWVlYjgwNmM2OWZlZjgwNTk1NTBlOGY1N2IwYTgzYjIyNjBhNjZkOTI3ZSJ9fX0=");
	private static final ItemStack MINECART_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2MzNTNmMjg5NmE0OTE1M2QyMzM4OGM3YjcxNzgxZDAyM2I5ZWJiNzU0NjdhZWY5Njg5MmYxYzIzZmQ0MiJ9fX0=");
	private static final ItemStack CRAFTING_TABLE_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWYzMGZmYmMwMTEwZWZhMzRlMDMwODYwZGExOGM4YTFkNmIyMjNkZTBmMDBkOWU0YzVkMGNmYTdlY2ZhZmE0OCJ9fX0=");
	private static final ItemStack TRADER_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmY3MWFmZDg3ZjdkMmVkZGQyNmY0MzUxYjkxNTYzNTY3NDM0MThjMzRkMTM0OGM5YjNjNWFmYjk5YzZiODMzOSJ9fX0=");
	private static final ItemStack SURVIVE_HEAD_1 = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ0NTJiZmNkYzNlYTE0ZjllNjEyYjFjOTZhYmVmOTdjMTBlOTZjNzExNmVhMmE0YjFhNWRmOGQ0YWExNzJmOSJ9fX0=");
	private static final ItemStack SURVIVE_HEAD_2 = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjYwZmE2MzIyNzRlODliMDY0YzMwOTBjMWFiMTEwMTQ3YzgzZWM3M2VjNGMzMWNhNzUwODE0NTdlYmI4YjI2OSJ9fX0=");
	private static final ItemStack SURVIVE_HEAD_3 = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDRhYjlkNmQ5ZjZkMjMxNGEyYmZmZjk4ZmRiODBmY2I0Y2UwYzhlZjc4ODEzMWE5YzZjMjJlY2M3MGU4M2Q3OSJ9fX0=");
	private static final TextColor PURPLE = Objects.requireNonNull(TextColor.fromHexString("#8b00e8"));

	public static final AdvancementManager MANAGER = new AdvancementManager(new JsonStorage(TrappedNewbie.instance()));

	public static final AdvancementTab REQUIEM_TAB = buildTab("requiem", MANAGER).inverseY().backgroundPathBlock(Material.SOUL_SAND).icon(Material.SKELETON_SKULL).build();
	public static final IRootAdvancement REQUIEM_ROOT = buildRoot(REQUIEM_TAB).display(display().icon(Material.SUNFLOWER)).requiredProgress(requirements("interact", "open")).visibilityRule(hidden()).build();
	public static final IAdvancement OPENING_HOLDER = buildBase(REQUIEM_ROOT, "holder").display(display().x(-1.25F).icon(WANDERING_TRADER_HEAD)).requiredProgress(alwaysDone()).build();
	public static final IAdvancement BRAVE_NEW_WORLD = buildBase(REQUIEM_ROOT, "brave_new_world").display(display().x(1F).icon(braveNewWorldItem())).requiredProgress(requirements("friendship", "fall")).build();
	public static final IAdvancement FIRST_POSSESSION = buildBase(BRAVE_NEW_WORLD, "first_possession").display(display().x(1.25F).icon(RequiemItems.HOST_REVOCATOR)).build();
	public static final IAdvancement GOOD_AS_NEW = buildBase(FIRST_POSSESSION, "good_as_new").display(display().x(1F).icon(ItemUtil.texturedHead(MoreMobHeads.ZOMBIE_VILLAGER_PLAINS_ARMORER))).build();
	public static final IAdvancement MERE_MORTAL = new MereMortalAdvancement(buildBase(GOOD_AS_NEW, "mere_mortal").display(display().x(1F).icon(Material.PLAYER_HEAD)).visibilityRule(hidden())); // ToDo
	public static final IAdvancement I_HATE_SAND = buildBase(FIRST_POSSESSION, "i_hate_sand").display(display().xy(1F, 1F).icon(Material.SAND).goalFrame()).build();
	public static final IAdvancement KUNG_FU_PANDA = buildBase(FIRST_POSSESSION, "kung_fu_panda").display(display().xy(1F, -1F).icon(Material.BAMBOO).goalFrame()).build();

	public static final AdvancementTab BASICS_TAB = buildTab("basics", MANAGER).inverseY().backgroundPathBlock(Material.GRAVEL).build();
	public static final IRootAdvancement BASICS_ROOT = buildRoot(BASICS_TAB).display(display().icon(Material.ROOTED_DIRT))
			.visibilityRule(ifDone(false, GOOD_AS_NEW))
			.requiredProgress(alwaysDone())
			.build();
	public static final IAdvancement GET_A_FIBER = buildBase(BASICS_ROOT, "get_a_fiber").display(display().x(1.5F).icon(TrappedNewbieItems.FIBER))
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FIBER))))
			.build();
	public static final IAdvancement MAKE_A_TWINE = buildBase(GET_A_FIBER, "make_a_twine").display(display().x(1F).icon(TrappedNewbieItems.TWINE))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.TWINE))))
			.build();
	public static final IAdvancement FIND_GRAVEL = buildBase(BASICS_ROOT, "find_gravel").display(display().xy(1F, -1F).icon(Material.GRAVEL))
			.visibilityRule(ifDone(MAKE_A_TWINE))
			.build();
	public static final IAdvancement GET_A_FLINT = buildBase(FIND_GRAVEL, "get_a_flint").display(display().x(1F).icon(Material.FLINT))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(Material.FLINT))))
			.build();
	public static final IAdvancement GET_A_FLAKED_FLINT = buildBase(GET_A_FLINT, "get_a_flaked_flint").display(display().x(1F).icon(TrappedNewbieItems.FLAKED_FLINT))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLAKED_FLINT))))
			.build();
	public static final IAdvancement MAKE_FLINT_SHEARS = buildMulti(GET_A_FLAKED_FLINT, "make_flint_shears", MAKE_A_TWINE).linkingToAll(false).display(display().xy(1F, 0.5F).goalFrame().icon(TrappedNewbieItems.FLINT_SHEARS))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_SHEARS))))
			.build();
	public static final IAdvancement GET_A_BRANCH = buildBase(BASICS_ROOT, "get_a_branch").display(display().xy(2F, 1F).icon(TrappedNewbieItems.OAK_BRANCH))
			.visibilityRule(ifDone(GET_A_FLAKED_FLINT))
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.BRANCHES))))
			.build();
	public static final IAdvancement MAKE_ROUGH_STICKS = buildBase(MAKE_FLINT_SHEARS, "make_rough_sticks").display(display().xy(1F, 0.75F).icon(TrappedNewbieItems.ROUGH_STICK))
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.ROUGH_STICK))))
			.build();
	public static final IAdvancement MAKE_A_FLINT_AXE = buildBase(MAKE_ROUGH_STICKS, "make_a_flint_axe").display(display().y(2F).goalFrame().icon(TrappedNewbieItems.FLINT_AXE))
			.visibilityRule(grandParentGranted())
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_AXE))))
			.build();
	public static final IAdvancement GET_A_LOG = buildBase(MAKE_A_FLINT_AXE, "get_a_log").display(display().x(-1F).icon(Material.OAK_LOG))
			.visibilityRule(grandParentGranted())
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(Tag.LOGS))))
			.build();
	public static final IAdvancement MAKE_A_CHOPPING_BLOCK = buildBase(GET_A_LOG, "make_a_chopping_block").display(display().x(-1F).icon(TrappedNewbieItems.OAK_CHOPPING_BLOCK))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.ITEM_CHOPPING_BLOCKS))))
			.build();
	public static final IAdvancement MAKE_PLANKS = buildBase(MAKE_A_CHOPPING_BLOCK, "make_planks").display(display().x(-1F).icon(Material.OAK_PLANKS))
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(Tag.PLANKS))))
			.build();
	public static final IAdvancement MAKE_STICKS = buildBase(MAKE_PLANKS, "make_sticks").display(display().x(-1.05F).icon(TrappedNewbieItems.OAK_STICK))
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.STICKS))))
			.build();

	public static final IAdvancement MAKE_A_FIRE = buildBase(MAKE_ROUGH_STICKS, "make_a_fire").display(display().xy(1.5F, -0.9F).goalFrame().icon(TrappedNewbieItems.INVENTORY_FIRE)).visibilityRule(grandParentGranted()).build();
	public static final IAdvancement MAKE_A_FIRE_FILLER = buildFake(MAKE_A_FIRE).display(display().x(0.5F).isHidden(true)).visibilityRule(parentGranted()).build();

	private static final IAdvancement MAKE_FLINT_SHEARS_TO_GET_A_BRANCH_MIMIC = buildLinking(MAKE_FLINT_SHEARS, GET_A_BRANCH).build();
	private static final IAdvancement GET_A_FLAKED_FLINT_TO_MAKE_A_TWINE_MIMIC = buildLinking(GET_A_FLAKED_FLINT, MAKE_A_TWINE).visibilityRule(ifVisible(false, MAKE_FLINT_SHEARS)).build();

	public static final AdvancementTab STATISTICS_TAB = buildTab("statistics", MANAGER).inverseY().backgroundPathTexture("block/loom_side").build();
	public static final IRootAdvancement STATISTICS_ROOT = buildRoot(STATISTICS_TAB).display(display().icon(Material.WRITABLE_BOOK)).visibilityRule(ifDone(false, GOOD_AS_NEW)).requiredProgress(alwaysDone()).build();
	public static final IAdvancement STATISTICS_RIGHT_LINKER = buildFake(STATISTICS_ROOT).display(display().x(0.75F).isHidden(true)).build();
	public static final IAdvancement STATISTICS_UP_LINKER = buildFake(STATISTICS_ROOT).display(display().x(-0.5F).isHidden(true)).build();
	// Up
	public static final IAdvancement PLAY_1D = buildBase(STATISTICS_UP_LINKER, "play_1d").display(display().xy(0.5F, 1.25F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SUNFLOWER)).build();
	public static final IAdvancement PLAY_100D = buildBase(STATISTICS_UP_LINKER, "play_100d").display(display().xy(0.5F, 2.25F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.CLOCK)).build();
	public static final IAdvancement PLAY_365D = buildBase(STATISTICS_UP_LINKER, "play_365d").display(display().xy(0.5F, 3.25F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.FIREWORK_ROCKET)).build();
	public static final IAdvancement PLAY_1000D = buildBase(STATISTICS_UP_LINKER, "play_1000d").display(display().xy(0.5F, 4.25F).challengeFrame().fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).icon(HAPPY_1000_DAYS_HEAD)).build();
	// Right top
	public static final IAdvancement BELL_100 = buildBase(STATISTICS_RIGHT_LINKER, "bell_100").display(display().x(1).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.STICK)).build();
	public static final IAdvancement BELL_1K = buildBase(BELL_100, "bell_1k").display(display().x(1).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.MUSIC_DISC_13)).build();
	public static final IAdvancement BELL_10K = buildBase(BELL_1K, "bell_10k").display(display().x(1).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.BELL)).build();
	public static final IAdvancement BELL_100K = buildBase(BELL_10K, "bell_100k").display(display().x(1).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(Material.REDSTONE)).build();
	public static final IAdvancement BELL_1000K = buildBase(BELL_100K, "bell_1000k").display(display().x(1).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.SKELETON_SKULL)).build();
	public static final IAdvancement BELL_10000K = buildBase(BELL_1000K, "bell_10000k").display(display().x(1).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(ItemUtil.glint(Material.BELL))).build();
	public static final IAdvancement LEVEL_30 = buildBase(STATISTICS_RIGHT_LINKER, "level_30").display(display().xy(1F, 1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemUtil.glint(Material.EXPERIENCE_BOTTLE))).build();
	public static final IAdvancement LEVEL_100 = buildBase(LEVEL_30, "level_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.glint(Material.ENCHANTED_BOOK))).build();
	public static final IAdvancement LEVEL_300 = buildBase(LEVEL_100, "level_300").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.LAPIS_BLOCK))).build();
	public static final IAdvancement LEVEL_1000 = buildBase(LEVEL_300, "level_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.SCULK))).build();
	public static final IAdvancement LEVEL_2500 = buildBase(LEVEL_1000, "level_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().icon(ItemUtil.glint(Material.ENCHANTING_TABLE))).build();
	public static final IAdvancement LEVEL_5000 = buildBase(LEVEL_2500, "level_5000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(ItemUtil.glint(Material.DAMAGED_ANVIL))).build();
	public static final IAdvancement ENCHANT_10 = buildBase(STATISTICS_RIGHT_LINKER, "enchant_10").display(display().xy(1F, 2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemUtil.glint(Material.IRON_SWORD))).build();
	public static final IAdvancement ENCHANT_50 = buildBase(ENCHANT_10, "enchant_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.glint(Material.DIAMOND_AXE))).build();
	public static final IAdvancement ENCHANT_250 = buildBase(ENCHANT_50, "enchant_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.TRIDENT))).build();
	public static final IAdvancement ENCHANT_1000 = buildBase(ENCHANT_250, "enchant_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.NETHERITE_PICKAXE))).build();
	public static final IAdvancement ENCHANT_2500 = buildBase(ENCHANT_1000, "enchant_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().icon(ItemUtil.glint(Material.BOW))).build();
	public static final IAdvancement ENCHANT_5K = buildBase(ENCHANT_2500, "enchant_5k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(ItemUtil.glint(Material.BRUSH))).build();
	public static final IAdvancement ENCHANT_10K = buildBase(ENCHANT_5K, "enchant_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(ItemUtil.glint(Material.ENCHANTED_BOOK))).build();
	public static final IAdvancement WASH_10 = buildBase(STATISTICS_RIGHT_LINKER, "wash_10").display(display().xy(1F, 3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.WHITE_BANNER)).requiredProgress(simple(10, 1)).build();
	public static final IAdvancement WASH_50 = buildBase(WASH_10, "wash_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(new ItemStack(Material.LEATHER_BOOTS).withColor(NamedTextColor.LIGHT_PURPLE))).requiredProgress(simple(50, 1)).build();
	public static final IAdvancement WASH_250 = buildBase(WASH_50, "wash_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.CAULDRON)).requiredProgress(simple(250, 1)).build();
	public static final IAdvancement WASH_1K = buildBase(WASH_250, "wash_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().icon(ItemUtil.glint(Material.CAULDRON))).requiredProgress(simple(1000, 1)).build();
	public static final IAdvancement WASH_5K = buildBase(WASH_1K, "wash_5k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.SHULKER_BOX)).requiredProgress(simple(5000, 1)).build();
	public static final IAdvancement WASH_10K = buildBase(WASH_5K, "wash_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(Material.WATER_BUCKET)).requiredProgress(simple(10000, 1)).build();
	public static final IAdvancement FISH_250 = buildBase(STATISTICS_RIGHT_LINKER, "fish_250").display(display().xy(1F, 4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.COD)).build();
	public static final IAdvancement FISH_1K = buildBase(FISH_250, "fish_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.SALMON)).build();
	public static final IAdvancement FISH_2500 = buildBase(FISH_1K, "fish_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.TROPICAL_FISH)).build();
	public static final IAdvancement FISH_5K = buildBase(FISH_2500, "fish_5k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().icon(Material.PUFFERFISH)).build();
	public static final IAdvancement FISH_10K = buildBase(FISH_5K, "fish_10k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.NAUTILUS_SHELL)).build();
	public static final IAdvancement FISH_50K = buildBase(FISH_10K, "fish_50k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(ItemUtil.glint(Material.FISHING_ROD))).build();
	public static final IAdvancement EAT_200 = buildBase(STATISTICS_RIGHT_LINKER, "eat_200").display(display().xy(1F, 5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BREAD)).requiredProgress(simple(200, 1)).build();
	public static final IAdvancement EAT_1K = buildBase(EAT_200, "eat_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.COOKED_PORKCHOP)).requiredProgress(simple(1000, 1)).build();
	public static final IAdvancement EAT_2500 = buildBase(EAT_1K, "eat_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.COOKED_BEEF)).requiredProgress(simple(2500, 1)).build();
	public static final IAdvancement EAT_5K = buildBase(EAT_2500, "eat_5k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.GOLDEN_CARROT)).requiredProgress(simple(5000, 1)).build();
	public static final IAdvancement EAT_10K = buildBase(EAT_5K, "eat_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().icon(Material.GOLDEN_APPLE)).requiredProgress(simple(10000, 1)).build();
	public static final IAdvancement EAT_25K = buildBase(EAT_10K, "eat_25k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.COOKIE)).requiredProgress(simple(25000, 1)).build();
	public static final IAdvancement EAT_50K = buildBase(EAT_25K, "eat_50k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(Material.CAKE)).requiredProgress(simple(50000, 1)).build();
	public static final IAdvancement TOTEM_5 = buildBase(STATISTICS_RIGHT_LINKER, "totem_5").display(display().xy(1F, 6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.GOLD_NUGGET)).build();
	public static final IAdvancement TOTEM_10 = buildBase(TOTEM_5, "totem_10").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.TOTEM_OF_UNDYING)).build();
	public static final IAdvancement TOTEM_25 = buildBase(TOTEM_10, "totem_25").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.RAW_GOLD)).build();
	public static final IAdvancement TOTEM_50 = buildBase(TOTEM_25, "totem_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.PLAYER_HEAD)).build();
	public static final IAdvancement TOTEM_100 = buildBase(TOTEM_50, "totem_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.ZOMBIE_HEAD)).build();
	public static final IAdvancement TOTEM_250 = buildBase(TOTEM_100, "totem_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.SKELETON_SKULL)).build();
	public static final IAdvancement TOTEM_500 = buildBase(TOTEM_250, "totem_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.WITHER_SKELETON_SKULL)).build();
	public static final IAdvancement TOTEM_1000 = buildBase(TOTEM_500, "totem_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(Material.EVOKER_SPAWN_EGG)).build();
	public static final IAdvancement TOTEM_2500 = buildBase(TOTEM_1000, "totem_2500").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.END_CRYSTAL)).build();
	public static final IAdvancement TOTEM_5000 = buildBase(TOTEM_2500, "totem_5000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(ItemUtil.glint(Material.TOTEM_OF_UNDYING))).build();
	public static final IAdvancement RAID_5 = buildBase(STATISTICS_RIGHT_LINKER, "raid_5").display(display().xy(1F, 7F).fancyDescriptionParent(NamedTextColor.GREEN).challengeFrame().icon(Material.CROSSBOW)).build();
	public static final IAdvancement RAID_20 = buildBase(RAID_5, "raid_20").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.IRON_AXE)).build();
	public static final IAdvancement RAID_100 = buildBase(RAID_20, "raid_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.SADDLE))).build();
	public static final IAdvancement RAID_2000 = buildBase(RAID_100, "raid_2000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.TOTEM_OF_UNDYING))).build();
	// Right bottom
	public static final IAdvancement BREED_100 = buildBase(STATISTICS_RIGHT_LINKER, "breed_100").display(display().xy(1F, -1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PORKCHOP)).build();
	public static final IAdvancement BREED_500 = buildBase(BREED_100, "breed_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BEEF)).build();
	public static final IAdvancement BREED_2500 = buildBase(BREED_500, "breed_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.CHICKEN)).build();
	public static final IAdvancement BREED_5000 = buildBase(BREED_2500, "breed_5000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.MUTTON)).build();
	public static final IAdvancement BREED_10000 = buildBase(BREED_5000, "breed_10000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().icon(Material.RABBIT)).build();
	public static final IAdvancement BREED_15000 = buildBase(BREED_10000, "breed_15000").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.HONEY_BOTTLE)).build();
	public static final IAdvancement BREED_25000 = buildBase(BREED_15000, "breed_25000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(ItemUtil.glint(Material.EGG))).build();
	public static final IAdvancement KILL_250 = buildBase(STATISTICS_RIGHT_LINKER, "kill_250").display(display().xy(1F, -2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.STONE_SWORD)).build();
	public static final IAdvancement KILL_2500 = buildBase(KILL_250, "kill_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.IRON_SWORD )).build();
	public static final IAdvancement KILL_25K = buildBase(KILL_2500, "kill_25k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DIAMOND_SWORD)).build();
	public static final IAdvancement KILL_50K = buildBase(KILL_25K, "kill_50k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.GOLDEN_SWORD)).build();
	public static final IAdvancement KILL_100K = buildBase(KILL_50K, "kill_100k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().icon(Material.NETHERITE_SWORD)).build();
	public static final IAdvancement KILL_250K = buildBase(KILL_100K, "kill_250k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.WOODEN_SWORD)).build();
	public static final IAdvancement KILL_500K = buildBase(KILL_250K, "kill_500k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(ItemUtil.glint(Material.NETHERITE_SWORD))).build();
	public static final IAdvancement TRADE_100 = buildBase(STATISTICS_RIGHT_LINKER, "trade_100").display(display().xy(1F, -3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.EMERALD)).build();
	public static final IAdvancement TRADE_500 = buildBase(TRADE_100, "trade_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.EMERALD_BLOCK)).build();
	public static final IAdvancement TRADE_2500 = buildBase(TRADE_500, "trade_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.EGG)).build();
	public static final IAdvancement TRADE_10K = buildBase(TRADE_2500, "trade_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.PUMPKIN)).build();
	public static final IAdvancement TRADE_25K = buildBase(TRADE_10K, "trade_25k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().icon(Material.MELON)).build();
	public static final IAdvancement TRADE_50K = buildBase(TRADE_25K, "trade_50k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.GLASS)).build();
	public static final IAdvancement TRADE_250K = buildBase(TRADE_50K, "trade_250k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(TRADER_HEAD)).build();
	public static final IAdvancement LOOT_10 = buildBase(STATISTICS_RIGHT_LINKER, "loot_10").display(display().xy(1F, -4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CHEST)).requiredProgress(simple(10, 1)).build();
	public static final IAdvancement LOOT_100 = buildBase(LOOT_10, "loot_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.BARREL)).requiredProgress(simple(100, 1)).build();
	public static final IAdvancement LOOT_500 = buildBase(LOOT_100, "loot_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.ENDER_CHEST)).requiredProgress(simple(500, 1)).build();
	public static final IAdvancement LOOT_1000 = buildBase(LOOT_500, "loot_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).icon(Material.ENCHANTED_GOLDEN_APPLE)).requiredProgress(simple(1000, 1)).build();
	public static final IAdvancement LOOT_2500 = buildBase(LOOT_1000, "loot_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).icon(ItemUtil.glint(Material.BARREL))).requiredProgress(simple(2500, 1)).build();
	public static final IAdvancement LOOT_5000 = buildBase(LOOT_2500, "loot_5000").display(display().x(1F).fancyDescriptionParent(PURPLE).icon(ItemUtil.glint(Material.ENDER_CHEST))).requiredProgress(simple(5000, 1)).build();
	public static final IAdvancement LOOT_10000 = buildBase(LOOT_5000, "loot_10000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).icon(Material.FLINT_AND_STEEL)).requiredProgress(simple(10000, 1)).build();
	public static final IAdvancement OPEN_CHEST_100 = buildBase(STATISTICS_RIGHT_LINKER, "open_chest_100").display(display().xy(1F, -5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CHEST)).build();
	public static final IAdvancement OPEN_CHEST_1K = buildBase(OPEN_CHEST_100, "open_chest_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.CHEST_MINECART)).build();
	public static final IAdvancement OPEN_CHEST_10K = buildBase(OPEN_CHEST_1K, "open_chest_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(CHEST_HEAD)).build();
	public static final IAdvancement OPEN_CHEST_25K = buildBase(OPEN_CHEST_10K, "open_chest_25k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(CHRISTMAS_CHEST_HEAD)).build();
	public static final IAdvancement OPEN_SHULKER_100 = buildBase(OPEN_CHEST_25K, "open_shulker_100").display(display().x(1.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.WHITE_SHULKER_BOX)).build();
	public static final IAdvancement OPEN_SHULKER_1K = buildBase(OPEN_SHULKER_100, "open_shulker_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.LIGHT_GRAY_SHULKER_BOX)).build();
	public static final IAdvancement OPEN_SHULKER_10K = buildBase(OPEN_SHULKER_1K, "open_shulker_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.GRAY_SHULKER_BOX)).build();
	public static final IAdvancement OPEN_SHULKER_100K = buildBase(OPEN_SHULKER_10K, "open_shulker_100k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.BLACK_SHULKER_BOX)).build();
	public static final IAdvancement OPEN_CRAFTING_TABLE_15 = buildBase(STATISTICS_RIGHT_LINKER, "open_crafting_table_15").display(display().xy(1F, -6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_LOG)).build();
	public static final IAdvancement OPEN_CRAFTING_TABLE_100 = buildBase(OPEN_CRAFTING_TABLE_15, "open_crafting_table_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.OAK_PLANKS)).build();
	public static final IAdvancement OPEN_CRAFTING_TABLE_500 = buildBase(OPEN_CRAFTING_TABLE_100, "open_crafting_table_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.CRAFTING_TABLE))).build();
	public static final IAdvancement OPEN_CRAFTING_TABLE_2500 = buildBase(OPEN_CRAFTING_TABLE_500, "open_crafting_table_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(CRAFTING_TABLE_HEAD)).build();
	public static final IAdvancement BREAK_100_IRON = buildBase(STATISTICS_RIGHT_LINKER, "break_100_iron").display(display().xy(1F, -7F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.IRON_PICKAXE)).build();
	public static final IAdvancement BREAK_2500_DIAMOND = buildBase(BREAK_100_IRON, "break_2500_diamond").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.DIAMOND_PICKAXE)).build();
	public static final IAdvancement BREAK_10K_NETHERITE = buildBase(BREAK_2500_DIAMOND, "break_10k_netherite").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.NETHERITE_PICKAXE)).build();
	public static final IAdvancement BREAK_100K_NETHERITE = buildBase(BREAK_10K_NETHERITE, "break_100k_netherite").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.NETHERITE_PICKAXE))).build();
	public static final IAdvancement SURVIVE_1H = buildBase(STATISTICS_RIGHT_LINKER, "survive_1h").display(display().xy(1F, -8F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CLOCK)).build();
	public static final IAdvancement SURVIVE_10H = buildBase(SURVIVE_1H, "survive_10h").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(SURVIVE_HEAD_1)).build();
	public static final IAdvancement SURVIVE_50H = buildBase(SURVIVE_10H, "survive_50h").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(SURVIVE_HEAD_2)).build();
	public static final IAdvancement SURVIVE_200H = buildBase(SURVIVE_50H, "survive_200h").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(SURVIVE_HEAD_3)).build();
	public static final IAdvancement DEATHS_1 = buildBase(SURVIVE_200H, "deaths_1").display(display().x(1.5F).fancyDescriptionParent(NamedTextColor.DARK_RED).icon(RequiemItems.HEART)).build();
	public static final IAdvancement DEATHS_50 = buildBase(DEATHS_1, "deaths_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BONE)).build();
	public static final IAdvancement DEATHS_250 = buildBase(DEATHS_50, "deaths_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(RequiemItems.SPINE)).build();
	public static final IAdvancement DEATHS_1000 = buildBase(DEATHS_250, "deaths_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.SKELETON_SKULL)).build();
//	public static final IAdvancement HORSE_SPEED_1 = buildBase(STATISTICS_RIGHT_LINKER, "horse_speed_1").display(display().xy(1F, -9F).fancyDescriptionParent(NamedTextColor.GREEN).icon(new ItemStack(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16707753)))).build();
//	public static final IAdvancement HORSE_SPEED_2 = buildBase(HORSE_SPEED_1, "horse_speed_2").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(new ItemStack(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16495988)))).build();
//	public static final IAdvancement HORSE_SPEED_3 = buildBase(HORSE_SPEED_2, "horse_speed_3").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(new ItemStack(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16481652)))).build();
//	public static final IAdvancement HORSE_SPEED_4 = buildBase(HORSE_SPEED_3, "horse_speed_4").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(new ItemStack(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16352992)))).build();

	// Left top
	public static final IAdvancement WALK_10KM = buildBase(STATISTICS_ROOT, "walk_10km").display(display().x(-1.75F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.LEATHER_BOOTS)).build();
	public static final IAdvancement WALK_50KM = buildBase(WALK_10KM, "walk_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.CHAINMAIL_BOOTS)).build();
	public static final IAdvancement WALK_250KM = buildBase(WALK_50KM, "walk_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.IRON_BOOTS)).build();
	public static final IAdvancement WALK_1000KM = buildBase(WALK_250KM, "walk_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.GOLDEN_BOOTS)).build();
	public static final IAdvancement WALK_5000KM = buildBase(WALK_1000KM, "walk_5000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(Material.DIAMOND_BOOTS)).build();
	public static final IAdvancement WALK_10000KM = buildBase(WALK_5000KM, "walk_10000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(Material.NETHERITE_BOOTS)).build();
	public static final IAdvancement SPRINT_10KM = buildBase(WALK_10KM, "sprint_10km").display(display().y(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.RED_TERRACOTTA)).build();
	public static final IAdvancement SPRINT_MARATHON = buildBase(SPRINT_10KM, "sprint_marathon").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.GRASS_BLOCK)).build();
	public static final IAdvancement SPRINT_250KM = buildBase(SPRINT_MARATHON, "sprint_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.CHAINMAIL_LEGGINGS)).build();
	public static final IAdvancement SPRINT_1000KM = buildBase(SPRINT_250KM, "sprint_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.NETHERITE_LEGGINGS))).build();
	public static final IAdvancement SPRINT_2500KM = buildBase(SPRINT_1000KM, "sprint_2500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(potionItem(Material.SPLASH_POTION, PotionType.INVISIBILITY))).build();
	public static final IAdvancement SPRINT_5000KM = buildBase(SPRINT_2500KM, "sprint_5000km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.FEATHER)).build();
	public static final IAdvancement SPRINT_10000KM = buildBase(SPRINT_5000KM, "sprint_10000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(Material.BARRIER)).build();
	public static final IAdvancement JUMP_1K = buildBase(WALK_10KM, "jump_1k").display(display().y(2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_FENCE)).build();
	public static final IAdvancement JUMP_10K = buildBase(JUMP_1K, "jump_10k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.ACACIA_LEAVES)).build();
	public static final IAdvancement JUMP_50K = buildBase(JUMP_10K, "jump_50k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).goalFrame().icon(Material.CYAN_CARPET)).build();
	public static final IAdvancement JUMP_100K = buildBase(JUMP_50K, "jump_100k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.SLIME_BLOCK)).build();
	public static final IAdvancement JUMP_250K = buildBase(JUMP_100K, "jump_250k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(Material.RABBIT_FOOT)).build();
	public static final IAdvancement JUMP_500K = buildBase(JUMP_250K, "jump_500k").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(potionItem(Material.POTION, PotionType.LEAPING))).build();
	public static final IAdvancement JUMP_1000K = buildBase(JUMP_500K, "jump_1000k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(Material.PINK_BED)).build();
	public static final IAdvancement BOAT_1KM = buildBase(WALK_10KM, "boat_1km").display(display().y(3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_BOAT)).build();
	public static final IAdvancement BOAT_10KM = buildBase(BOAT_1KM, "boat_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.SPRUCE_BOAT)).build();
	public static final IAdvancement BOAT_25KM = buildBase(BOAT_10KM, "boat_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DARK_OAK_BOAT)).build();
	public static final IAdvancement BOAT_50KM = buildBase(BOAT_25KM, "boat_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.MANGROVE_BOAT)).build();
	public static final IAdvancement BOAT_100KM = buildBase(BOAT_50KM, "boat_100km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(Material.ACACIA_BOAT)).build();
	public static final IAdvancement BOAT_250KM = buildBase(BOAT_100KM, "boat_250km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.BIRCH_BOAT)).build();
	public static final IAdvancement BOAT_500KM = buildBase(BOAT_250KM, "boat_500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(ItemUtil.glint(Material.CHERRY_BOAT))).build();
	public static final IAdvancement HORSE_1K = buildBase(WALK_10KM, "horse_1k").display(display().y(4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.IRON_HORSE_ARMOR)).build();
	public static final IAdvancement HORSE_10K = buildBase(HORSE_1K, "horse_10k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.GOLDEN_HORSE_ARMOR)).build();
	public static final IAdvancement HORSE_25K = buildBase(HORSE_10K, "horse_25k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DIAMOND_HORSE_ARMOR)).build();
	public static final IAdvancement HORSE_50K = buildBase(HORSE_25K, "horse_50k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.BONE)).build();
	public static final IAdvancement HORSE_100K = buildBase(HORSE_50K, "horse_100k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(ItemUtil.texturedHead(MoreMobHeads.SKELETON_HORSE))).build();
	public static final IAdvancement HORSE_250K = buildBase(HORSE_100K, "horse_250k").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.SADDLE)).build();
	public static final IAdvancement HORSE_500K = buildBase(HORSE_250K, "horse_500k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(wrathHorseItem())).build();
	public static final IAdvancement PIG_100M = buildBase(WALK_10KM, "pig_100m").display(display().y(5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SADDLE)).build();
	public static final IAdvancement PIG_1KM = buildBase(PIG_100M, "pig_1km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.CARROT_ON_A_STICK)).build();
	public static final IAdvancement PIG_10KM = buildBase(PIG_1KM, "pig_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.texturedHead(MoreMobHeads.PIG_REGULAR))).build();
	public static final IAdvancement PIG_25KM = buildBase(PIG_10KM, "pig_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.PORKCHOP)).build();
	public static final IAdvancement PIG_50KM = buildBase(PIG_25KM, "pig_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(Material.COOKED_PORKCHOP)).build();
	public static final IAdvancement PIG_100KM = buildBase(PIG_50KM, "pig_100km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(TrappedNewbieItems.ASH)).build();
	public static final IAdvancement PIG_250KM = buildBase(PIG_100KM, "pig_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(PIG_HOG_HEAD)).build();
	public static final IAdvancement ELYTRA_10KM = buildBase(WALK_10KM, "elytra_10km").display(display().y(6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PHANTOM_MEMBRANE)).build();
	public static final IAdvancement ELYTRA_100KM = buildBase(ELYTRA_10KM, "elytra_100km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.ELYTRA)).build();
	public static final IAdvancement ELYTRA_1000KM = buildBase(ELYTRA_100KM, "elytra_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.FIREWORK_ROCKET)).build();
	public static final IAdvancement ELYTRA_2500KM = buildBase(ELYTRA_1000KM, "elytra_2500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.PURPUR_BLOCK)).build();
	public static final IAdvancement ELYTRA_5000KM = buildBase(ELYTRA_2500KM, "elytra_5000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(Material.FIREWORK_STAR)).build();
	public static final IAdvancement ELYTRA_10000KM = buildBase(ELYTRA_5000KM, "elytra_10000km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(brokenElytra())).build();
	public static final IAdvancement ELYTRA_15000KM = buildBase(ELYTRA_10000KM, "elytra_15000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(ItemUtil.glint(Material.ELYTRA))).build();
	// Left bottom
	public static final IAdvancement SWIM_1KM = buildBase(WALK_10KM, "swim_1km").display(display().y(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(new ItemStack(Material.LEATHER_LEGGINGS).withColor(Color.BLUE))).build();
	public static final IAdvancement SWIM_10KM = buildBase(SWIM_1KM, "swim_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.GOLDEN_LEGGINGS)).build();
	public static final IAdvancement SWIM_50KM = buildBase(SWIM_10KM, "swim_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.GOLD_BLOCK)).build();
	public static final IAdvancement SWIM_100KM = buildBase(SWIM_50KM, "swim_100km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.WATER_BUCKET)).build();
	public static final IAdvancement SWIM_250KM = buildBase(SWIM_100KM, "swim_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(ItemUtil.glint(Material.RAW_GOLD_BLOCK))).build();
	public static final IAdvancement SWIM_500KM = buildBase(SWIM_250KM, "swim_500km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.CONDUIT)).build();
	public static final IAdvancement SWIM_1000KM = buildBase(SWIM_500KM, "swim_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(Material.COD)).build();
	public static final IAdvancement SNEAK_100M = buildBase(WALK_10KM, "sneak_100m").display(display().y(-2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(new ItemStack(Material.LEATHER_BOOTS).withColor(Color.BLACK))).build();
	public static final IAdvancement SNEAK_1KM = buildBase(SNEAK_100M, "sneak_1km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(new ItemStack(Material.LEATHER_CHESTPLATE).withColor(Color.BLACK))).build();
	public static final IAdvancement SNEAK_10KM = buildBase(SNEAK_1KM, "sneak_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.PANDA_SPAWN_EGG)).build();
	public static final IAdvancement SNEAK_25KM = buildBase(SNEAK_10KM, "sneak_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(potionItem(Material.POTION, PotionType.INVISIBILITY))).build();
	public static final IAdvancement SNEAK_50KM = buildBase(SNEAK_25KM, "sneak_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(Material.BARRIER)).build();
	public static final IAdvancement SNEAK_100KM = buildBase(SNEAK_50KM, "sneak_100km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.STRUCTURE_VOID)).build();
	public static final IAdvancement SNEAK_250KM = buildBase(SNEAK_100KM, "sneak_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(SHIFTING_HEAD)).build();
	public static final IAdvancement CLIMB_100M = buildBase(WALK_10KM, "climb_100m").display(display().y(-3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.VINE)).build();
	public static final IAdvancement CLIMB_500M = buildBase(CLIMB_100M, "climb_500m").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.WEEPING_VINES)).build();
	public static final IAdvancement CLIMB_3KM = buildBase(CLIMB_500M, "climb_3km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.LADDER)).build();
	public static final IAdvancement CLIMB_10KM = buildBase(CLIMB_3KM, "climb_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(LADDER_ACE_HEAD)).build();
	public static final IAdvancement MINECART_1KM = buildBase(WALK_10KM, "minecart_1km").display(display().y(-4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.MINECART)).build();
	public static final IAdvancement MINECART_10KM = buildBase(MINECART_1KM, "minecart_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.FURNACE_MINECART)).build();
	public static final IAdvancement MINECART_50KM = buildBase(MINECART_10KM, "minecart_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.COMMAND_BLOCK_MINECART)).build();
	public static final IAdvancement MINECART_250KM = buildBase(MINECART_50KM, "minecart_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(MINECART_HEAD)).build();
	public static final IAdvancement STRIDER_100M = buildBase(WALK_10KM, "strider_100m").display(display().y(-5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.LAVA_BUCKET)).build();
	public static final IAdvancement STRIDER_1KM = buildBase(STRIDER_100M, "strider_1km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.WARPED_FUNGUS_ON_A_STICK)).build();
	public static final IAdvancement STRIDER_10KM = buildBase(STRIDER_1KM, "strider_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.STRIDER_SPAWN_EGG)).build();
	public static final IAdvancement STRIDER_25KM = buildBase(STRIDER_10KM, "strider_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(Material.WARPED_FUNGUS)).build();
	public static final IAdvancement STRIDER_50KM = buildBase(STRIDER_25KM, "strider_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().icon(Material.LEAD)).build();
	public static final IAdvancement STRIDER_100KM = buildBase(STRIDER_50KM, "strider_100km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().icon(Material.LEAD)).build();
	public static final IAdvancement STRIDER_250KM = buildBase(STRIDER_100KM, "strider_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).challengeFrame().icon(ItemUtil.texturedHead(MoreMobHeads.STRIDER))).build();

	private static ItemStack braveNewWorldItem() {
		var item = new ItemStack(TrappedNewbieItems.LETTER);
		NBT.modify(item, nbt -> {
			nbt.setItemStackArray(LetterModifier.CONTENTS_TAG, new ItemStack[]{WANDERING_TRADER_HEAD});
			nbt.setEnum(LetterModifier.TYPE_TAG, LetterModifier.LetterType.STAR);
		});
		return item;
	}

	private static ItemStack brokenElytra() {
		var item = new ItemStack(Material.ELYTRA);
		item.setData(DataComponentTypes.DAMAGE, 1);
		item.setData(DataComponentTypes.MAX_DAMAGE, 1);
		return item;
	}

	private static ItemStack wrathHorseItem() {
		var item = new ItemStack(Material.LEATHER_HORSE_ARMOR);
		item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
		item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.fromRGB(10409302)));
		return item;
	}

	private static ItemStack potionItem(Material itemType, PotionType potionType) {
		var item = new ItemStack(itemType);
		item.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(potionType).build());
		return item;
	}

	private static FancyAdvancementDisplay.FancyAdvancementDisplayImpl display() {
		return FancyAdvancementDisplay.fancyDisplay().noAnnounceChat().fancyDescriptionParent(NamedTextColor.GRAY);
	}

	public static void setupAdvancements() {
		MANAGER.registerTabs(
			REQUIEM_TAB, BASICS_TAB, STATISTICS_TAB
		);

		REQUIEM_TAB.registerAdvancements(
			REQUIEM_ROOT, OPENING_HOLDER, BRAVE_NEW_WORLD, FIRST_POSSESSION, GOOD_AS_NEW, MERE_MORTAL, I_HATE_SAND, KUNG_FU_PANDA
		);

		BASICS_TAB.registerAdvancements(
			BASICS_ROOT,
			GET_A_FIBER, MAKE_A_TWINE,
			FIND_GRAVEL, GET_A_FLINT, GET_A_FLAKED_FLINT,
			MAKE_FLINT_SHEARS, //GET_LEAVES, GET_A_DEAD_BUSH,
			GET_A_BRANCH, MAKE_ROUGH_STICKS,
//			EXPELLIARMUS,// SLAPFISH, DIEMONDS, INSPECTOR_GADGET, ITS_TIME_CONSUMING, ROCK_PAPER_SHEARS, // ToDo
			MAKE_A_FLINT_AXE, GET_A_LOG, MAKE_A_CHOPPING_BLOCK, MAKE_PLANKS, MAKE_STICKS,
			/*MAKE_A_GRASS_MESH, TREASURE_HUNT, MEET_THE_FLINTSTONES, */MAKE_A_FIRE, MAKE_A_FIRE_FILLER,
//			GET_A_CHARCOAL, MAKE_A_FIRESTRIKER, CAMPING_OUT, SPAWN_CAMPING,
//			MAKE_A_FLINT_KNIFE, GET_A_STRING, MAKE_A_CARPET,
//			GET_A_WOOL,
//			MAKE_A_SLEEPING_BAG, SLEEP_IN_BED, EAT_A_ROASTED_SPIDER_EYE, LUCID_DREAMING,
//			MAKE_A_WORK_STATION, MAKE_A_FLINT_PICKAXE, GET_A_ROCK, MAKE_A_COBBLESTONE,
//			MAKE_A_COBBLESTONE_HAMMER, FLOWERS_FOR_YOU,
//			MAKE_A_FLINT_SHOVEL, PATHWAYS, GET_A_CLAY_BALL, MAKE_A_CLAY_KILN,
//			MAKE_A_STONE, MAKE_A_STONE_TOOL, MOAR_TOOLS,
//			GET_A_BRICK, MAKE_A_POT,
//			// Mimics
			MAKE_FLINT_SHEARS_TO_GET_A_BRANCH_MIMIC, GET_A_FLAKED_FLINT_TO_MAKE_A_TWINE_MIMIC
//			GET_A_CLAY_BALL_TO_MAKE_A_WORK_STATION_MIMIC, MAKE_A_FIRE_TO_GET_A_BRICK_MIMIC,
//			EAT_A_ROASTED_SPIDER_EYE_TO_LUCID_DREAMING_MIMIC
		);

		STATISTICS_TAB.registerAdvancements(
			STATISTICS_ROOT, STATISTICS_RIGHT_LINKER, STATISTICS_UP_LINKER,
			PLAY_1D, PLAY_100D, PLAY_365D, PLAY_1000D,
			BELL_100, BELL_1K, BELL_10K, BELL_100K, BELL_1000K, BELL_10000K,
			LEVEL_30, LEVEL_100, LEVEL_300, LEVEL_1000, LEVEL_2500, LEVEL_5000,
			ENCHANT_10, ENCHANT_50, ENCHANT_250, ENCHANT_1000, ENCHANT_2500, ENCHANT_5K, ENCHANT_10K,
			WASH_10, WASH_50, WASH_250, WASH_1K, WASH_5K, WASH_10K,
			FISH_250, FISH_1K, FISH_2500, FISH_5K, FISH_10K, FISH_50K,
			EAT_200, EAT_1K, EAT_2500, EAT_5K, EAT_10K, EAT_25K, EAT_50K,
			TOTEM_5, TOTEM_10, TOTEM_25, TOTEM_50, TOTEM_100, TOTEM_250, TOTEM_500, TOTEM_1000, TOTEM_2500, TOTEM_5000,
			RAID_5, RAID_20, RAID_100, RAID_2000,
			BREED_100, BREED_500, BREED_2500, BREED_5000, BREED_10000, BREED_15000, BREED_25000,
			KILL_250, KILL_2500, KILL_25K, KILL_50K, KILL_100K, KILL_250K, KILL_500K,
			TRADE_100, TRADE_500, TRADE_2500, TRADE_10K, TRADE_25K, TRADE_50K, TRADE_250K,
			LOOT_10, LOOT_100, LOOT_500, LOOT_1000, LOOT_2500, LOOT_5000, LOOT_10000,
			OPEN_CHEST_100, OPEN_CHEST_1K, OPEN_CHEST_10K, OPEN_CHEST_25K,
			OPEN_SHULKER_100, OPEN_SHULKER_1K, OPEN_SHULKER_10K, OPEN_SHULKER_100K,
			OPEN_CRAFTING_TABLE_15, OPEN_CRAFTING_TABLE_100, OPEN_CRAFTING_TABLE_500, OPEN_CRAFTING_TABLE_2500,
			BREAK_100_IRON, BREAK_2500_DIAMOND, BREAK_10K_NETHERITE, BREAK_100K_NETHERITE,
			SURVIVE_1H, SURVIVE_10H, SURVIVE_50H, SURVIVE_200H,
			DEATHS_1, DEATHS_50, DEATHS_250, DEATHS_1000,
//			HORSE_SPEED_9, HORSE_SPEED_11, HORSE_SPEED_13, HORSE_SPEED_14, // ToDo
			WALK_10KM, WALK_50KM, WALK_250KM, WALK_1000KM, WALK_5000KM, WALK_10000KM,
			SPRINT_10KM, SPRINT_MARATHON, SPRINT_250KM, SPRINT_1000KM, SPRINT_2500KM, SPRINT_5000KM, SPRINT_10000KM,
			JUMP_1K, JUMP_10K, JUMP_50K, JUMP_100K, JUMP_250K, JUMP_500K, JUMP_1000K,
			BOAT_1KM, BOAT_10KM, BOAT_25KM, BOAT_50KM, BOAT_100KM, BOAT_250KM, BOAT_500KM,
			PIG_100M, PIG_1KM, PIG_10KM, PIG_25KM, PIG_50KM, PIG_100KM, PIG_250KM,
			HORSE_1K, HORSE_10K, HORSE_25K, HORSE_50K, HORSE_100K, HORSE_250K, HORSE_500K,
			ELYTRA_10KM, ELYTRA_100KM, ELYTRA_1000KM, ELYTRA_2500KM, ELYTRA_5000KM, ELYTRA_10000KM, ELYTRA_15000KM,
			SWIM_1KM, SWIM_10KM, SWIM_50KM, SWIM_100KM, SWIM_250KM, SWIM_500KM, SWIM_1000KM,
			SNEAK_100M, SNEAK_1KM, SNEAK_10KM, SNEAK_25KM, SNEAK_50KM, SNEAK_100KM, SNEAK_250KM,
			CLIMB_100M, CLIMB_500M, CLIMB_3KM, CLIMB_10KM,
			MINECART_1KM, MINECART_10KM, MINECART_50KM, MINECART_250KM,
			STRIDER_100M, STRIDER_1KM, STRIDER_10KM, STRIDER_25KM, STRIDER_50KM, STRIDER_100KM, STRIDER_250KM
		);
	}

}
