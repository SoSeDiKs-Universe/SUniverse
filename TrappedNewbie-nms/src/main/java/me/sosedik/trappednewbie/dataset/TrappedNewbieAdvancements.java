package me.sosedik.trappednewbie.dataset;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BannerPatternLayers;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.Fireworks;
import io.papermc.paper.datacomponent.item.ItemContainerContents;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.PotionContents;
import io.papermc.paper.datacomponent.item.UseCooldown;
import me.sosedik.miscme.dataset.MoreMobHeads;
import me.sosedik.packetadvancements.api.advancement.IAdvancement;
import me.sosedik.packetadvancements.api.advancement.root.IRootAdvancement;
import me.sosedik.packetadvancements.api.tab.AdvancementManager;
import me.sosedik.packetadvancements.api.tab.AdvancementTab;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.DamageTriggerCondition;
import me.sosedik.packetadvancements.imlp.progress.vanilla.conditions.ItemTriggerCondition;
import me.sosedik.packetadvancements.util.storage.JsonStorage;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.advancement.display.AdvancementFrame;
import me.sosedik.trappednewbie.api.advancement.display.AnnouncementMessage;
import me.sosedik.trappednewbie.api.advancement.display.FancierAdvancementDisplay;
import me.sosedik.trappednewbie.api.advancement.display.OpeningHolderAdvancementDisplay;
import me.sosedik.trappednewbie.api.advancement.reward.FancyAdvancementReward;
import me.sosedik.trappednewbie.impl.advancement.AttackSquidInTheAirWithASnowballAdvancement;
import me.sosedik.trappednewbie.impl.advancement.AttackWithAnEggAdvancement;
import me.sosedik.trappednewbie.impl.advancement.AttackZombieWithAnEggAdvancement;
import me.sosedik.trappednewbie.impl.advancement.GetABannerShieldAdvancement;
import me.sosedik.trappednewbie.impl.advancement.InspectorGadgetAdvancement;
import me.sosedik.trappednewbie.impl.advancement.MasterShieldsmanAdvancement;
import me.sosedik.trappednewbie.impl.advancement.MereMortalAdvancement;
import me.sosedik.trappednewbie.impl.advancement.RockPaperShearsAdvancement;
import me.sosedik.trappednewbie.impl.advancement.Walk10KKMAdvancement;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.trappednewbie.listener.advancement.AdvancementTrophies;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.util.ItemUtil;
import me.sosedik.utilizer.util.MiscUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.alwaysDone;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.neverDone;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.requirements;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.simple;
import static me.sosedik.packetadvancements.api.progression.RequiredAdvancementProgress.vanilla;
import static me.sosedik.packetadvancements.api.tab.AdvancementTab.buildTab;
import static me.sosedik.packetadvancements.imlp.advancement.base.BaseAdvancement.buildBase;
import static me.sosedik.packetadvancements.imlp.advancement.fake.FakeAdvancement.buildFake;
import static me.sosedik.packetadvancements.imlp.advancement.linking.LinkingAdvancement.buildLinking;
import static me.sosedik.packetadvancements.imlp.advancement.multi.MultiParentAdvancement.buildMulti;
import static me.sosedik.packetadvancements.imlp.advancement.root.RootAdvancement.buildRoot;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.hidden;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.ifDone;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.ifVisible;
import static me.sosedik.packetadvancements.imlp.display.AdvancementVisibilities.parentGranted;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.entityHurtPlayer;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.fishingRodHooked;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.inventoryChanged;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerHurtEntity;
import static me.sosedik.packetadvancements.imlp.progress.vanilla.types.VanillaTriggerData.playerKilledEntity;
import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

@NullMarked
@SuppressWarnings("unused")
public class TrappedNewbieAdvancements {

	private static final ItemStack WANDERING_TRADER_HEAD = ItemUtil.texturedHead(MoreMobHeads.WANDERING_TRADER);
	private static final ItemStack SHIFTING_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ4NmQwOTZiOGMwOGFkNWRhNmVhY2E5YTEwMTc1MWZiYzY4NzcwZDY3ZDUzMWU4Y2ZhNTYzYmE3M2Y5N2EzYyJ9fX0=");
	private static final ItemStack PIG_HEAD = ItemUtil.texturedHead(MoreMobHeads.PIG_REGULAR);
	private static final ItemStack MR_SHEEP_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ0M2M0YjAwNDNhZWQyYThjMDAzNTEzZjI3OTgwMDM1NzY0MjUzMzg3N2UzZjJkNjhmZDkyYjY5NGJlM2E4NiJ9fX0=");
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
	private static final ItemStack STONKS_HEAD = ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWUxNzBmMzZmNjY3MThmOGE2OWMyNWI4YTk5ZTI5ZDMwNjYxODU1M2MyM2IxZmUwNzBjZmViMzU5Y2NhMGU3YSJ9fX0=");
	private static final TextColor PURPLE = AnnouncementMessage.SUPER_TORTURE.getColor();
	private static final TextColor GRAY = requireNonNull(TextColor.fromHexString("#cccccc"));

	public static final AdvancementManager MANAGER = new AdvancementManager(new JsonStorage(TrappedNewbie.instance()));

	public static final AdvancementTab REQUIEM_TAB = buildTab("requiem", MANAGER).inverseY().backgroundPathBlock(Material.SOUL_SAND).icon(Material.SKELETON_SKULL).build();
	public static final IRootAdvancement REQUIEM_ROOT = buildRoot(REQUIEM_TAB).display(display().xy(0F, 0F).noAnnounceChat().icon(Material.SUNFLOWER)).requiredProgress(requirements("interact", "open"))
		.visibilityRule(hidden())
		.buildAndRegister();
	public static final IAdvancement OPENING_HOLDER = buildFake(REQUIEM_ROOT, "holder").display(new OpeningHolderAdvancementDisplay().x(-1.25F).noAnnounceChat().withAdvancementFrame(AdvancementFrame.SPEECH_BUBBLE).icon(WANDERING_TRADER_HEAD))
		.requiredProgress(alwaysDone())
		.buildAndRegister();
	public static final IAdvancement BRAVE_NEW_WORLD = buildBase(REQUIEM_ROOT, "brave_new_world").display(display().x(1F).noAnnounceChat().icon(braveNewWorldItem())).requiredProgress(requirements("friendship", "fall")).buildAndRegister();
	public static final IAdvancement FIRST_POSSESSION = buildBase(BRAVE_NEW_WORLD, "first_possession").display(display().x(1.25F).withAdvancementFrame(AdvancementFrame.SHARP).icon(RequiemItems.HOST_REVOCATOR))
		.visibilityRule(parentGranted())
		.buildAndRegister();
	public static final IAdvancement GOOD_AS_NEW = buildBase(FIRST_POSSESSION, "good_as_new").display(display().x(1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(ItemUtil.texturedHead(MoreMobHeads.ZOMBIE_VILLAGER_PLAINS_ARMORER)))
		.buildAndRegister();
	public static final IAdvancement MERE_MORTAL = buildBase(GOOD_AS_NEW, "mere_mortal").display(display().x(1F).icon(Material.PLAYER_HEAD)).visibilityRule(hidden())
		.buildAndRegister(MereMortalAdvancement::new); // ToDo
	public static final IAdvancement I_HATE_SAND = buildBase(FIRST_POSSESSION, "i_hate_sand").display(display().xy(1F, 1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SAND))
		.visibilityRule(parentGranted())
		.buildAndRegister();
	public static final IAdvancement KUNG_FU_PANDA = buildBase(FIRST_POSSESSION, "kung_fu_panda").display(display().xy(1F, -1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BAMBOO))
		.visibilityRule(parentGranted())
		.buildAndRegister();

	public static final AdvancementTab BASICS_TAB = buildTab("basics", MANAGER).inverseY().backgroundPathBlock(Material.GRAVEL).build();
	public static final IRootAdvancement BASICS_ROOT = buildRoot(BASICS_TAB).display(display().xy(0F, 0F).withAdvancementFrame(AdvancementFrame.SQUIRCLE).icon(Material.ROOTED_DIRT))
			.visibilityRule(ifDone(false, GOOD_AS_NEW))
			.requiredProgress(alwaysDone())
			.buildAndRegister();
	public static final IAdvancement GET_A_FIBER = buildBase(BASICS_ROOT, "get_a_fiber").display(display().x(1.5F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.FIBER))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FIBER))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_TWINE = buildBase(GET_A_FIBER, "make_a_twine").display(display().x(1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.TWINE))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.TWINE))))
			.buildAndRegister();
	public static final IAdvancement GET_A_ROCK = buildBase(BASICS_ROOT, "get_a_rock").display(display().xy(-0.2F, -1F).icon(TrappedNewbieItems.ROCK))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.ROCK))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_COBBLESTONE = buildBase(GET_A_ROCK, "make_a_cobblestone").display(display().x(-1F).icon(Material.COBBLESTONE))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.COBBLESTONE))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_COBBLESTONE_HAMMER = buildBase(MAKE_A_COBBLESTONE, "make_a_cobblestone_hammer").display(display().x(-1F).goalFrame().icon(TrappedNewbieItems.COBBLESTONE_HAMMER))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.COBBLESTONE_HAMMER))))
			.buildAndRegister();
	public static final IAdvancement FIND_GRAVEL = buildBase(BASICS_ROOT, "find_gravel").display(display().xy(1F, -1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(Material.GRAVEL))
			.buildAndRegister();
	public static final IAdvancement GET_A_FLINT = buildBase(FIND_GRAVEL, "get_a_flint").display(display().x(1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(Material.FLINT))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.FLINT))))
			.buildAndRegister();
	public static final IAdvancement GET_A_FLAKED_FLINT = buildBase(GET_A_FLINT, "get_a_flaked_flint").display(display().x(1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.FLAKED_FLINT))
			.buildAndRegister();
	public static final IAdvancement MAKE_FLINT_SHEARS = buildMulti(GET_A_FLAKED_FLINT, "make_flint_shears", MAKE_A_TWINE).linkingToAll(false).display(display().xy(1F, 0.5F).goalFrame().icon(TrappedNewbieItems.FLINT_SHEARS))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_SHEARS))))
			.buildAndRegister();
	public static final IAdvancement GET_A_BRANCH = buildBase(BASICS_ROOT, "get_a_branch").display(display().xy(2F, 1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.OAK_BRANCH))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.BRANCHES))))
			.buildAndRegister();
	public static final IAdvancement MAKE_ROUGH_STICKS = buildBase(MAKE_FLINT_SHEARS, "make_rough_sticks").display(display().xy(1.2F, 0.75F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.ROUGH_STICK))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.ROUGH_STICK))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_FLINT_AXE = buildBase(MAKE_ROUGH_STICKS, "make_a_flint_axe").display(display().y(2F).withAdvancementFrame(AdvancementFrame.ARROW_LEFT).icon(TrappedNewbieItems.FLINT_AXE))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_AXE))))
			.buildAndRegister();
	public static final IAdvancement GET_A_LOG = buildBase(MAKE_A_FLINT_AXE, "get_a_log").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(Material.OAK_LOG))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Tag.LOGS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_CHOPPING_BLOCK = buildBase(GET_A_LOG, "make_a_chopping_block").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.OAK_CHOPPING_BLOCK))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.ITEM_CHOPPING_BLOCKS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_PLANKS = buildBase(MAKE_A_CHOPPING_BLOCK, "make_planks").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.SHARP).icon(Material.OAK_PLANKS))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Tag.PLANKS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_STICKS = buildBase(MAKE_PLANKS, "make_sticks").display(display().x(-1.05F).withAdvancementFrame(AdvancementFrame.SHARP).icon(TrappedNewbieItems.OAK_STICK))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.STICKS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_GRASS_MESH = buildBase(BASICS_ROOT, "make_a_grass_mesh").display(display().xy(1F, -2.5F).goalFrame().icon(TrappedNewbieItems.GRASS_MESH))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.GRASS_MESH))))
			.buildAndRegister();
	public static final IAdvancement TREASURE_HUNT = buildBase(MAKE_A_GRASS_MESH, "treasure_hunt").display(display().x(1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BOWL)).buildAndRegister();
	public static final IAdvancement MEET_THE_FLINTSTONES = buildBase(TREASURE_HUNT, "meet_the_flintstones").display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.FLINT))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.FLINT).withMinAmount(64))))
			.buildAndRegister();
	public static final IAdvancement CAMPING_OUT = buildBase(MAKE_ROUGH_STICKS, "camping_out").display(display().xy(1.2F, -1F).withAdvancementFrame(AdvancementFrame.BLOCK).icon(Material.CAMPFIRE))
			.visibilityRule(parentGranted())
			.buildAndRegister();
	public static final IAdvancement SPAWN_CAMPING = buildBase(CAMPING_OUT, "spawn_camping").display(display().xy(0.5F, 1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.SOUL_CAMPFIRE)).buildAndRegister();
	public static final IAdvancement MAKE_A_FIRE = buildBase(CAMPING_OUT, "make_a_fire").display(display().x(1F).goalFrame().icon(TrappedNewbieItems.INVENTORY_FIRE))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_FIRE_FILLER = buildFake(MAKE_A_FIRE).display(display().x(0.25F).isHidden(true))
			.requiredProgress(neverDone())
			.buildAndRegister();
	public static final IAdvancement GET_A_CHARCOAL = buildBase(MAKE_A_FIRE_FILLER, "get_a_charcoal").display(display().xy(1F, -0.55F).icon(Material.CHARCOAL))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.CHARCOAL))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_FIRESTRIKER = buildBase(GET_A_CHARCOAL, "make_a_firestriker").display(display().x(1F).goalFrame().icon(TrappedNewbieItems.FIRESTRIKER))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FIRESTRIKER))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_FLINT_KNIFE = buildBase(MAKE_ROUGH_STICKS, "make_a_flint_knife").display(display().xy(1F, -3.2F).goalFrame().icon(TrappedNewbieItems.FLINT_KNIFE))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_KNIFE))))
			.buildAndRegister();
	public static final IAdvancement GET_A_STRING = buildBase(MAKE_A_FLINT_KNIFE, "get_a_string").display(display().xy(1.2F, -0.5F).icon(Material.STRING))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.STRING))))
			.buildAndRegister();
	public static final IAdvancement GET_A_WOOL = buildBase(MAKE_A_FLINT_KNIFE, "get_a_wool").display(display().xy(1.2F, 0.5F).icon(Material.WHITE_WOOL))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Tag.WOOL))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_CARPET = buildMulti(GET_A_STRING, "make_a_carpet", GET_A_WOOL).display(display().xy(1.2F, 0.5F).withAdvancementFrame(AdvancementFrame.HEART).icon(Material.WHITE_CARPET))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Tag.WOOL_CARPETS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_SLEEPING_BAG = buildBase(MAKE_A_CARPET, "make_a_sleeping_bag").display(display().x(1F).icon(ItemStack.of(TrappedNewbieItems.SLEEPING_BAG).withColor(DyeColor.WHITE)))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.SLEEPING_BAG))))
			.buildAndRegister();
