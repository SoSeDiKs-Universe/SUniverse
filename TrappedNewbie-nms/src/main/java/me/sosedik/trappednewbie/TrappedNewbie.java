package me.sosedik.trappednewbie;

import io.leangen.geantyref.TypeToken;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.limboworldgenerator.VoidChunkGenerator;
import me.sosedik.miscme.task.CustomDayCycleTask;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.api.command.parser.PlayerWorldParser;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.api.task.BossBarTask;
import me.sosedik.trappednewbie.command.MigrateCommand;
import me.sosedik.trappednewbie.command.SpitCommand;
import me.sosedik.trappednewbie.command.TestCommand;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieRecipes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.impl.block.nms.ClayKilnBlock;
import me.sosedik.trappednewbie.impl.blockstorage.ChoppingBlockStorage;
import me.sosedik.trappednewbie.impl.blockstorage.ClayKilnBlockStorage;
import me.sosedik.trappednewbie.impl.blockstorage.DrumBlockStorage;
import me.sosedik.trappednewbie.impl.blockstorage.FlowerPotBlockStorage;
import me.sosedik.trappednewbie.impl.blockstorage.SleepingBagBlockStorage;
import me.sosedik.trappednewbie.impl.blockstorage.TotemBaseBlockStorage;
import me.sosedik.trappednewbie.impl.blockstorage.WorkStationBlockStorage;
import me.sosedik.trappednewbie.impl.item.modifier.AdvancementTrophyModifier;
import me.sosedik.trappednewbie.impl.item.modifier.AdvancementTrophyNameLoreModifier;
import me.sosedik.trappednewbie.impl.item.modifier.ItemModelModifier;
import me.sosedik.trappednewbie.impl.item.modifier.ItemOverlayToggleModifier;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.trappednewbie.impl.item.modifier.PaperPlaneModifier;
import me.sosedik.trappednewbie.impl.item.modifier.UnlitCampfireModifier;
import me.sosedik.trappednewbie.impl.item.modifier.VisualArmorModifier;
import me.sosedik.trappednewbie.impl.recipe.ChoppingBlockCrafting;
import me.sosedik.trappednewbie.listener.advancement.AdvancementRecipes;
import me.sosedik.trappednewbie.listener.advancement.AdvancementTrophies;
import me.sosedik.trappednewbie.listener.advancement.AdvancementsLocalizer;
import me.sosedik.trappednewbie.listener.advancement.LoadSaveAdvancementsOnJoinQuit;
import me.sosedik.trappednewbie.listener.advancement.dedicated.CampfirePlacingAdvancements;
import me.sosedik.trappednewbie.listener.advancement.dedicated.CrossbowWithFireworkAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.CrossbowsHotbarAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.FindGravelAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.FirstPossessionAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.FoodConsumingAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.GoodAsNewAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.HalfAHeartAdvancements;
import me.sosedik.trappednewbie.listener.advancement.dedicated.HorseStatsAdvancements;
import me.sosedik.trappednewbie.listener.advancement.dedicated.IHateSandAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.IgniteChargedCreeperMidairAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.InventoryAdvancements;
import me.sosedik.trappednewbie.listener.advancement.dedicated.Kill10ChickensInAirAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.KungFuPandaAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.LevelAdvancements;
import me.sosedik.trappednewbie.listener.advancement.dedicated.LootOpensAdvancements;
import me.sosedik.trappednewbie.listener.advancement.dedicated.OpeningHolderAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.PathwaysAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.QuickDeathAdvancements;
import me.sosedik.trappednewbie.listener.advancement.dedicated.StatisticAdvancements;
import me.sosedik.trappednewbie.listener.advancement.dedicated.TNTPrimesAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.WashAdvancements;
import me.sosedik.trappednewbie.listener.advancement.dedicated.YouMonsterAdvancement;
import me.sosedik.trappednewbie.listener.block.BlockBreakHurts;
import me.sosedik.trappednewbie.listener.block.BlockChoppingViaSwing;
import me.sosedik.trappednewbie.listener.block.CustomBlockBreaking;
import me.sosedik.trappednewbie.listener.block.SoftBlockHandBreaking;
import me.sosedik.trappednewbie.listener.block.UnlitCampfireByDefault;
import me.sosedik.trappednewbie.listener.entity.AngryAnimals;
import me.sosedik.trappednewbie.listener.entity.BabierBabyMobs;
import me.sosedik.trappednewbie.listener.entity.CreepersLoveCrawlers;
import me.sosedik.trappednewbie.listener.entity.LimboEntities;
import me.sosedik.trappednewbie.listener.entity.LimboWandererTrades;
import me.sosedik.trappednewbie.listener.entity.ShearableCreepers;
import me.sosedik.trappednewbie.listener.item.BlackBeltSpeed;
import me.sosedik.trappednewbie.listener.item.FirestrikerFire;
import me.sosedik.trappednewbie.listener.item.FlintToFlakedFlint;
import me.sosedik.trappednewbie.listener.item.FlowerBouquetAttackEffects;
import me.sosedik.trappednewbie.listener.item.FriendshipLetters;
import me.sosedik.trappednewbie.listener.item.GlassShardCuts;
import me.sosedik.trappednewbie.listener.item.HammerBlockRepair;
import me.sosedik.trappednewbie.listener.item.KnifeCarvesTotemBases;
import me.sosedik.trappednewbie.listener.item.MeshSifting;
import me.sosedik.trappednewbie.listener.item.PaperPlanes;
import me.sosedik.trappednewbie.listener.item.RoughSticksCreateFire;
import me.sosedik.trappednewbie.listener.item.SimpleSoundItems;
import me.sosedik.trappednewbie.listener.item.SteelAndFlint;
import me.sosedik.trappednewbie.listener.item.ThrowableRockBehavior;
import me.sosedik.trappednewbie.listener.item.TimeMachineClockIsTotemOfUndying;
import me.sosedik.trappednewbie.listener.item.TrumpetScare;
import me.sosedik.trappednewbie.listener.item.VisualPumpkin;
import me.sosedik.trappednewbie.listener.misc.CustomHudRenderer;
import me.sosedik.trappednewbie.listener.misc.DisableJoinQuitMessages;
import me.sosedik.trappednewbie.listener.misc.DynamicInventoryInfoGatherer;
import me.sosedik.trappednewbie.listener.misc.DynamicReducedF3DebugInfo;
import me.sosedik.trappednewbie.listener.misc.FakeHardcoreHearts;
import me.sosedik.trappednewbie.listener.misc.TabHeaderFooterBeautifier;
import me.sosedik.trappednewbie.listener.misc.TemporaryScoreboardAdvancementMessages;
import me.sosedik.trappednewbie.listener.player.DisableNaturalRespawn;
import me.sosedik.trappednewbie.listener.player.DynamicGameMode;
import me.sosedik.trappednewbie.listener.player.ExtraPossessedDrops;
import me.sosedik.trappednewbie.listener.player.NewbieWelcome;
import me.sosedik.trappednewbie.listener.player.StartAsGhost;
import me.sosedik.trappednewbie.listener.player.TaskManagement;
import me.sosedik.trappednewbie.listener.player.TeamableLeatherEquipment;
import me.sosedik.trappednewbie.listener.player.TotemRituals;
import me.sosedik.trappednewbie.listener.player.TrappedNewbiePlayerOptions;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.trappednewbie.listener.world.LimboWorldFall;
import me.sosedik.trappednewbie.listener.world.LimitedLimbo;
import me.sosedik.trappednewbie.listener.world.NoDayChangeInLimbo;
import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.listener.BlockStorage;
import me.sosedik.utilizer.listener.item.PlaceableBlockItems;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.FileUtil;
import me.sosedik.utilizer.util.InventoryUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.List;

