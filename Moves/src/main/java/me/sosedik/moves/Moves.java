package me.sosedik.moves;

import me.sosedik.moves.listener.movement.FreeFall;
import me.sosedik.moves.listener.movement.HigherWaterJump;
import me.sosedik.moves.listener.movement.SneakCounter;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Moves extends JavaPlugin {

	private static Moves instance;

	private Scheduler scheduler;

	@Override
	public void onLoad() {
		Moves.instance = this;
		this.scheduler = new Scheduler(this);
	}

	@Override
	public void onEnable() {
		EventUtil.registerListeners(this,
			// movement
			FreeFall.class,
			HigherWaterJump.class,
			SneakCounter.class
		);
	}

	@Override
	public void onDisable() {
		// TODO
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static @NotNull Moves instance() {
		return Moves.instance;
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
