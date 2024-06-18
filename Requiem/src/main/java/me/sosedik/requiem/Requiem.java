package me.sosedik.requiem;

import me.sosedik.requiem.command.ReviveCommand;
import me.sosedik.requiem.listener.entity.PrepareGhostMobs;
import me.sosedik.requiem.listener.player.DeathMakesGhosts;
import me.sosedik.requiem.listener.player.GhostsDontSprint;
import me.sosedik.requiem.listener.player.GhostsDontStarveOrChoke;
import me.sosedik.requiem.listener.player.GhostsKeepNightVision;
import me.sosedik.requiem.listener.player.LoadSavePlayers;
import me.sosedik.requiem.listener.player.NoAirForWaterPossessing;
import me.sosedik.requiem.listener.player.PossessedDismount;
import me.sosedik.requiem.listener.player.PossessingOverMobs;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Requiem extends JavaPlugin {

	private static Requiem instance;

	private Scheduler scheduler;

	@Override
	public void onLoad() {
		Requiem.instance = this;
		this.scheduler = new Scheduler(this);
	}

	@Override
	public void onEnable() {
		registerCommands();
		EventUtil.registerListeners(this,
			// entity
			PrepareGhostMobs.class,
			// player
			DeathMakesGhosts.class,
			GhostsDontSprint.class,
			GhostsDontStarveOrChoke.class,
			GhostsKeepNightVision.class,
			LoadSavePlayers.class,
			NoAirForWaterPossessing.class,
			PossessedDismount.class,
			PossessingOverMobs.class
		);
	}

	private void registerCommands() {
		CommandManager.commandManager().registerCommands(this,
			ReviveCommand.class
		);
	}

	@Override
	public void onDisable() {
		PrepareGhostMobs.unregisterGhosts();
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static @NotNull Requiem instance() {
		return Requiem.instance;
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