@NullMarked
public final class TrappedNewbie extends JavaPlugin {

	public static final String NAMESPACE = "trapped_newbie";
	private static final int CHUNK_RADIUS = 2;

	private static @UnknownNullability TrappedNewbie instance;
	private static @Nullable World limboWorld;

	private @UnknownNullability Scheduler scheduler;

	@Override
	public void onLoad() {
		TrappedNewbie.instance = this;
		this.scheduler = new Scheduler(this);

		cleanupTemporaryWorlds();

		TranslationHolder.extractLocales(this);
		ResourceLib.loadDefaultResources(this);

		TrappedNewbieTags.CHOPPING_BLOCKS.getValues().forEach(material -> BlockStorage.addMapping(material, ChoppingBlockStorage.class));
		TrappedNewbieTags.DRUMS.getValues().forEach(material -> BlockStorage.addMapping(material, DrumBlockStorage.class));
		TrappedNewbieTags.TOTEM_BASES.getValues().forEach(material -> BlockStorage.addMapping(material, TotemBaseBlockStorage.class));
		TrappedNewbieTags.WORK_STATIONS.getValues().forEach(material -> BlockStorage.addMapping(material, WorkStationBlockStorage.class));
		Tag.FLOWER_POTS.getValues().forEach(material -> BlockStorage.addMapping(material, FlowerPotBlockStorage.class));
		BlockStorage.addMapping(TrappedNewbieItems.CLAY_KILN, ClayKilnBlockStorage.class);
		BlockStorage.addMapping(TrappedNewbieItems.SLEEPING_BAG, SleepingBagBlockStorage.class);
	}

