package me.sosedik.trappednewbie.dataset;

import de.tr7zw.nbtapi.NBT;
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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.alwaysDone;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.requirements;
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

	public static final ItemStack WANDERING_TRADER_HEAD = ItemUtil.texturedHead(MoreMobHeads.WANDERING_TRADER);
	public static final ItemStack ZOMBIE_VILLAGER_HEAD = ItemUtil.texturedHead(MoreMobHeads.ZOMBIE_VILLAGER);

	public static final AdvancementManager MANAGER = new AdvancementManager(new JsonStorage(TrappedNewbie.instance()));

	public static final AdvancementTab REQUIEM_TAB = buildTab("requiem", MANAGER).inverseY().backgroundPathBlock(Material.SOUL_SAND).icon(Material.SKELETON_SKULL).build();
	public static final IRootAdvancement REQUIEM_ROOT = buildRoot(REQUIEM_TAB).display(display().icon(Material.SUNFLOWER)).requiredProgress(requirements("interact", "open")).visibilityRule(hidden()).build();
	public static final IAdvancement OPENING_HOLDER = buildBase(REQUIEM_ROOT, "holder").display(display().x(-1.25F).icon(WANDERING_TRADER_HEAD)).requiredProgress(alwaysDone()).build();
	public static final IAdvancement BRAVE_NEW_WORLD = buildBase(REQUIEM_ROOT, "brave_new_world").display(display().x(1F).icon(braveNewWorldItem())).requiredProgress(requirements("friendship", "fall")).build();
	public static final IAdvancement FIRST_POSSESSION = buildBase(BRAVE_NEW_WORLD, "first_possession").display(display().x(1.25F).icon(RequiemItems.HOST_REVOCATOR)).build();
	public static final IAdvancement GOOD_AS_NEW = buildBase(FIRST_POSSESSION, "good_as_new").display(display().x(1F).icon(ZOMBIE_VILLAGER_HEAD)).build();
	public static final IAdvancement MERE_MORTAL = new MereMortalAdvancement(buildBase(GOOD_AS_NEW, "mere_mortal").display(display().x(1F).icon(Material.PLAYER_HEAD)).visibilityRule(hidden())); // ToDo
	public static final IAdvancement I_HATE_SAND = buildBase(FIRST_POSSESSION, "i_hate_sand").display(display().xy(1F, 1F).icon(Material.SAND).goalFrame()).build();
	public static final IAdvancement KUNG_FU_PANDA = buildBase(FIRST_POSSESSION, "kung_fu_panda").display(display().xy(1F, -1F).icon(Material.BAMBOO).goalFrame()).build();

	public static final AdvancementTab BASICS_TAB = buildTab("basics", MANAGER).inverseY().backgroundPathBlock(Material.GRAVEL).build();
	public static final IRootAdvancement BASICS_ROOT = buildRoot(BASICS_TAB).display(display().icon(Material.ROOTED_DIRT)).visibilityRule(ifDone(false, GOOD_AS_NEW)).requiredProgress(alwaysDone()).build();
	public static final IAdvancement GET_A_FIBER = buildBase(BASICS_ROOT, "get_a_fiber").display(display().x(1.5F).icon(TrappedNewbieItems.FIBER))
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FIBER))))
			.build();
	public static final IAdvancement MAKE_A_TWINE = buildBase(GET_A_FIBER, "make_a_twine").display(display().x(1F).icon(TrappedNewbieItems.TWINE)).visibilityRule(parentGranted())
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.TWINE))))
			.build();
	public static final IAdvancement FIND_GRAVEL = buildBase(BASICS_ROOT, "find_gravel").display(display().xy(1F, -1F).icon(Material.GRAVEL)).visibilityRule(ifDone(MAKE_A_TWINE)).build();
	public static final IAdvancement GET_A_FLINT = buildBase(FIND_GRAVEL, "get_a_flint").display(display().x(1F).icon(Material.FLINT)).visibilityRule(parentGranted())
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(Material.FLINT))))
			.build();
	public static final IAdvancement GET_A_FLAKED_FLINT = buildBase(GET_A_FLINT, "get_a_flaked_flint").display(display().x(1F).icon(TrappedNewbieItems.FLAKED_FLINT)).visibilityRule(parentGranted())
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLAKED_FLINT))))
			.build();
	public static final IAdvancement MAKE_FLINT_SHEARS = buildMulti(GET_A_FLAKED_FLINT, "make_flint_shears", MAKE_A_TWINE).linkingToAll(false).display(display().xy(1F, 0.5F).icon(TrappedNewbieItems.FLINT_SHEARS).goalFrame()).visibilityRule(parentGranted())
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_SHEARS))))
			.build();
	public static final IAdvancement GET_A_BRANCH = buildBase(BASICS_ROOT, "get_a_branch").display(display().xy(2F, 1F).icon(TrappedNewbieItems.OAK_BRANCH))
			.visibilityRule(ifDone(GET_A_FLAKED_FLINT))
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.BRANCHES))))
			.build();
	public static final IAdvancement MAKE_ROUGH_STICKS = buildBase(MAKE_FLINT_SHEARS, "make_rough_sticks").display(display().xy(1F, 0.75F).icon(TrappedNewbieItems.ROUGH_STICK))
			.requiredProgress(vanilla(VanillaTriggerData.inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.ROUGH_STICK))))
			.build();

	public static final IAdvancement MAKE_A_FIRE = buildBase(MAKE_ROUGH_STICKS, "make_a_fire").display(display().xy(1.5F, -0.9F).icon(TrappedNewbieItems.INVENTORY_FIRE).goalFrame()).visibilityRule(grandParentGranted()).build();
	public static final IAdvancement MAKE_A_FIRE_FILLER = buildFake(MAKE_A_FIRE).display(display().x(0.5F).isHidden(true)).visibilityRule(parentGranted()).build();

	private static final IAdvancement MAKE_FLINT_SHEARS_TO_GET_A_BRANCH_MIMIC = buildLinking(MAKE_FLINT_SHEARS, GET_A_BRANCH).build();
	private static final IAdvancement GET_A_FLAKED_FLINT_TO_MAKE_A_TWINE_MIMIC = buildLinking(GET_A_FLAKED_FLINT, MAKE_A_TWINE).visibilityRule(ifVisible(false, MAKE_FLINT_SHEARS)).build();

	private static ItemStack braveNewWorldItem() {
		var item = new ItemStack(TrappedNewbieItems.LETTER);
		NBT.modify(item, nbt -> {
			nbt.setItemStackArray(LetterModifier.CONTENTS_TAG, new ItemStack[]{WANDERING_TRADER_HEAD});
			nbt.setEnum(LetterModifier.TYPE_TAG, LetterModifier.LetterType.STAR);
		});
		return item;
	}

	private static FancyAdvancementDisplay.FancyAdvancementDisplayImpl display() {
		return FancyAdvancementDisplay.fancyDisplay().noAnnounceChat().fancyDescriptionParent(NamedTextColor.GRAY);
	}

	public static void setupAdvancements() {
		MANAGER.registerTabs(
			REQUIEM_TAB, BASICS_TAB
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
//			MAKE_A_FLINT_AXE, GET_A_LOG, MAKE_A_CHOPPING_BLOCK, MAKE_PLANKS, MAKE_STICKS,
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
	}

}
