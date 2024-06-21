package me.sosedik.trappednewbie;

import io.leangen.geantyref.TypeToken;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.limboworldgenerator.VoidChunkGenerator;
import me.sosedik.trappednewbie.api.command.parser.PlayerWorldParser;
import me.sosedik.trappednewbie.listener.world.LimboWorldFall;
import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.FileUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
import org.jetbrains.annotations.NotNull;

import java.io.File;

import static me.sosedik.trappednewbie.api.command.parser.PlayerWorldParser.playerWorldParser;

public final class TrappedNewbie extends JavaPlugin {

	private static TrappedNewbie instance;

	private Scheduler scheduler;

	@Override
	public void onLoad() {
		TrappedNewbie.instance = this;
		this.scheduler = new Scheduler(this);

		cleanupTemporaryWorlds();
	}

	private void cleanupTemporaryWorlds() {
		FileUtil.deleteFolder(new File(Bukkit.getWorldContainer(), "worlds-resources"));
	}

	@Override
	public void onEnable() {
		checkLimboWorld();
		applyWorldRules();
		registerCommands();
		EventUtil.registerListeners(this,
			// world
			LimboWorldFall.class,
			PerPlayerWorlds.class
		);
	}

	private void checkLimboWorld() {
		World world = Bukkit.getWorlds().getFirst();
		if (!(world.getGenerator() instanceof VoidChunkGenerator)) return;

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
		commandManager.manager().parserRegistry().registerParser(playerWorldParser());
		BukkitBrigadierMapper<CommandSourceStack> mapper = new BukkitBrigadierMapper<>(
			getLogger(),
			commandManager.manager().brigadierManager()
		);
		mapper.mapSimpleNMS(new TypeToken<PlayerWorldParser<CommandSourceStack>>() {}, "nbt_path", true);
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static @NotNull TrappedNewbie instance() {
		return TrappedNewbie.instance;
	}

	/**
	 * Gets the plugin's task scheduler
	 *
	 * @return the plugin's task scheduler
	 */
	public static @NotNull Scheduler scheduler() {
		return instance().scheduler;
	}

	/**
	 * Gets the plugin's component logger
	 *
	 * @return the plugin's component logger
	 */
	public static @NotNull ComponentLogger logger() {
		return instance().getComponentLogger();
	}

}
