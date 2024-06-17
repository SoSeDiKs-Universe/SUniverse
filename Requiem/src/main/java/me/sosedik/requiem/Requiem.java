package me.sosedik.requiem;

import me.sosedik.requiem.listener.entity.PrepareGhostMobs;
import me.sosedik.requiem.listener.player.LoadSaveGhosts;
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
		EventUtil.registerListeners(this,
			// entity
			PrepareGhostMobs.class,
			// player
			LoadSaveGhosts.class
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
