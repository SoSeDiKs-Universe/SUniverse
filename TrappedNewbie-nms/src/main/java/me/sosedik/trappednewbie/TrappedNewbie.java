package me.sosedik.trappednewbie;

import io.leangen.geantyref.TypeToken;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.limboworldgenerator.VoidChunkGenerator;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.trappednewbie.api.command.parser.PlayerWorldParser;
import me.sosedik.trappednewbie.command.SpitCommand;
import me.sosedik.trappednewbie.impl.item.modifier.PaperPlaneModifier;
import me.sosedik.trappednewbie.impl.item.modifier.VisualArmorModifier;
import me.sosedik.trappednewbie.listener.item.PaperPlanes;
import me.sosedik.trappednewbie.listener.misc.DisableJoinQuitMessages;
import me.sosedik.trappednewbie.listener.misc.FakeHardcoreHearts;
import me.sosedik.trappednewbie.listener.misc.TabHeaderFooterBeautifier;
import me.sosedik.trappednewbie.listener.player.TeamableLeatherEquipment;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.trappednewbie.listener.world.InfiniteStartingNight;
import me.sosedik.trappednewbie.listener.world.LimboWorldFall;
import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import me.sosedik.trappednewbie.misc.TrappedNewbieRecipes;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.FileUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
import org.jspecify.annotations.NullMarked;

import java.io.File;

@NullMarked
public final class TrappedNewbie extends JavaPlugin {

	public static final String NAMESPACE = "trapped_newbie";

	private static TrappedNewbie instance;

	private Scheduler scheduler;

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
		setupLimboWorld();
		applyWorldRules();
		registerCommands();

		TrappedNewbieRecipes.addRecipes();

		new PaperPlaneModifier(trappedNewbieKey("paper_plane")).register();
		new VisualArmorModifier(trappedNewbieKey("visual_armor")).register();

		EventUtil.registerListeners(this,
			// item
			PaperPlanes.class,
			// misc
			DisableJoinQuitMessages.class,
			FakeHardcoreHearts.class,
			TabHeaderFooterBeautifier.class,
			// player
			TeamableLeatherEquipment.class,
			VisualArmorLayer.class,
			// world
			InfiniteStartingNight.class,
			LimboWorldFall.class,
			PerPlayerWorlds.class
		);
	}

	private void setupLimboWorld() {
		World world = Bukkit.getWorlds().getFirst();
		if (!(world.getGenerator() instanceof VoidChunkGenerator)) {
			getLogger().warning("Limbo world generator has changed!");
			return;
		}

		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setFullTime(0);

		Block block = world.getHighestBlockAt(0, 0);
		if (block.getLocation().getBlockY() > world.getMinHeight()) return;

		block = world.getBlockAt(0, 120, 0);
		block.setType(Material.GLASS);
		world.setSpawnLocation(0, 121, 0);
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

}
