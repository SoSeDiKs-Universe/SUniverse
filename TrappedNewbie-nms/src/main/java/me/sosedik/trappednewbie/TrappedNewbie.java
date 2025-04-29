package me.sosedik.trappednewbie;

import io.leangen.geantyref.TypeToken;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.limboworldgenerator.VoidChunkGenerator;
import me.sosedik.miscme.task.CustomDayCycleTask;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.api.command.parser.PlayerWorldParser;
import me.sosedik.trappednewbie.command.MigrateCommand;
import me.sosedik.trappednewbie.command.SpitCommand;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieRecipes;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.trappednewbie.impl.item.modifier.PaperPlaneModifier;
import me.sosedik.trappednewbie.impl.item.modifier.VisualArmorModifier;
import me.sosedik.trappednewbie.listener.advancement.AdvancementsLocalizer;
import me.sosedik.trappednewbie.listener.advancement.LoadSaveAdvancementsOnJoinQuit;
import me.sosedik.trappednewbie.listener.advancement.dedicated.FindGravelAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.FirstPossessionAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.GoodAsNewAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.IHateSandAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.KungFuPandaAdvancement;
import me.sosedik.trappednewbie.listener.advancement.dedicated.OpeningHolderAdvancement;
import me.sosedik.trappednewbie.listener.block.CustomBlockBreaking;
import me.sosedik.trappednewbie.listener.entity.AngryAnimals;
import me.sosedik.trappednewbie.listener.entity.BabierBabyMobs;
import me.sosedik.trappednewbie.listener.entity.LimboEntities;
import me.sosedik.trappednewbie.listener.item.FlintToFlakedFlint;
import me.sosedik.trappednewbie.listener.item.PaperPlanes;
import me.sosedik.trappednewbie.listener.item.RoughSticksCreateFire;
import me.sosedik.trappednewbie.listener.item.TrumpetScare;
import me.sosedik.trappednewbie.listener.item.VisualPumpkin;
import me.sosedik.trappednewbie.listener.misc.CustomHudRenderer;
import me.sosedik.trappednewbie.listener.misc.DisableJoinQuitMessages;
import me.sosedik.trappednewbie.listener.misc.FakeHardcoreHearts;
import me.sosedik.trappednewbie.listener.misc.TabHeaderFooterBeautifier;
import me.sosedik.trappednewbie.listener.player.DynamicGameMode;
import me.sosedik.trappednewbie.listener.player.ExtraPossessedDrops;
import me.sosedik.trappednewbie.listener.player.NewbieWelcome;
import me.sosedik.trappednewbie.listener.player.StartAsGhost;
import me.sosedik.trappednewbie.listener.player.TaskManagement;
import me.sosedik.trappednewbie.listener.player.TeamableLeatherEquipment;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.trappednewbie.listener.world.LimboWorldFall;
import me.sosedik.trappednewbie.listener.world.LimitedLimbo;
import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.FileUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.io.File;

@NullMarked
public final class TrappedNewbie extends JavaPlugin {

	public static final String NAMESPACE = "trapped_newbie";

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

		TrappedNewbieRecipes.addRecipes();
		TrappedNewbieAdvancements.setupAdvancements();

		new LetterModifier(trappedNewbieKey("letter")).register();
		new PaperPlaneModifier(trappedNewbieKey("paper_plane")).register();
		new VisualArmorModifier(trappedNewbieKey("visual_armor")).register();

		EventUtil.registerListeners(this,
			// advancement
			AdvancementsLocalizer.class,
			LoadSaveAdvancementsOnJoinQuit.class,
			/// dedicated advancement
			FindGravelAdvancement.class,
			FirstPossessionAdvancement.class,
			GoodAsNewAdvancement.class,
			IHateSandAdvancement.class,
			KungFuPandaAdvancement.class,
			OpeningHolderAdvancement.class,
			// block
			CustomBlockBreaking.class,
			// entity
			AngryAnimals.class,
			BabierBabyMobs.class,
			LimboEntities.class,
			// item
			FlintToFlakedFlint.class,
			PaperPlanes.class,
			RoughSticksCreateFire.class,
			TrumpetScare.class,
			VisualPumpkin.class,
			// misc
			CustomHudRenderer.class,
			DisableJoinQuitMessages.class,
			FakeHardcoreHearts.class,
			TabHeaderFooterBeautifier.class,
			// player
			DynamicGameMode.class,
			ExtraPossessedDrops.class,
			NewbieWelcome.class,
			StartAsGhost.class,
			TaskManagement.class,
			TeamableLeatherEquipment.class,
			VisualArmorLayer.class,
			// world
			LimboWorldFall.class,
			LimitedLimbo.class,
			PerPlayerWorlds.class,
			// command
			MigrateCommand.class
		);

		GhostyPlayer.addFlightDenyRule(player -> player.getWorld() == limboWorld());
		GhostyPlayer.addItemsDenyRule(player -> player.getWorld() == limboWorld());
		PossessingPlayer.addItemsDenyRule(player -> player.getWorld() == limboWorld());
	}

	@Override
	public void onDisable() {
		TrappedNewbieAdvancements.MANAGER.saveProgresses();
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
			SpitCommand.class
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