	private void cleanupTemporaryWorlds() {
		if (true) return; // TODO only if month had passed?
		FileUtil.deleteFolder(new File(Bukkit.getWorldContainer(), "worlds-resources"));
	}

	@Override
	public void onEnable() {
		applyWorldRules();
		setupLimboWorld();
		registerCommands();

		BossBarTask.init(this);
		TrappedNewbieRecipes.addRecipes();
		ChoppingBlockCrafting.registerRecipes();
		TrappedNewbieAdvancements.setupAdvancements();

		InventoryUtil.addExtraItemChecker(player -> List.of(VisualArmor.of(player).getAllContents()));

		new AdvancementTrophyModifier(trappedNewbieKey("advancement_trophy")).register();
		new AdvancementTrophyNameLoreModifier(trappedNewbieKey("advancement_trophy_name_lore")).register();
		new ItemModelModifier(trappedNewbieKey("item_model")).register();
		ItemModelModifier.addReplacement(TrappedNewbieItems.SLEEPING_BAG, ResourceLib.storage().getItemModelMapping(trappedNewbieKey("sleeping_bag_item")));
		ItemModelModifier.addReplacement(TrappedNewbieItems.CLAY_KILN, contextBox -> {
			if (!contextBox.getItem().hasBlockData()) return null;
			if (!(contextBox.getItem().getBlockData(contextBox.getInitialType()) instanceof ClayKilnBlock.ClayKiln clayKiln)) return null;
			if (!clayKiln.isBurned()) return null;
			return ResourceLib.storage().getItemModelMapping(trappedNewbieKey("brick_kiln"));
		});
		new ItemOverlayToggleModifier(trappedNewbieKey("overlay_toggle")).register();
		new LetterModifier(trappedNewbieKey("letter")).register();
		new PaperPlaneModifier(trappedNewbieKey("paper_plane")).register();
		new UnlitCampfireModifier(trappedNewbieKey("unlit_campfire")).register();
		new VisualArmorModifier(trappedNewbieKey("visual_armor")).register();

		EventUtil.registerListeners(this,
			// advancement
			AdvancementRecipes.class,
			AdvancementsLocalizer.class,
			AdvancementTrophies.class,
			LoadSaveAdvancementsOnJoinQuit.class,
			/// dedicated advancement
			CampfirePlacingAdvancements.class,
			CrossbowsHotbarAdvancement.class,
			CrossbowWithFireworkAdvancement.class,
			FindGravelAdvancement.class,
			FirstPossessionAdvancement.class,
			FoodConsumingAdvancement.class,
			GoodAsNewAdvancement.class,
			HalfAHeartAdvancements.class,
			HorseStatsAdvancements.class,
			IgniteChargedCreeperMidairAdvancement.class,
			IHateSandAdvancement.class,
			InventoryAdvancements.class,
			Kill10ChickensInAirAdvancement.class,
			KungFuPandaAdvancement.class,
			LevelAdvancements.class,
			LootOpensAdvancements.class,
			OpeningHolderAdvancement.class,
			PathwaysAdvancement.class,
			QuickDeathAdvancements.class,
			StatisticAdvancements.class,
			TNTPrimesAdvancement.class,
			WashAdvancements.class,
			YouMonsterAdvancement.class,
			// block
			BlockBreakHurts.class,
			BlockChoppingViaSwing.class,
			CustomBlockBreaking.class,
			SoftBlockHandBreaking.class,
			UnlitCampfireByDefault.class,
			// entity
			AngryAnimals.class,
			BabierBabyMobs.class,
			CreepersLoveCrawlers.class,
			LimboEntities.class,
			LimboWandererTrades.class,
			ShearableCreepers.class,
			// item
			BlackBeltSpeed.class,
			FirestrikerFire.class,
			FlintToFlakedFlint.class,
			FlowerBouquetAttackEffects.class,
			FriendshipLetters.class,
			GlassShardCuts.class,
			HammerBlockRepair.class,
			KnifeCarvesTotemBases.class,
			MeshSifting.class,
			PaperPlanes.class,
			RoughSticksCreateFire.class,
			SimpleSoundItems.class,
			SteelAndFlint.class,
			ThrowableRockBehavior.class,
			TimeMachineClockIsTotemOfUndying.class,
			TrumpetScare.class,
			VisualPumpkin.class,
			// misc
			CustomHudRenderer.class,
			DisableJoinQuitMessages.class,
			DynamicInventoryInfoGatherer.class,
			DynamicReducedF3DebugInfo.class,
			FakeHardcoreHearts.class,
			TabHeaderFooterBeautifier.class,
			TemporaryScoreboardAdvancementMessages.class,
			// player
			DisableNaturalRespawn.class,
			DynamicGameMode.class,
			ExtraPossessedDrops.class,
			NewbieWelcome.class,
			StartAsGhost.class,
			TaskManagement.class,
			TeamableLeatherEquipment.class,
			TotemRituals.class,
			TrappedNewbiePlayerOptions.class,
			VisualArmorLayer.class,
			// world
			LimboWorldFall.class,
			LimitedLimbo.class,
			NoDayChangeInLimbo.class,
			PerPlayerWorlds.class,
			// command
			MigrateCommand.class
		);

		TrappedNewbieTags.ROCKS.getValues().forEach(rock -> {
			Material pebble = Material.getMaterial(rock.name().replace("ROCK", "PEBBLE"));
			if (pebble != null)
				PlaceableBlockItems.addMapping(rock, pebble);
		});

		GhostyPlayer.addFlightDenyRule(player -> player.getWorld() == limboWorld());
		GhostyPlayer.addItemsDenyRule(player -> player.getWorld() == limboWorld());
		PossessingPlayer.addItemsDenyRule(player -> player.getWorld() == limboWorld());
	}