//	public static final IAdvancement SLEEP_IN_BED = buildBase(MAKE_A_SLEEPING_BAG, "sleep_in_bed").display(display().x(1F).challengeFrame().icon(Material.WHITE_BED)) // TODO
//			.visibilityRule(grandParentGranted())
//			.buildAndRegister();
//	public static final IAdvancement EAT_A_ROASTED_SPIDER_EYE = buildBase(CAMPING_OUT, "eat_a_roasted_spider_eye").display(display().xy(1.2F, -0.6F).icon(Material.SPIDER_EYE/*DelightfulFarmingItems.ROASTED_SPIDER_EYE*/)) // TODO
//			.visibilityRule(ifDone(SLEEP_IN_BED))
////			.requiredProgress(vanilla(consumeItem().withItem(ItemTriggerCondition.of(DelightfulFarmingItems.ROASTED_SPIDER_EYE))))
//			.buildAndRegister();
//	public static final IAdvancement LUCID_DREAMING = buildBase(SLEEP_IN_BED, "lucid_dreaming").display(display().xy(1.5F, 0.6F).challengeFrame().icon(Material.PHANTOM_SPAWN_EGG)).visibilityRule(parentGranted()).buildAndRegister(); // TODO
	public static final IAdvancement MAKE_A_FLINT_PICKAXE = buildBase(MAKE_STICKS, "make_a_flint_pickaxe").display(display().xy(0.5F, 1.5F).goalFrame().icon(TrappedNewbieItems.FLINT_PICKAXE))
			.visibilityRule(ifDone(MAKE_PLANKS))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_PICKAXE))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_WORK_STATION = buildBase(GET_A_LOG, "make_a_work_station").display(display().xy(0.5F, 1.5F).withAdvancementFrame(AdvancementFrame.BLOCK).icon(TrappedNewbieItems.OAK_WORK_STATION))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieTags.ITEM_WORK_STATIONS))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_FLINT_SHOVEL = buildBase(MAKE_ROUGH_STICKS, "make_a_flint_shovel").display(display().xy(1.25F, 1.1F).goalFrame().icon(TrappedNewbieItems.FLINT_SHOVEL))
			.visibilityRule(parentGranted())
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLINT_SHOVEL))))
			.buildAndRegister();
	public static final IAdvancement PATHWAYS = buildBase(MAKE_A_FLINT_SHOVEL, "pathways").display(display().xy(0.5F, 1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.DIRT_PATH))
			.visibilityRule(parentGranted())
			.buildAndRegister();
	public static final IAdvancement GET_A_CLAY_BALL = buildBase(MAKE_A_FLINT_SHOVEL, "get_a_clay_ball").display(display().x(1.25F).withAdvancementFrame(AdvancementFrame.SHARP).icon(Material.CLAY_BALL))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.CLAY_BALL))))
			.buildAndRegister();
	public static final IAdvancement MAKE_A_CLAY_KILN = buildBase(GET_A_CLAY_BALL, "make_a_clay_kiln").display(display().xy(1.15F, 1.2F).withAdvancementFrame(AdvancementFrame.BLOCK).icon(TrappedNewbieItems.CLAY_KILN))
			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.CLAY_KILN))))
			.buildAndRegister();
//	public static final IAdvancement MAKE_A_STONE = buildBase(MAKE_A_CLAY_KILN, "make_a_stone").display(display().x(1F).challengeFrame().icon(Material.STONE))
//			.visibilityRule(parentGranted())
//			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE))))
//			.buildAndRegister();
//	public static final IAdvancement MAKE_A_STONE_TOOL = buildBase(MAKE_A_STONE, "make_a_stone_tool").display(display().xy(1F, 0F).goalFrame().icon(Material.STONE_PICKAXE))
//			.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_AXE, Material.STONE_PICKAXE, Material.STONE_HOE, Material.STONE_SHOVEL, Material.STONE_SWORD))))
//			.buildAndRegister();
//	public static final IAdvancement MOAR_TOOLS = buildBase(MAKE_A_STONE_TOOL, "moar_tools").display(display().xy(0.5F, 1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.STONE_SHOVEL))
//			.requiredProgress(vanilla(
//				inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_AXE)),
//				inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_PICKAXE)),
//				inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_HOE)),
//				inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_SHOVEL)),
//				inventoryChanged().withItems(ItemTriggerCondition.of(Material.STONE_SWORD)))
//			)
//			.buildAndRegister();
	public static final IAdvancement GET_A_BRICK = buildBase(GET_A_CLAY_BALL, "get_a_brick").display(display().xy(0.75F, -1F).icon(Material.BRICK))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.BRICK))))
		.buildAndRegister();
	public static final IAdvancement MAKE_A_POT = buildBase(GET_A_BRICK, "make_a_pot").display(display().x(1F).withAdvancementFrame(AdvancementFrame.CIRCLE).icon(Material.FLOWER_POT))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.FLOWER_POT))))
		.buildAndRegister();
	public static final IAdvancement GET_LEAVES = buildBase(MAKE_FLINT_SHEARS, "get_leaves").display(display().y(-1.1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_LEAVES))
		.visibilityRule(parentGranted())
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Tag.LEAVES))))
		.buildAndRegister();
	public static final IAdvancement GET_A_DEAD_BUSH = buildBase(MAKE_FLINT_SHEARS, "get_a_dead_bush").display(display().xy(1.05F, -1.1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.DEAD_BUSH))
		.visibilityRule(parentGranted())
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.DEAD_BUSH))))
		.buildAndRegister();
	public static final IAdvancement FLOWERS_FOR_YOU = buildBase(BASICS_ROOT, "flowers_for_you").display(display().x(-1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(TrappedNewbieItems.FLOWER_BOUQUET))
		.visibilityRule(ifDone(MAKE_A_WORK_STATION))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(TrappedNewbieItems.FLOWER_BOUQUET))))
		.buildAndRegister();

	private static final IAdvancement GET_A_FLAKED_FLINT_TO_MAKE_A_TWINE_MIMIC = buildLinking(GET_A_FLAKED_FLINT, MAKE_A_TWINE).visibilityRule(ifVisible(false, MAKE_FLINT_SHEARS)).buildAndRegister();
	private static final IAdvancement MAKE_FLINT_SHEARS_TO_GET_A_BRANCH_MIMIC = buildLinking(MAKE_FLINT_SHEARS, GET_A_BRANCH).buildAndRegister();
	private static final IAdvancement MAKE_A_FIRE_TO_GET_A_BRICK_MIMIC = buildLinking(MAKE_A_FIRE, GET_A_BRICK).buildAndRegister();