	@Override
	public void onDisable() {
		TrappedNewbieAdvancements.MANAGER.saveProgresses();

		for (int x = -CHUNK_RADIUS; x < CHUNK_RADIUS; x++) {
			for (int y = -CHUNK_RADIUS; y < CHUNK_RADIUS; y++) {
				limboWorld().setChunkForceLoaded(x, y, false);
			}
		}
	}

	private void setupLimboWorld() {
		World world = Bukkit.getWorlds().getFirst();
		if (!(world.getGenerator() instanceof VoidChunkGenerator)) {
			getLogger().warning("Limbo world generator has changed!");
			return;
		}

		world.setFullTime(0);
		world.setDifficulty(Difficulty.PEACEFUL);
		world.setGameRule(GameRule.SPAWN_RADIUS, 0);
		world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 3);
		world.setGameRule(GameRule.DO_INSOMNIA, false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
		world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
		world.setGameRule(GameRule.DO_WARDEN_SPAWNING, false);
		world.setGameRule(GameRule.DO_VINES_SPREAD, false);
		world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
		world.setGameRule(GameRule.MOB_GRIEFING, false);
		new CustomDayCycleTask(world, () -> {
			if (Bukkit.getServerTickManager().isFrozen()) return 0D;
			if (limboWorld().getPlayers().isEmpty()) return 0D;
			return Bukkit.getServerTickManager().getTickRate() / 2D;
		});

		// Spawn chunks are disabled, force them for limbo
		for (int x = -CHUNK_RADIUS; x < CHUNK_RADIUS; x++) {
			for (int y = -CHUNK_RADIUS; y < CHUNK_RADIUS; y++) {
				world.setChunkForceLoaded(x, y, true);
			}
		}

		Block block = world.getHighestBlockAt(0, 0);
		if (block.getLocation().getBlockY() > world.getMinHeight()) return;

		block = world.getBlockAt(0, 120, 0);
		if (block.isEmpty()) {
			block.setType(Material.GLASS);
			world.setSpawnLocation(0, 121, 0);
		}
	}

	private void applyWorldRules() {
		for (World world : Bukkit.getWorlds()) {
			PerPlayerWorlds.applyWorldRules(world);
		}
	}

	private void registerCommands() {
		var commandManager = CommandManager.commandManager();

		commandManager.manager().parserRegistry().registerParser(PlayerWorldParser.playerWorldParser());
		BukkitBrigadierMapper<CommandSourceStack> mapper = new BukkitBrigadierMapper<>(
			getLogger(),
			commandManager.manager().brigadierManager()
		);
		mapper.mapSimpleNMS(new TypeToken<PlayerWorldParser<CommandSourceStack>>() {}, "nbt_path", true);

		commandManager.registerCommands(this,
			MigrateCommand.class,
			SpitCommand.class,
			TestCommand.class
		);
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static TrappedNewbie instance() {
		return TrappedNewbie.instance;
	}

	/**
	 * Gets the plugin's task scheduler
	 *
	 * @return the plugin's task scheduler
	 */
	public static Scheduler scheduler() {
		return instance().scheduler;
	}

	/**
	 * Gets the plugin's component logger
	 *
	 * @return the plugin's component logger
	 */
	public static ComponentLogger logger() {
		return instance().getComponentLogger();
	}

	/**
	 * Makes a namespaced key with this plugin's namespace
	 *
	 * @param value value
	 * @return namespaced key
	 */
	public static NamespacedKey trappedNewbieKey(String value) {
		return new NamespacedKey(NAMESPACE, value);
	}

	/**
	 * Gets the Limbo world
	 *
	 * @return limbo world
	 */
	public static World limboWorld() {
		if (limboWorld == null)
			limboWorld = Bukkit.getWorlds().getFirst();
		return limboWorld;
	}

}