//	private static final IAdvancement EAT_A_ROASTED_SPIDER_EYE_TO_LUCID_DREAMING_MIMIC = buildLinking(EAT_A_ROASTED_SPIDER_EYE, LUCID_DREAMING).buildAndRegister();

	public static final AdvancementTab WEAPONRY_TAB = buildTab("weaponry", MANAGER).inverseY().backgroundPathTexture("block/smithing_table_top").build();
	public static final IRootAdvancement WEAPONRY_ROOT = buildRoot(WEAPONRY_TAB).display(display().xy(0F, 0F).withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.STONE_SWORD))
		.visibilityRule(ifDone(false, BRAVE_NEW_WORLD))
		.requiredProgress(alwaysDone())
		.buildAndRegister();
	public static final IAdvancement EXPELLIARMUS = buildBase(WEAPONRY_ROOT, "expelliarmus").display(display().x(1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemUtil.glint(TrappedNewbieItems.ROUGH_STICK)))
		.requiredProgress(vanilla(playerHurtEntity("expelliarmus").withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))).withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(ItemTriggerCondition.of(MiscUtil.combineArrays(TrappedNewbieTags.STICKS.getValues().toArray(new Material[0]), new Material[] {TrappedNewbieItems.ROUGH_STICK})))))))
		.buildAndRegister();
	public static final IAdvancement SLAPFISH = buildBase(EXPELLIARMUS, "slapfish").display(display().x(1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.COD))
		.requiredProgress(vanilla(playerHurtEntity("slapfish").withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))).withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(Tag.ITEMS_FISHES)))))
		.buildAndRegister();
	public static final IAdvancement DIEMONDS = buildBase(SLAPFISH, "diemonds").display(display().x(1F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.DIAMOND))
		.requiredProgress(vanilla(playerHurtEntity("diemonds").withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))).withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(Material.DIAMOND)))))
		.buildAndRegister();
	public static final IAdvancement INSPECTOR_GADGET = buildBase(DIEMONDS, "inspector_gadget").display(display().xy(1F, -0.5F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SPYGLASS))
		.withReward(rewards().addItems(ItemStack.of(Material.COPPER_INGOT, 4), ItemStack.of(Material.AMETHYST_SHARD, 4)))
		.requiredProgress(vanilla(playerKilledEntity().withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))).withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(Material.SPYGLASS)))))
		.buildAndRegister(InspectorGadgetAdvancement::new);
	public static final IAdvancement ROCK_PAPER_SHEARS = buildBase(INSPECTOR_GADGET, "rock_paper_shears").display(display().x(1F).withAdvancementFrame(AdvancementFrame.STAR).fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.SHEARS)).buildAndRegister(RockPaperShearsAdvancement::new);
	public static final IAdvancement ITS_TIME_CONSUMING = buildBase(DIEMONDS, "its_time_consuming").display(display().xy(1F, 0.5F).withAdvancementFrame(AdvancementFrame.CIRCLE).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CLOCK))
		.withReward(rewards().addItems(ItemStack.of(Material.GOLD_INGOT, 4)))
		.requiredProgress(vanilla(playerKilledEntity().withEntity(entity -> entity.withDistanceToPlayer(distance -> distance.maxAbsolute(5D))).withPlayer(player -> player.withEquipment(equipment -> equipment.withMainHand(Material.CLOCK)))))
		.buildAndRegister();
	public static final IAdvancement DIE_TWICE_WITHIN_10S = buildBase(ITS_TIME_CONSUMING, "die_twice_within_10s").display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.GREEN).icon(banner(Material.BLACK_BANNER, new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT), new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP), new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE), new Pattern(DyeColor.BLACK, PatternType.BORDER)))).buildAndRegister();
	public static final IAdvancement DIE_TWICE_WITHIN_5S = buildBase(DIE_TWICE_WITHIN_10S, "die_twice_within_5s").display(display().x(1F).challengeFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(banner(Material.WHITE_BANNER, new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM), new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT), new Pattern(DyeColor.WHITE, PatternType.BORDER))))
		.withReward(rewards()
			.withTrophy(() -> {
				ItemStack item = shield(DyeColor.WHITE);
				item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
					new Pattern(DyeColor.BLACK, PatternType.HALF_VERTICAL),
					new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM)
				)));
				return item;
			}, true)
			.withExtraAction(action -> {
				Player completer = action.completer();
				if (completer == null) return;

				Location loc = completer.getLocation();
				loc.getWorld().spawn(loc, ExperienceOrb.class, orb -> orb.setExperience(35));

				ItemStack item = AdvancementTrophies.produceTrophy(requireNonNull(WEAPONRY_TAB.getAdvancement(NamespacedKey.fromString(WEAPONRY_TAB.getKey() + "/die_twice_within_5s"))), completer);
				if (item != null)
					loc.getWorld().dropItemNaturally(loc, item, drop -> drop.setInvulnerable(true));
			})
			.withExtraMessage(p -> FancyAdvancementReward.getExpMessage(p, 35))
		)
		.buildAndRegister();
	public static final IAdvancement HALF_A_HEART_1M = buildBase(ROCK_PAPER_SHEARS, "half_a_heart_1m").display(display().x(1F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.NETHER_WART))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement HALF_A_HEART_1H = buildBase(HALF_A_HEART_1M, "half_a_heart_1h").display(display().x(1F).challengeFrame().torture().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.NETHER_WART_BLOCK))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.BLACK_DYE))
		)
		.buildAndRegister();
	public static final IAdvancement HALF_A_HEART_6H = buildBase(HALF_A_HEART_1H, "half_a_heart_6h").display(display().x(1F).withAdvancementFrame(AdvancementFrame.BUTTERFLY).superTorture().fancyDescriptionParent(NamedTextColor.DARK_RED).icon(potionItem(Material.POTION, PotionType.REGENERATION)))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.COMPOSTER))
		)
		.buildAndRegister();
	public static final IAdvancement GET_A_SHIELD = buildBase(WEAPONRY_ROOT, "get_a_shield").display(display().xy(1F, 1.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SHIELD))
		.requiredProgress(vanilla(inventoryChanged().withItems(ItemTriggerCondition.of(Material.SHIELD))))
		.buildAndRegister();
	public static final IAdvancement BLOCK_WITH_SHIELD = buildBase(GET_A_SHIELD, "block_with_shield").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.ROTTEN_FLESH))
		.requiredProgress(vanilla(entityHurtPlayer().withDamage(DamageTriggerCondition::blocked)))
		.buildAndRegister();
	public static final IAdvancement GET_A_BANNER_SHIELD = buildBase(BLOCK_WITH_SHIELD, "get_a_banner_shield").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(shield(DyeColor.RED)))
		.withReward(rewards().addItems(ItemStack.of(Material.WHITE_WOOL, 6)))
		.buildAndRegister(GetABannerShieldAdvancement::new);
	public static final IAdvancement MORE_SHIELDS = buildBase(GET_A_BANNER_SHIELD, "more_shields").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(() -> {
			ItemStack item = shield(DyeColor.WHITE);
			item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
				new Pattern(DyeColor.LIGHT_GRAY, PatternType.MOJANG),
				new Pattern(DyeColor.LIGHT_BLUE, PatternType.GRADIENT_UP),
				new Pattern(DyeColor.BLACK, PatternType.GRADIENT),
				new Pattern(DyeColor.BLACK, PatternType.TRIANGLE_BOTTOM),
				new Pattern(DyeColor.WHITE, PatternType.CIRCLE),
				new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_LEFT)
			)));
			return item;
		})).buildAndRegister();
	public static final IAdvancement MASTER_SHIELDSMAN = buildBase(MORE_SHIELDS, "master_shieldsman").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(shield(DyeColor.BLACK)))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(() -> {
				ItemStack item = shield(DyeColor.BLUE);
				item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
					new Pattern(DyeColor.RED, PatternType.FLOWER),
					new Pattern(DyeColor.BLUE, PatternType.HALF_HORIZONTAL),
					new Pattern(DyeColor.YELLOW, PatternType.TRIANGLE_TOP),
					new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP),
					new Pattern(DyeColor.YELLOW, PatternType.TRIANGLES_BOTTOM),
					new Pattern(DyeColor.BLACK, PatternType.BORDER),
					new Pattern(DyeColor.LIGHT_GRAY, PatternType.CURLY_BORDER)
				)));
				item.addUnsafeEnchantment(Enchantment.UNBREAKING, 10);
				item.addUnsafeEnchantment(Enchantment.THORNS, 1);
				return item;
			})
		)
		.buildAndRegister(MasterShieldsmanAdvancement::new);
	public static final IAdvancement DEFLECT_40 = buildBase(MASTER_SHIELDSMAN, "deflect_40").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(() -> {
			ItemStack item = shield(DyeColor.BLACK);
			item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
				new Pattern(DyeColor.GRAY, PatternType.CURLY_BORDER),
				new Pattern(DyeColor.GRAY, PatternType.TRIANGLE_BOTTOM),
				new Pattern(DyeColor.GRAY, PatternType.TRIANGLE_TOP),
				new Pattern(DyeColor.GRAY, PatternType.BRICKS)
			)));
			return item;
		}))
		.requiredProgress(vanilla(entityHurtPlayer().withDamage(damage -> damage.blocked().withMinDealtDamage(40D))))
		.buildAndRegister();
	public static final IAdvancement ATTACK_WITH_AN_EGG = buildBase(WEAPONRY_ROOT, "attack_with_an_egg").display(display().xy(1F, -1.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.EGG))
		.buildAndRegister(AttackWithAnEggAdvancement::new);
	public static final IAdvancement ATTACK_ZOMBIE_WITH_AN_EGG = buildBase(ATTACK_WITH_AN_EGG, "attack_zombie_with_an_egg").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.ZOMBIE_HEAD))
		.buildAndRegister(AttackZombieWithAnEggAdvancement::new);
	public static final IAdvancement ATTACK_SQUID_IN_THE_AIR_WITH_A_SNOWBALL = buildBase(ATTACK_ZOMBIE_WITH_AN_EGG, "attack_squid_in_the_air_with_a_snowball").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.SNOWBALL))
		.withReward(rewards().withExp(70))
		.buildAndRegister(AttackSquidInTheAirWithASnowballAdvancement::new);
	public static final IAdvancement HOOK_PIG = buildBase(WEAPONRY_ROOT, "hook_pig").display(display().xy(1F, -2.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PORKCHOP))
		.requiredProgress(vanilla(fishingRodHooked().withEntity(entity -> entity.withEntityType(EntityType.PIG))))
		.buildAndRegister();
	public static final IAdvancement HOOK_MONSTER = buildBase(HOOK_PIG, "hook_monster").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.FISHING_ROD))
		.requiredProgress(vanilla(fishingRodHooked().withEntity(entity -> entity.withEntityType(UtilizerTags.HOSTILE_MONSTERS))))
		.buildAndRegister();
	public static final IAdvancement WHEN_PIGS_FINALLY_FLY = buildBase(HOOK_MONSTER, "when_pigs_finally_fly").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(PIG_HEAD))
		.withReward(rewards()
			.withExp(100)
			.addItems(ItemStack.of(Material.PORKCHOP, 32))
			.withTrophy(ItemStack.of(Material.PORKCHOP))
		)
		.requiredProgress(vanilla(fishingRodHooked().withEntity(entity -> entity.withEntityType(EntityType.PIG).withDistanceToPlayer(distance -> distance.minAbsolute(25D)))))
		.buildAndRegister();

	public static final AdvancementTab CHALLENGES_TAB = buildTab("challenges", MANAGER).inverseY().backgroundPathBlock(Material.BEDROCK).build();
	public static final IRootAdvancement CHALLENGES_ROOT = buildRoot(CHALLENGES_TAB).display(display().xy(0F, 0F).withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.ENDER_EYE))
		.visibilityRule(ifDone(false, BRAVE_NEW_WORLD))
		.requiredProgress(alwaysDone())
		.buildAndRegister();
	public static final IAdvancement DEFLECT_200 = buildBase(CHALLENGES_ROOT, "deflect_200").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().superChallenge().icon(ItemUtil.glint(Material.SKULL_BANNER_PATTERN)))
		.requiredProgress(vanilla(entityHurtPlayer().withDamage(damage -> damage.blocked().withMinDealtDamage(200D))))
		.withReward(rewards()
			.withExp(250)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.SHIELD);
				item.setData(DataComponentTypes.UNBREAKABLE);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement DEFLECT_SHIELD = buildBase(DEFLECT_200, "deflect_shield").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().superChallenge().icon(() -> {
			var item = ItemStack.of(Material.SHIELD);
			item.setData(DataComponentTypes.BASE_COLOR, DyeColor.RED);
			item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(
				new Pattern(DyeColor.BLACK, PatternType.GRADIENT_UP),
				new Pattern(DyeColor.BLACK, PatternType.MOJANG)
			)));
			return item;
		}))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.SHIELD);
				item.setData(DataComponentTypes.UNBREAKABLE);
				item.setData(DataComponentTypes.ITEM_MODEL, Material.OAK_PLANKS.key());
				return item;
			})
		)
		.requiredProgress(vanilla(entityHurtPlayer().withDamage(damage -> damage.blocked().withMinDealtDamage((double) Material.SHIELD.getMaxDurability()))))
		.buildAndRegister();

	public static final AdvancementTab STATISTICS_TAB = buildTab("statistics", MANAGER).inverseY().backgroundPathTexture("block/loom_side").build();
	public static final IRootAdvancement STATISTICS_ROOT = buildRoot(STATISTICS_TAB).display(display().xy(0F, 0F).withAdvancementFrame(AdvancementFrame.SQUIRCLE).fancyDescriptionParent(GRAY).icon(Material.WRITABLE_BOOK)).visibilityRule(ifDone(false, BRAVE_NEW_WORLD)).requiredProgress(alwaysDone()).buildAndRegister();
	public static final IAdvancement STATISTICS_RIGHT_LINKER = buildFake(STATISTICS_ROOT).display(display().x(0.75F).isHidden(true)).buildAndRegister();
	public static final IAdvancement STATISTICS_UP_LINKER = buildFake(STATISTICS_ROOT).display(display().x(-0.5F).isHidden(true)).buildAndRegister();
	// Up
	public static final IAdvancement PLAY_1D = buildBase(STATISTICS_UP_LINKER, "play_1d").display(display().xy(0.5F, 1.25F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SUNFLOWER))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement PLAY_100D = buildBase(STATISTICS_UP_LINKER, "play_100d").display(display().xy(0.5F, 2.25F).goalFrame().fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.CLOCK))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement PLAY_365D = buildBase(STATISTICS_UP_LINKER, "play_365d").display(display().xy(0.5F, 3.25F).challengeFrame().fancyDescriptionParent(NamedTextColor.DARK_PURPLE).icon(Material.FIREWORK_ROCKET))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.FIREWORK_ROCKET, 16);
				item.setData(DataComponentTypes.FIREWORKS, Fireworks.fireworks()
						.flightDuration(3)
						.addEffect(FireworkEffect.builder()
							.with(FireworkEffect.Type.STAR)
							.withFlicker()
							.withTrail()
							.withColor(Color.fromRGB(12134697), Color.fromRGB(16777215))
							.withFade(Color.fromRGB(16777215))
							.build()
						)
						.build()
					);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement PLAY_1000D = buildBase(STATISTICS_UP_LINKER, "play_1000d").display(display().xy(0.5F, 4.25F).challengeFrame().fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).icon(HAPPY_1000_DAYS_HEAD))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4OTQ2YmNkM2IwYTE2ZmJmZjM1ZDE2MTRmYmJiZmYzYzE5NzI2NjNjNTUzMjE1NGZlYTFjYTJhNGMxOWIxZSJ9fX0="))
		)
		.buildAndRegister();
	// Right top
	public static final IAdvancement BELL_100 = buildBase(STATISTICS_RIGHT_LINKER, "bell_100").display(display().x(1).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.STICK)).buildAndRegister();
	public static final IAdvancement BELL_1K = buildBase(BELL_100, "bell_1k").display(display().x(1).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.MUSIC_DISC_13)).buildAndRegister();
	public static final IAdvancement BELL_10K = buildBase(BELL_1K, "bell_10k").display(display().x(1).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.BELL)).buildAndRegister();
	public static final IAdvancement BELL_100K = buildBase(BELL_10K, "bell_100k").display(display().x(1).fancyDescriptionParent(NamedTextColor.DARK_RED).torture().challengeFrame().icon(Material.REDSTONE)).buildAndRegister();
	public static final IAdvancement BELL_1000K = buildBase(BELL_100K, "bell_1000k").display(display().x(1).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.SKELETON_SKULL)).buildAndRegister();
	public static final IAdvancement BELL_10000K = buildBase(BELL_1000K, "bell_10000k").display(display().x(1).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.BELL)))
		.withReward(rewards()
			.withTrophy(ItemStack.of(Material.BELL))
		)
		.buildAndRegister();
	public static final IAdvancement LEVEL_30 = buildBase(STATISTICS_RIGHT_LINKER, "level_30").display(display().xy(1F, 1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemUtil.glint(Material.EXPERIENCE_BOTTLE)))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.LAPIS_LAZULI, 12)))
		.buildAndRegister();
	public static final IAdvancement LEVEL_100 = buildBase(LEVEL_30, "level_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.glint(Material.ENCHANTED_BOOK)))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.LAPIS_LAZULI, 64)))
		.buildAndRegister();
	public static final IAdvancement LEVEL_300 = buildBase(LEVEL_100, "level_300").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.LAPIS_BLOCK)))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.LAPIS_BLOCK, 20))
			.withTrophy(ItemStack.of(Material.SMOKER))
		)
		.buildAndRegister();
	public static final IAdvancement LEVEL_1000 = buildBase(LEVEL_300, "level_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.SCULK)))
		.withReward(rewards()
			.withExp(1000)
			.withTrophy(ItemStack.of(Material.MAGMA_CREAM))
		)
		.buildAndRegister();
	public static final IAdvancement LEVEL_2500 = buildBase(LEVEL_1000, "level_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().superTorture().icon(ItemUtil.glint(Material.ENCHANTING_TABLE))).buildAndRegister();
	public static final IAdvancement LEVEL_5000 = buildBase(LEVEL_2500, "level_5000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.DAMAGED_ANVIL))).buildAndRegister();
	public static final IAdvancement ENCHANT_10 = buildBase(STATISTICS_RIGHT_LINKER, "enchant_10").display(display().xy(1F, 2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemUtil.glint(Material.IRON_SWORD)))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.LAPIS_LAZULI, 12)))
		.buildAndRegister();
	public static final IAdvancement ENCHANT_50 = buildBase(ENCHANT_10, "enchant_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.glint(Material.DIAMOND_AXE)))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.LAPIS_LAZULI, 64)))
		.buildAndRegister();
	public static final IAdvancement ENCHANT_250 = buildBase(ENCHANT_50, "enchant_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.TRIDENT)))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.LAPIS_BLOCK, 20))
			.withTrophy(ItemStack.of(Material.ENCHANTED_BOOK))
		)
		.buildAndRegister();
	public static final IAdvancement ENCHANT_1000 = buildBase(ENCHANT_250, "enchant_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(ItemUtil.glint(Material.NETHERITE_PICKAXE))).buildAndRegister();
	public static final IAdvancement ENCHANT_2500 = buildBase(ENCHANT_1000, "enchant_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().torture().icon(ItemUtil.glint(Material.BOW))).buildAndRegister();
	public static final IAdvancement ENCHANT_5K = buildBase(ENCHANT_2500, "enchant_5k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(ItemUtil.glint(Material.BRUSH)))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemStack.of(Material.ENCHANTING_TABLE))
		)
		.buildAndRegister();
	public static final IAdvancement ENCHANT_10K = buildBase(ENCHANT_5K, "enchant_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.ENCHANTED_BOOK))).buildAndRegister();
	public static final IAdvancement WASH_10 = buildBase(STATISTICS_RIGHT_LINKER, "wash_10").display(display().xy(1F, 3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.WHITE_BANNER)).requiredProgress(simple(10, 1)).buildAndRegister();
	public static final IAdvancement WASH_50 = buildBase(WASH_10, "wash_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemStack.of(Material.LEATHER_BOOTS).withColor(NamedTextColor.LIGHT_PURPLE))).requiredProgress(simple(50, 1)).buildAndRegister();
	public static final IAdvancement WASH_250 = buildBase(WASH_50, "wash_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.CAULDRON)).requiredProgress(simple(250, 1)).buildAndRegister();
	public static final IAdvancement WASH_1K = buildBase(WASH_250, "wash_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().torture().icon(ItemUtil.glint(Material.CAULDRON))).requiredProgress(simple(1000, 1)).buildAndRegister();
	public static final IAdvancement WASH_5K = buildBase(WASH_1K, "wash_5k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.SHULKER_BOX)).requiredProgress(simple(5000, 1)).buildAndRegister();
	public static final IAdvancement WASH_10K = buildBase(WASH_5K, "wash_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.WATER_BUCKET)).requiredProgress(simple(10000, 1)).buildAndRegister();
	public static final IAdvancement FISH_5 = buildBase(STATISTICS_RIGHT_LINKER, "fish_5").display(display().xy(1F, 4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.FISHING_ROD))
		.withReward(rewards().withExp(5))
		.buildAndRegister();
	public static final IAdvancement FISH_25 = buildBase(FISH_5, "fish_25").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.INK_SAC))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.SALMON, 5)))
		.buildAndRegister();
	public static final IAdvancement FISH_100 = buildBase(FISH_25, "fish_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.COD)).buildAndRegister();
	public static final IAdvancement FISH_250 = buildBase(FISH_100, "fish_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).goalFrame().icon(Material.SALMON))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(ItemStack.of(Material.SALMON_BUCKET))
		)
		.buildAndRegister();
	public static final IAdvancement FISH_500 = buildBase(FISH_250, "fish_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemUtil.glint(Material.COD))).buildAndRegister();
	public static final IAdvancement FISH_1K = buildBase(FISH_500, "fish_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).challengeFrame().icon(ItemUtil.glint(Material.SALMON)))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.FISHING_ROD);
				item.addEnchantment(Enchantment.MENDING, 1);
				item.addEnchantment(Enchantment.LUCK_OF_THE_SEA, 3);
				item.addEnchantment(Enchantment.LURE, 3);
				item.addEnchantment(Enchantment.UNBREAKING, 3);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement FISH_2500 = buildBase(FISH_1K, "fish_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().superChallenge().icon(Material.TROPICAL_FISH))
		.withReward(rewards()
			.withTrophy(() -> {
				var item = ItemStack.of(Material.FISHING_ROD);
				item.setData(DataComponentTypes.UNBREAKABLE);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement FISH_5K = buildBase(FISH_2500, "fish_5k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().torture().icon(Material.PUFFERFISH)).buildAndRegister();
	public static final IAdvancement FISH_10K = buildBase(FISH_5K, "fish_10k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.NAUTILUS_SHELL)).buildAndRegister();
	public static final IAdvancement FISH_50K = buildBase(FISH_10K, "fish_50k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.FISHING_ROD))).buildAndRegister();
	public static final IAdvancement EAT_200 = buildBase(STATISTICS_RIGHT_LINKER, "eat_200").display(display().xy(1F, 5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.BREAD)).requiredProgress(simple(200, 1))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement EAT_1K = buildBase(EAT_200, "eat_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.COOKED_PORKCHOP)).requiredProgress(simple(1000, 1))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement EAT_2500 = buildBase(EAT_1K, "eat_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.COOKED_BEEF)).requiredProgress(simple(2500, 1))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemStack.of(Material.COOKED_BEEF))
		)
		.buildAndRegister();
	public static final IAdvancement EAT_5K = buildBase(EAT_2500, "eat_5k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.GOLDEN_CARROT)).requiredProgress(simple(5000, 1)).buildAndRegister();
	public static final IAdvancement EAT_10K = buildBase(EAT_5K, "eat_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().torture().icon(Material.GOLDEN_APPLE)).requiredProgress(simple(10000, 1))
		.withReward(rewards()
			.withExp(550)
			.withTrophy(ItemStack.of(Material.DRIED_KELP))
		)
		.buildAndRegister();
	public static final IAdvancement EAT_25K = buildBase(EAT_10K, "eat_25k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.COOKIE)).requiredProgress(simple(25000, 1)).buildAndRegister();
	public static final IAdvancement EAT_50K = buildBase(EAT_25K, "eat_50k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.CAKE)).requiredProgress(simple(50000, 1)).buildAndRegister();
	public static final IAdvancement TOTEM_5 = buildBase(STATISTICS_RIGHT_LINKER, "totem_5").display(display().xy(1F, 6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.GOLD_NUGGET))
		.withReward(rewards().withExp(15).addItems(ItemStack.of(Material.EMERALD, 4)))
		.buildAndRegister();
	public static final IAdvancement TOTEM_10 = buildBase(TOTEM_5, "totem_10").display(display().x(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.TOTEM_OF_UNDYING))
		.withReward(rewards().withExp(25).addItems(ItemStack.of(Material.TOTEM_OF_UNDYING)))
		.buildAndRegister();
	public static final IAdvancement TOTEM_25 = buildBase(TOTEM_10, "totem_25").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.RAW_GOLD))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement TOTEM_50 = buildBase(TOTEM_25, "totem_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.PLAYER_HEAD))
		.withReward(rewards().withExp(75))
		.buildAndRegister();
	public static final IAdvancement TOTEM_100 = buildBase(TOTEM_50, "totem_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.ZOMBIE_HEAD))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(ItemStack.of(Material.TOTEM_OF_UNDYING))
		)
		.buildAndRegister();
	public static final IAdvancement TOTEM_250 = buildBase(TOTEM_100, "totem_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.SKELETON_SKULL))
		.withReward(rewards().withExp(150))
		.buildAndRegister();
	public static final IAdvancement TOTEM_500 = buildBase(TOTEM_250, "totem_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.WITHER_SKELETON_SKULL))
			.withReward(rewards().withExp(250))
			.buildAndRegister();
	public static final IAdvancement TOTEM_1000 = buildBase(TOTEM_500, "totem_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.EVOKER_SPAWN_EGG))
		.withReward(rewards()
			.withExp(350)
			.withTrophy(ItemStack.of(Material.TOTEM_OF_UNDYING))
		)
		.buildAndRegister();
	public static final IAdvancement TOTEM_2500 = buildBase(TOTEM_1000, "totem_2500").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.END_CRYSTAL))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDI2ODg1N2JmYzJmMWI5YjA2MzZlMTVlNGYwN2Q3MWM5YmIyNjhjYjc5YzNkNDdmNTU3OTk2MzBkNmFiMDgzMCJ9fX0=")) // todo textured heads
		)
		.buildAndRegister();
	public static final IAdvancement TOTEM_5000 = buildBase(TOTEM_2500, "totem_5000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.TOTEM_OF_UNDYING)))
		.withReward(rewards().withExp(1000))
		.buildAndRegister();
	public static final IAdvancement RAID_5 = buildBase(STATISTICS_RIGHT_LINKER, "raid_5").display(display().xy(1F, 7F).fancyDescriptionParent(NamedTextColor.GREEN).challengeFrame().icon(Material.CROSSBOW))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.EMERALD, 4)))
		.buildAndRegister();
	public static final IAdvancement RAID_20 = buildBase(RAID_5, "raid_20").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.IRON_AXE))
		.withReward(rewards().withExp(70).addItems(ItemStack.of(Material.EMERALD, 16)))
		.buildAndRegister();
	public static final IAdvancement RAID_100 = buildBase(RAID_20, "raid_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.SADDLE)))
		.withReward(rewards()
			.withExp(120)
			.addItems(ItemStack.of(Material.TOTEM_OF_UNDYING))
			.withTrophy(() -> {
				var item = ItemStack.of(Material.IRON_AXE);
				item.addEnchantment(Enchantment.SHARPNESS, 5);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement RAID_2000 = buildBase(RAID_100, "raid_2000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(ItemUtil.glint(Material.TOTEM_OF_UNDYING)))
		.withReward(rewards()
			.withExp(250)
			.addItems(ItemStack.of(Material.EMERALD_BLOCK, 6))
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWM3M2UxNmZhMjkyNjg5OWNmMTg0MzQzNjBlMjE0NGY4NGVmMWViOTgxZjk5NjE0ODkxMjE0OGRkODdlMGIyYSJ9fX0=")) // toto textured heads
		)
		.buildAndRegister();
	// Right bottom
	public static final IAdvancement BREED_100 = buildBase(STATISTICS_RIGHT_LINKER, "breed_100").display(display().xy(1F, -1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PORKCHOP))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.WHEAT, 32)))
		.buildAndRegister();
	public static final IAdvancement BREED_500 = buildBase(BREED_100, "breed_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BEEF))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.HAY_BLOCK, 16), ItemStack.of(Material.GOLDEN_APPLE, 8)))
		.buildAndRegister();
	public static final IAdvancement BREED_2500 = buildBase(BREED_500, "breed_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.CHICKEN))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.HAY_BLOCK, 64), ItemStack.of(Material.ENCHANTED_GOLDEN_APPLE, 8))
			.withTrophy(ItemStack.of(Material.OAK_BOAT))
		)
		.buildAndRegister();
	public static final IAdvancement BREED_5K = buildBase(BREED_2500, "breed_5k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.MUTTON)).buildAndRegister();
	public static final IAdvancement BREED_10K = buildBase(BREED_5K, "breed_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().torture().icon(Material.RABBIT))
		.withReward(rewards()
			.withExp(550)
			.withTrophy(MR_SHEEP_HEAD) // todo textured heads
		).buildAndRegister();
	public static final IAdvancement BREED_15K = buildBase(BREED_10K, "breed_15k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.HONEY_BOTTLE)).buildAndRegister();
	public static final IAdvancement BREED_25K = buildBase(BREED_15K, "breed_25k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.EGG))).buildAndRegister();
	public static final IAdvancement KILL_250 = buildBase(STATISTICS_RIGHT_LINKER, "kill_250").display(display().xy(1F, -2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.STONE_SWORD))
		.withReward(rewards()
			.withExp(50)
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.SHARPNESS, 2).build());
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement KILL_2500 = buildBase(KILL_250, "kill_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.IRON_SWORD ))
		.withReward(rewards()
			.withExp(200)
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.SHARPNESS, 4).build());
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement KILL_25K = buildBase(KILL_2500, "kill_25k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DIAMOND_SWORD))
		.withReward(rewards()
			.withExp(500)
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.SHARPNESS, 5).build());
				return item;
			})
			.addItems(() -> {
				var item = ItemStack.of(Material.ENCHANTED_BOOK);
				item.setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments().add(Enchantment.MENDING, 1).build());
				return item;
			})
			.withTrophy(ItemStack.of(Material.DANDELION))
		)
		.buildAndRegister();
	public static final IAdvancement KILL_50K = buildBase(KILL_25K, "kill_50k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.GOLDEN_SWORD)).buildAndRegister();
	public static final IAdvancement KILL_100K = buildBase(KILL_50K, "kill_100k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().torture().icon(Material.NETHERITE_SWORD)).buildAndRegister();
	public static final IAdvancement KILL_250K = buildBase(KILL_100K, "kill_250k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.WOODEN_SWORD)).buildAndRegister();
	public static final IAdvancement KILL_500K = buildBase(KILL_250K, "kill_500k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.NETHERITE_SWORD)))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDA3MTU0OTFiZjJiODVjMjc2ZmZiNzAxN2JhY2I0NDNjNzg5YjBlNWQ0NjVmYzMzYjVmOTkzYmQzYjQyMGZiZCJ9fX0=")) // todo textured heads
		)
		.buildAndRegister();
	public static final IAdvancement TRADE_100 = buildBase(STATISTICS_RIGHT_LINKER, "trade_100").display(display().xy(1F, -3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.EMERALD))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.EMERALD, 8)))
		.buildAndRegister();
	public static final IAdvancement TRADE_500 = buildBase(TRADE_100, "trade_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.EMERALD_BLOCK))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.EMERALD, 24)))
		.buildAndRegister();
	public static final IAdvancement TRADE_2500 = buildBase(TRADE_500, "trade_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.EGG))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.EMERALD, 64))
			.withTrophy(ItemStack.of(Material.MAGENTA_GLAZED_TERRACOTTA))
		)
		.buildAndRegister();
	public static final IAdvancement TRADE_10K = buildBase(TRADE_2500, "trade_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.PUMPKIN))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(STONKS_HEAD)
		)
		.buildAndRegister();
	public static final IAdvancement TRADE_25K = buildBase(TRADE_10K, "trade_25k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.RED).challengeFrame().torture().icon(Material.MELON)).buildAndRegister();
	public static final IAdvancement TRADE_50K = buildBase(TRADE_25K, "trade_50k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.GLASS)).buildAndRegister();
	public static final IAdvancement TRADE_250K = buildBase(TRADE_50K, "trade_250k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(TRADER_HEAD)).buildAndRegister();
	public static final IAdvancement LOOT_10 = buildBase(STATISTICS_RIGHT_LINKER, "loot_10").display(display().xy(1F, -4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CHEST)).requiredProgress(simple(10, 1))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement LOOT_100 = buildBase(LOOT_10, "loot_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).icon(Material.BARREL)).requiredProgress(simple(100, 1))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement LOOT_500 = buildBase(LOOT_100, "loot_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).goalFrame().icon(Material.ENDER_CHEST)).requiredProgress(simple(500, 1))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemStack.of(Material.TRAPPED_CHEST))
		)
		.buildAndRegister();
	public static final IAdvancement LOOT_1000 = buildBase(LOOT_500, "loot_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.ENCHANTED_GOLDEN_APPLE)).requiredProgress(simple(1000, 1)).buildAndRegister();
	public static final IAdvancement LOOT_2500 = buildBase(LOOT_1000, "loot_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(ItemUtil.glint(Material.BARREL))).requiredProgress(simple(2500, 1))
		.withReward(rewards()
			.withExp(550)
			.withTrophy(ItemStack.of(Material.CHEST))
		)
		.buildAndRegister();
	public static final IAdvancement LOOT_5K = buildBase(LOOT_2500, "loot_5k").display(display().x(1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(ItemUtil.glint(Material.ENDER_CHEST))).requiredProgress(simple(5000, 1)).buildAndRegister();
	public static final IAdvancement LOOT_10K = buildBase(LOOT_5K, "loot_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.FLINT_AND_STEEL)).requiredProgress(simple(10000, 1)).buildAndRegister();
	public static final IAdvancement OPEN_CHEST_100 = buildBase(STATISTICS_RIGHT_LINKER, "open_chest_100").display(display().xy(1F, -5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CHEST))
		.withReward(rewards().withExp(15))
		.buildAndRegister();
	public static final IAdvancement OPEN_CHEST_1K = buildBase(OPEN_CHEST_100, "open_chest_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.CHEST_MINECART))
		.withReward(rewards().withExp(30))
		.buildAndRegister();
	public static final IAdvancement OPEN_CHEST_10K = buildBase(OPEN_CHEST_1K, "open_chest_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(CHEST_HEAD))
		.withReward(rewards()
			.withExp(80)
			.withTrophy(ItemStack.of(Material.CHEST))
		)
		.buildAndRegister();
	public static final IAdvancement OPEN_CHEST_25K = buildBase(OPEN_CHEST_10K, "open_chest_25k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(CHRISTMAS_CHEST_HEAD))
		.withReward(rewards()
			.withExp(150)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZkZjQ3YzMwYjFlM2RiNTJlNDFmNWVlYjgwNmM2OWZlZjgwNTk1NTBlOGY1N2IwYTgzYjIyNjBhNjZkOTI3ZSJ9fX0=")) // todo textured heads
		)
		.buildAndRegister();
	public static final IAdvancement OPEN_SHULKER_100 = buildBase(OPEN_CHEST_25K, "open_shulker_100").display(display().x(1.5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.WHITE_SHULKER_BOX))
		.withReward(rewards().withExp(25))
		.buildAndRegister();
	public static final IAdvancement OPEN_SHULKER_1K = buildBase(OPEN_SHULKER_100, "open_shulker_1k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.LIGHT_GRAY_SHULKER_BOX))
		.withReward(rewards().withExp(30))
		.buildAndRegister();
	public static final IAdvancement OPEN_SHULKER_10K = buildBase(OPEN_SHULKER_1K, "open_shulker_10k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.GRAY_SHULKER_BOX))
		.withReward(rewards()
			.withExp(100)
			.withTrophy(ItemStack.of(Material.PINK_SHULKER_BOX))
		)
		.buildAndRegister();
	public static final IAdvancement OPEN_SHULKER_100K = buildBase(OPEN_SHULKER_10K, "open_shulker_100k").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(Material.BLACK_SHULKER_BOX))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjA4OGNjODJhNTk3NGYxMWYxYjg4ZGRjMTE0YjI2MjE2MWE0ZmJjMDkwZDIxMzQ0OTAzNTlhMjFlNDEyYSJ9fX0=")) // todo textured heads
		)
		.buildAndRegister();
	public static final IAdvancement OPEN_CRAFTING_TABLE_15 = buildBase(STATISTICS_RIGHT_LINKER, "open_crafting_table_15").display(display().xy(1F, -6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_LOG))
		.withReward(rewards().withExp(5))
		.buildAndRegister();
	public static final IAdvancement OPEN_CRAFTING_TABLE_100 = buildBase(OPEN_CRAFTING_TABLE_15, "open_crafting_table_100").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.OAK_PLANKS))
		.withReward(rewards().withExp(15))
		.buildAndRegister();
	public static final IAdvancement OPEN_CRAFTING_TABLE_500 = buildBase(OPEN_CRAFTING_TABLE_100, "open_crafting_table_500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemUtil.glint(Material.CRAFTING_TABLE)))
		.withReward(rewards()
			.withExp(50)
			.withTrophy(ItemStack.of(Material.CRAFTING_TABLE))
		)
		.buildAndRegister();
	public static final IAdvancement OPEN_CRAFTING_TABLE_2500 = buildBase(OPEN_CRAFTING_TABLE_500, "open_crafting_table_2500").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(CRAFTING_TABLE_HEAD))
		.withReward(rewards()
			.withExp(130)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmYyNTc3MjY5MzdiYTE4MTQwOTYyMDllYjdiNTE2OGM2NjU1MmQyNWU0MWIxZTUxNGFhNmQzMWM0ZDNhYTZkYyJ9fX0="))
		)
		.buildAndRegister();
	public static final IAdvancement BREAK_100_IRON = buildBase(STATISTICS_RIGHT_LINKER, "break_100_iron").display(display().xy(1F, -7F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.IRON_PICKAXE))
		.withReward(rewards().withExp(20).addItems(ItemStack.of(Material.COBBLESTONE, 16)))
		.buildAndRegister();
	public static final IAdvancement BREAK_2500_DIAMOND = buildBase(BREAK_100_IRON, "break_2500_diamond").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.DIAMOND_PICKAXE))
		.withReward(rewards().withExp(35).addItems(ItemStack.of(Material.IRON_INGOT, 16)))
		.buildAndRegister();
	public static final IAdvancement BREAK_10K_NETHERITE = buildBase(BREAK_2500_DIAMOND, "break_10k_netherite").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.NETHERITE_PICKAXE))
		.withReward(rewards()
			.withExp(150)
			.addItems(ItemStack.of(Material.DIAMOND, 5))
			.withTrophy(ItemStack.of(Material.DIAMOND_ORE))
		)
		.buildAndRegister();
	public static final IAdvancement BREAK_100K_NETHERITE = buildBase(BREAK_10K_NETHERITE, "break_100k_netherite").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(ItemUtil.glint(Material.NETHERITE_PICKAXE)))
		.withReward(rewards()
			.withExp(400)
			.addItems(ItemStack.of(Material.DIAMOND_ORE, 5))
			.withTrophy(() -> {
				var item = ItemStack.of(Material.GOLDEN_PICKAXE);
				item.addUnsafeEnchantment(Enchantment.EFFICIENCY, 6);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement SURVIVE_1H = buildBase(STATISTICS_RIGHT_LINKER, "survive_1h").display(display().xy(1F, -8F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.CLOCK))
		.withReward(rewards().withExp(30))
		.buildAndRegister();
	public static final IAdvancement SURVIVE_10H = buildBase(SURVIVE_1H, "survive_10h").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(SURVIVE_HEAD_1))
		.withReward(rewards().withExp(70))
		.buildAndRegister();
	public static final IAdvancement SURVIVE_50H = buildBase(SURVIVE_10H, "survive_50h").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(SURVIVE_HEAD_2))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(ItemStack.of(Material.CLOCK))
		)
		.buildAndRegister();
	public static final IAdvancement SURVIVE_200H = buildBase(SURVIVE_50H, "survive_200h").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).superChallenge().icon(SURVIVE_HEAD_3))
		.withReward(rewards()
			.withExp(1000)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmUwOTk4NTgxMDg3ZWYyY2M3ZTA1OGQ3ZGZjZTIxOTZiY2FjZDk2ZmY0NjhlNjQ4MjM3YWE4OWQ4ZWI2NmUzMCJ9fX0=")) // TODO textured heads
		)
		.buildAndRegister();
	public static final IAdvancement DEATHS_1 = buildBase(SURVIVE_200H, "deaths_1").display(display().x(1.5F).fancyDescriptionParent(NamedTextColor.DARK_RED).icon(RequiemItems.HEART)).buildAndRegister();
	public static final IAdvancement DEATHS_50 = buildBase(DEATHS_1, "deaths_50").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BONE)).buildAndRegister();
	public static final IAdvancement DEATHS_250 = buildBase(DEATHS_50, "deaths_250").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(RequiemItems.SPINE)).buildAndRegister();
	public static final IAdvancement DEATHS_1000 = buildBase(DEATHS_250, "deaths_1000").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(Material.SKELETON_SKULL)).buildAndRegister();
//	public static final IAdvancement HORSE_SPEED_1 = buildBase(STATISTICS_RIGHT_LINKER, "horse_speed_1").display(display().xy(1F, -9F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemStack.of(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16707753))))
//		.withReward(rewards().withExp(10))
//		.buildAndRegister();
//	public static final IAdvancement HORSE_SPEED_2 = buildBase(HORSE_SPEED_1, "horse_speed_2").display(display().x(1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemStack.of(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16495988))))
//		.withReward(rewards().withExp(50))
//		.buildAndRegister();
//	public static final IAdvancement HORSE_SPEED_3 = buildBase(HORSE_SPEED_2, "horse_speed_3").display(display().x(1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(ItemStack.of(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16481652))))
//		.withReward(rewards()
//			.withExp(75)
//			.withTrophy(ItemStack.of(Material.SADDLE))
//		)
//		.buildAndRegister();
//	public static final IAdvancement HORSE_SPEED_4 = buildBase(HORSE_SPEED_3, "horse_speed_4").display(display().x(1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(ItemStack.of(Material.LEATHER_HORSE_ARMOR).withColor(TextColor.color(16352992))))
//		.withReward(rewards()
//			.withExp(300)
//			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzBjNDBjOWQ0NGQ2NjA5YmNkOGFmMjgyOTcyMThhY2U3ZWVhMmU0NTFjNDNkNzZiYWFmOTJjYmVmMGEwZGNhZSJ9fX0="))
//		)
//		.buildAndRegister();

	// Left top
	public static final IAdvancement WALK_10KM = buildBase(STATISTICS_ROOT, "walk_10km").display(display().x(-1.75F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.LEATHER_BOOTS))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement WALK_50KM = buildBase(WALK_10KM, "walk_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.CHAINMAIL_BOOTS))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement WALK_250KM = buildBase(WALK_50KM, "walk_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.IRON_BOOTS))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.LEATHER_BOOTS);
				item.setData(DataComponentTypes.UNBREAKABLE);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement WALK_1000KM = buildBase(WALK_250KM, "walk_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().torture().icon(Material.GOLDEN_BOOTS))
		.withReward(rewards()
			.withExp(1000)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.CLOCK);
				item.setData(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(60 * 60).cooldownGroup(trappedNewbieKey("time_machine")).build());
				return item;
			}).withTrophyModel(trappedNewbieKey("time_machine"))
		)
		.buildAndRegister(Walk10KKMAdvancement::new);
	public static final IAdvancement WALK_5000KM = buildBase(WALK_1000KM, "walk_5000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().superTorture().icon(Material.DIAMOND_BOOTS))
		.withReward(rewards().withExp(5000))
		.buildAndRegister();
	public static final IAdvancement WALK_10000KM = buildBase(WALK_5000KM, "walk_10000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.NETHERITE_BOOTS))
		.withReward(rewards().withExp(10000))
		.buildAndRegister();
	public static final IAdvancement SPRINT_10KM = buildBase(WALK_10KM, "sprint_10km").display(display().y(1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.RED_TERRACOTTA))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement SPRINT_MARATHON = buildBase(SPRINT_10KM, "sprint_marathon").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.GRASS_BLOCK))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement SPRINT_250KM = buildBase(SPRINT_MARATHON, "sprint_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.CHAINMAIL_LEGGINGS))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(potionItem(Material.POTION, PotionType.WATER))
		)
		.buildAndRegister();
	public static final IAdvancement SPRINT_1000KM = buildBase(SPRINT_250KM, "sprint_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(ItemUtil.glint(Material.NETHERITE_LEGGINGS)))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(ItemStack.of(Material.CHAINMAIL_BOOTS))
		)
		.buildAndRegister();
	public static final IAdvancement SPRINT_2500KM = buildBase(SPRINT_1000KM, "sprint_2500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(potionItem(Material.SPLASH_POTION, PotionType.INVISIBILITY))).buildAndRegister();
	public static final IAdvancement SPRINT_5000KM = buildBase(SPRINT_2500KM, "sprint_5000km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.FEATHER)).buildAndRegister();
	public static final IAdvancement SPRINT_10000KM = buildBase(SPRINT_5000KM, "sprint_10000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.BARRIER)).buildAndRegister();
	public static final IAdvancement JUMP_1K = buildBase(WALK_10KM, "jump_1k").display(display().y(2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_FENCE))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement JUMP_10K = buildBase(JUMP_1K, "jump_10k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.ACACIA_LEAVES))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement JUMP_50K = buildBase(JUMP_10K, "jump_50k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).goalFrame().icon(Material.CYAN_CARPET))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemStack.of(Material.SLIME_BLOCK))
		)
		.buildAndRegister();
	public static final IAdvancement JUMP_100K = buildBase(JUMP_50K, "jump_100k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.SLIME_BLOCK)).buildAndRegister();
	public static final IAdvancement JUMP_250K = buildBase(JUMP_100K, "jump_250k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.RABBIT_FOOT))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTE0NDkxOGUzNjc5ZjE5OGRmYWE3MzNiZjAwMzViY2NlMWVkMjkxOGQ1OTczMWVjNjU0MzgzNzJjM2U1NDhmIn19fQ==")) // todo textured heads
		)
		.buildAndRegister();
	public static final IAdvancement JUMP_500K = buildBase(JUMP_250K, "jump_500k").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(potionItem(Material.POTION, PotionType.LEAPING))).buildAndRegister();
	public static final IAdvancement JUMP_1000K = buildBase(JUMP_500K, "jump_1000k").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.PINK_BED)).buildAndRegister();
	public static final IAdvancement BOAT_1KM = buildBase(WALK_10KM, "boat_1km").display(display().y(3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.OAK_BOAT))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.NAUTILUS_SHELL)))
		.buildAndRegister();
	public static final IAdvancement BOAT_10KM = buildBase(BOAT_1KM, "boat_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.SPRUCE_BOAT))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.NAUTILUS_SHELL, 3)))
		.buildAndRegister();
	public static final IAdvancement BOAT_25KM = buildBase(BOAT_10KM, "boat_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DARK_OAK_BOAT))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.NAUTILUS_SHELL, 8))
			.withTrophy(ItemStack.of(Material.DARK_OAK_BOAT))
		)
		.buildAndRegister();
	public static final IAdvancement BOAT_50KM = buildBase(BOAT_25KM, "boat_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.MANGROVE_BOAT)).buildAndRegister();
	public static final IAdvancement BOAT_100KM = buildBase(BOAT_50KM, "boat_100km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.ACACIA_BOAT)).buildAndRegister();
	public static final IAdvancement BOAT_250KM = buildBase(BOAT_100KM, "boat_250km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.BIRCH_BOAT)).buildAndRegister();
	public static final IAdvancement BOAT_500KM = buildBase(BOAT_250KM, "boat_500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.CHERRY_BOAT)))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(ItemStack.of(Material.CHERRY_BOAT))
		)
		.buildAndRegister();
	public static final IAdvancement HORSE_1KM = buildBase(WALK_10KM, "horse_1km").display(display().y(4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.IRON_HORSE_ARMOR))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.LEATHER, 3), ItemStack.of(Material.IRON_HORSE_ARMOR)))
		.buildAndRegister();
	public static final IAdvancement HORSE_10KM = buildBase(HORSE_1KM, "horse_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.GOLDEN_HORSE_ARMOR))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.LEATHER, 8), ItemStack.of(Material.GOLDEN_HORSE_ARMOR)))
		.buildAndRegister();
	public static final IAdvancement HORSE_25KM = buildBase(HORSE_10KM, "horse_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.DIAMOND_HORSE_ARMOR))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.LEATHER, 25), ItemStack.of(Material.DIAMOND_HORSE_ARMOR), ItemStack.of(Material.SADDLE))
			.withTrophy(() -> {
				var item = ItemStack.of(Material.BOW);
				item.setData(DataComponentTypes.UNBREAKABLE);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement HORSE_50KM = buildBase(HORSE_25KM, "horse_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.BONE)).buildAndRegister();
	public static final IAdvancement HORSE_100KM = buildBase(HORSE_50KM, "horse_100km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(ItemUtil.texturedHead(MoreMobHeads.SKELETON_HORSE))).buildAndRegister();
	public static final IAdvancement HORSE_250KM = buildBase(HORSE_100KM, "horse_250km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.SADDLE))
		.withReward(rewards()
			.withExp(250)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Q4ODY0M2IwNDg2Nzk2MjRhNDJlZTA0NjY2YTQ4NjVlYzE2ODcxYzczMjc5NzM0NjVhZjZiZDVhYmRkOGNhNyJ9fX0=")) // todo textured heads
		)
		.buildAndRegister();
	public static final IAdvancement HORSE_500KM = buildBase(HORSE_250KM, "horse_500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(wrathHorseItem())).buildAndRegister();
	public static final IAdvancement PIG_100M = buildBase(WALK_10KM, "pig_100m").display(display().y(5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.SADDLE))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.PORKCHOP, 3)))
		.buildAndRegister();
	public static final IAdvancement PIG_1KM = buildBase(PIG_100M, "pig_1km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.CARROT_ON_A_STICK))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.PORKCHOP, 8), ItemStack.of(Material.CARROT, 16)))
		.buildAndRegister();
	public static final IAdvancement PIG_10KM = buildBase(PIG_1KM, "pig_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(PIG_HEAD))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.PORKCHOP, 24), ItemStack.of(Material.SADDLE))
			.withTrophy(ItemStack.of(Material.PISTON))
		)
		.buildAndRegister();
	public static final IAdvancement PIG_25KM = buildBase(PIG_10KM, "pig_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.PORKCHOP)).buildAndRegister();
	public static final IAdvancement PIG_50KM = buildBase(PIG_25KM, "pig_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.COOKED_PORKCHOP)).buildAndRegister();
	public static final IAdvancement PIG_100KM = buildBase(PIG_50KM, "pig_100km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(TrappedNewbieItems.ASH))
		.withReward(rewards()
			.withExp(200)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2FiZjM0OWUwOTU5NGE2ZjYzMGEzMWU2MDdlY2U5YTExODc0N2UxN2U5NmUyN2ExOGExMWFlMDljZjFiMTJkMSJ9fX0=")) // todo textured heads
		)
		.buildAndRegister();
	public static final IAdvancement PIG_250KM = buildBase(PIG_100KM, "pig_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(PIG_HOG_HEAD)).buildAndRegister();
	public static final IAdvancement ELYTRA_10KM = buildBase(WALK_10KM, "elytra_10km").display(display().y(6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.PHANTOM_MEMBRANE))
		.withReward(rewards()
			.withExp(50)
			.addItems(() -> {
				var item = ItemStack.of(Material.FIREWORK_ROCKET, 64);
				item.setData(DataComponentTypes.FIREWORKS, Fireworks.fireworks().flightDuration(1).build());
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement ELYTRA_100KM = buildBase(ELYTRA_10KM, "elytra_100km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.ELYTRA))
		.withReward(rewards()
			.withExp(200)
			.addItems(() -> {
				var item = ItemStack.of(Material.FIREWORK_ROCKET, 192);
				item.setData(DataComponentTypes.FIREWORKS, Fireworks.fireworks().flightDuration(2).build());
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement ELYTRA_1000KM = buildBase(ELYTRA_100KM, "elytra_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.FIREWORK_ROCKET))
		.withReward(rewards()
			.withExp(500)
			.addItems(() -> {
				var fireworks = ItemStack.of(Material.FIREWORK_ROCKET, 64);
				fireworks.setData(DataComponentTypes.FIREWORKS, Fireworks.fireworks().flightDuration(3).build());
				var item = ItemStack.of(Material.RED_SHULKER_BOX);
				List<ItemStack> contents = new ArrayList<>(27);
				for (int i = 0; i < 27; i++) {
					if (i % 9 >= 3 && i % 9 <= 5) {
						contents.add(item);
					} else {
						contents.add(ItemStack.empty());
					}
				}
				item.setData(DataComponentTypes.CONTAINER, ItemContainerContents.containerContents(contents));
				return item;
			})
			.withTrophy(() -> {
				var item = ItemStack.of(Material.ELYTRA);
				item.addEnchantment(Enchantment.UNBREAKING, 3);
				item.addEnchantment(Enchantment.MENDING, 1);
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement ELYTRA_2500KM = buildBase(ELYTRA_1000KM, "elytra_2500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.PURPUR_BLOCK)).buildAndRegister();
	public static final IAdvancement ELYTRA_5000KM = buildBase(ELYTRA_2500KM, "elytra_5000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.FIREWORK_STAR)).buildAndRegister();
	public static final IAdvancement ELYTRA_10000KM = buildBase(ELYTRA_5000KM, "elytra_10000km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(brokenElytra())).buildAndRegister();
	public static final IAdvancement ELYTRA_15000KM = buildBase(ELYTRA_10000KM, "elytra_15000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.glint(Material.ELYTRA)))
		.withReward(rewards()
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNjMjQ4NzBhNjVlNGYzZmE0M2I5MmYxOTRhM2M2ZjdlYzllNmNkNmM1MmYxZTY2MjRlNWMyNmI4ZGQ1ZDkifX19")) // todo textured heads
		)
		.buildAndRegister();
	// Left bottom
	public static final IAdvancement SWIM_1KM = buildBase(WALK_10KM, "swim_1km").display(display().y(-1F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemStack.of(Material.LEATHER_LEGGINGS).withColor(Color.BLUE)))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.PUFFERFISH, 3)))
		.buildAndRegister();
	public static final IAdvancement SWIM_10KM = buildBase(SWIM_1KM, "swim_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.GOLDEN_LEGGINGS))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.PUFFERFISH, 8)))
		.buildAndRegister();
	public static final IAdvancement SWIM_50KM = buildBase(SWIM_10KM, "swim_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.GOLD_BLOCK))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.PUFFERFISH, 24))
			.withTrophy(ItemStack.of(Material.SUNFLOWER))
		)
		.buildAndRegister();
	public static final IAdvancement SWIM_100KM = buildBase(SWIM_50KM, "swim_100km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.WATER_BUCKET)).buildAndRegister();
	public static final IAdvancement SWIM_250KM = buildBase(SWIM_100KM, "swim_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(ItemUtil.glint(Material.RAW_GOLD_BLOCK)))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(ItemUtil.texturedHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzI2NjY2ODFkMzk0MzI1YzJkMWMwN2E0ZDJkZTRmZjEwZWMxODkwZmI1MGNhYTMzZmEyOWNjODc1ZWMxZDhlIn19fQ=="))
		)
		.buildAndRegister();
	public static final IAdvancement SWIM_500KM = buildBase(SWIM_250KM, "swim_500km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.CONDUIT)).buildAndRegister();
	public static final IAdvancement SWIM_1000KM = buildBase(SWIM_500KM, "swim_1000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.COD)).buildAndRegister();
	public static final IAdvancement SNEAK_100M = buildBase(WALK_10KM, "sneak_100m").display(display().y(-2F).fancyDescriptionParent(NamedTextColor.GREEN).icon(ItemStack.of(Material.LEATHER_BOOTS).withColor(Color.BLACK)))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement SNEAK_1KM = buildBase(SNEAK_100M, "sneak_1km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(ItemStack.of(Material.LEATHER_CHESTPLATE).withColor(Color.BLACK)))
		.withReward(rewards().withExp(200))
		.buildAndRegister();
	public static final IAdvancement SNEAK_10KM = buildBase(SNEAK_1KM, "sneak_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.PANDA_SPAWN_EGG))
		.withReward(rewards()
			.withExp(500)
			.withTrophy(() -> {
				var item = ItemStack.of(Material.LEATHER_LEGGINGS);
				item.setData(DataComponentTypes.UNBREAKABLE);
				item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.BLACK));
				return item;
			})
		)
		.buildAndRegister();
	public static final IAdvancement SNEAK_25KM = buildBase(SNEAK_10KM, "sneak_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(potionItem(Material.POTION, PotionType.INVISIBILITY))).buildAndRegister();
	public static final IAdvancement SNEAK_50KM = buildBase(SNEAK_25KM, "sneak_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.BARRIER)).buildAndRegister();
	public static final IAdvancement SNEAK_100KM = buildBase(SNEAK_50KM, "sneak_100km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.STRUCTURE_VOID))
		.withReward(rewards()
			.withExp(400)
			.withTrophy(ItemStack.of(Material.IRON_SWORD))
		)
		.buildAndRegister();
	public static final IAdvancement SNEAK_250KM = buildBase(SNEAK_100KM, "sneak_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(SHIFTING_HEAD)).buildAndRegister();
	public static final IAdvancement CLIMB_100M = buildBase(WALK_10KM, "climb_100m").display(display().y(-3F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.VINE))
		.withReward(rewards().withExp(5))
		.buildAndRegister();
	public static final IAdvancement CLIMB_500M = buildBase(CLIMB_100M, "climb_500m").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.WEEPING_VINES))
		.withReward(rewards().withExp(45))
		.buildAndRegister();
	public static final IAdvancement CLIMB_3KM = buildBase(CLIMB_500M, "climb_3km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.LADDER))
		.withReward(rewards()
			.withExp(160)
			.withTrophy(ItemStack.of(Material.LADDER))
		)
		.buildAndRegister();
	public static final IAdvancement CLIMB_10KM = buildBase(CLIMB_3KM, "climb_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).withAdvancementFrame(AdvancementFrame.BUTTERFLY).icon(LADDER_ACE_HEAD))
		.withReward(rewards()
			.withExp(300)
			.withTrophy(ItemStack.of(Material.LADDER))
		)
		.buildAndRegister();
	public static final IAdvancement MINECART_1KM = buildBase(WALK_10KM, "minecart_1km").display(display().y(-4F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.MINECART))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.IRON_INGOT, 8)))
		.buildAndRegister();
	public static final IAdvancement MINECART_10KM = buildBase(MINECART_1KM, "minecart_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.FURNACE_MINECART))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.IRON_INGOT, 24)))
		.buildAndRegister();
	public static final IAdvancement MINECART_50KM = buildBase(MINECART_10KM, "minecart_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.COMMAND_BLOCK_MINECART))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.IRON_BLOCK, 8))
			.withTrophy(ItemStack.of(Material.FURNACE_MINECART))
		)
		.buildAndRegister();
	public static final IAdvancement MINECART_250KM = buildBase(MINECART_50KM, "minecart_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(MINECART_HEAD))
		.withReward(rewards()
			.withExp(250)
			.withTrophy(ItemStack.of(Material.POWERED_RAIL))
		)
		.buildAndRegister();
	public static final IAdvancement MINECART_500KM = buildBase(MINECART_250KM, "minecart_500km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.HOPPER_MINECART)).buildAndRegister();
	public static final IAdvancement MINECART_5000KM = buildBase(MINECART_500KM, "minecart_5000km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.CHEST_MINECART)).buildAndRegister();
	public static final IAdvancement MINECART_15000KM = buildBase(MINECART_5000KM, "minecart_15000km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.TNT_MINECART)).buildAndRegister();
	public static final IAdvancement STRIDER_100M = buildBase(WALK_10KM, "strider_100m").display(display().y(-5F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.LAVA_BUCKET))
		.withReward(rewards().withExp(50).addItems(ItemStack.of(Material.WARPED_FUNGUS, 3)))
		.buildAndRegister();
	public static final IAdvancement STRIDER_1KM = buildBase(STRIDER_100M, "strider_1km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.WARPED_FUNGUS_ON_A_STICK))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.WARPED_FUNGUS, 8)))
		.buildAndRegister();
	public static final IAdvancement STRIDER_10KM = buildBase(STRIDER_1KM, "strider_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.STRIDER_SPAWN_EGG))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.WARPED_FUNGUS, 24))
			.withTrophy(ItemStack.of(Material.WARPED_FUNGUS))
		)
		.buildAndRegister();
	public static final IAdvancement STRIDER_25KM = buildBase(STRIDER_10KM, "strider_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.WARPED_FUNGUS)).buildAndRegister();
	public static final IAdvancement STRIDER_50KM = buildBase(STRIDER_25KM, "strider_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.LEAD)).buildAndRegister();
	public static final IAdvancement STRIDER_100KM = buildBase(STRIDER_50KM, "strider_100km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.LEAD)).buildAndRegister();
	public static final IAdvancement STRIDER_250KM = buildBase(STRIDER_100KM, "strider_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(ItemUtil.texturedHead(MoreMobHeads.STRIDER)))
		.withReward(rewards()
			.withExp(180)
			.withTrophy(ItemUtil.texturedHead(MoreMobHeads.STRIDER)) // TODO actual heads should render texture in modifier
		)
		.buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_100M = buildBase(WALK_10KM, "happy_ghast_100m").display(display().y(-6F).fancyDescriptionParent(NamedTextColor.GREEN).icon(Material.LIGHT_GRAY_HARNESS))
		.withReward(rewards().withExp(50))
		.buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_1KM = buildBase(HAPPY_GHAST_100M, "happy_ghast_1km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.AQUA).goalFrame().icon(Material.BROWN_HARNESS))
		.withReward(rewards().withExp(200).addItems(ItemStack.of(Material.DRIED_GHAST)))
		.buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_10KM = buildBase(HAPPY_GHAST_1KM, "happy_ghast_10km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_PURPLE).challengeFrame().icon(Material.RED_HARNESS))
		.withReward(rewards()
			.withExp(500)
			.addItems(ItemStack.of(Material.DRIED_GHAST, 3))
			.withTrophy(ItemStack.of(Material.LIGHT_BLUE_WOOL))
		)
		.buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_25KM = buildBase(HAPPY_GHAST_10KM, "happy_ghast_25km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.LIGHT_PURPLE).challengeFrame().superChallenge().icon(Material.YELLOW_HARNESS)).buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_50KM = buildBase(HAPPY_GHAST_25KM, "happy_ghast_50km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.DARK_RED).challengeFrame().torture().icon(Material.MAGENTA_HARNESS))
		.withReward(rewards()
			.withExp(1000)
			.withTrophy(ItemStack.of(Material.LIGHT_GRAY_HARNESS))
		)
		.buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_100KM = buildBase(HAPPY_GHAST_50KM, "happy_ghast_100km").display(display().x(-1F).fancyDescriptionParent(PURPLE).challengeFrame().superTorture().icon(Material.PURPLE_HARNESS)).buildAndRegister();
	public static final IAdvancement HAPPY_GHAST_250KM = buildBase(HAPPY_GHAST_100KM, "happy_ghast_250km").display(display().x(-1F).fancyDescriptionParent(NamedTextColor.BLACK).withAdvancementFrame(AdvancementFrame.BUTTERFLY).cheat().icon(Material.BLACK_HARNESS)).buildAndRegister();

	private static ItemStack braveNewWorldItem() {
		var item = ItemStack.of(TrappedNewbieItems.LETTER);
		NBT.modify(item, nbt -> {
			nbt.setItemStackArray(LetterModifier.CONTENTS_TAG, new ItemStack[]{WANDERING_TRADER_HEAD});
			nbt.setEnum(LetterModifier.TYPE_TAG, LetterModifier.LetterType.STAR);
		});
		return item;
	}

	private static ItemStack brokenElytra() {
		var item = ItemStack.of(Material.ELYTRA);
		item.setData(DataComponentTypes.DAMAGE, 1);
		item.setData(DataComponentTypes.MAX_DAMAGE, 1);
		return item;
	}

	private static ItemStack wrathHorseItem() {
		var item = ItemStack.of(Material.LEATHER_HORSE_ARMOR);
		item.setData(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
		item.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(Color.fromRGB(10409302)));
		return item;
	}

	private static ItemStack potionItem(Material itemType, PotionType potionType) {
		var item = ItemStack.of(itemType);
		item.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(potionType).build());
		return item;
	}

	private static ItemStack banner(Material type, Pattern... patterns) {
		var item = ItemStack.of(type);
		item.setData(DataComponentTypes.BANNER_PATTERNS, BannerPatternLayers.bannerPatternLayers(List.of(patterns)));
		return item;
	}

	private static ItemStack shield(DyeColor dyeColor) {
		var item = ItemStack.of(Material.SHIELD);
		item.setData(DataComponentTypes.BASE_COLOR, dyeColor);
		return item;
	}

	private static FancierAdvancementDisplay.FancierAdvancementDisplayImpl display() {
		return FancierAdvancementDisplay.fancierDisplay().fancyDescriptionParent(NamedTextColor.GRAY);
	}

	private static FancyAdvancementReward rewards() {
		return new FancyAdvancementReward();
	}

	public static void setupAdvancements() {
		// Filler
	}

}
