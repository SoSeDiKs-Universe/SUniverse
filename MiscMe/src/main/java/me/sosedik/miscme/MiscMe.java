package me.sosedik.miscme;

import me.sosedik.miscme.listener.block.CampfireSetsOnFire;
import me.sosedik.miscme.listener.block.BlockKnocking;
import me.sosedik.miscme.listener.block.DoorBells;
import me.sosedik.miscme.listener.misc.BetterTimeSetCommand;
import me.sosedik.miscme.listener.player.HidePlayerNameTags;
import me.sosedik.miscme.listener.world.CustomDayCycleCleanup;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class MiscMe extends JavaPlugin {

	private static MiscMe instance;

	private Scheduler scheduler;

	@Override
	public void onLoad() {
		MiscMe.instance = this;
		this.scheduler = new Scheduler(this);
	}

	@Override
	public void onEnable() {
		EventUtil.registerListeners(this,
			// block
			BlockKnocking.class,
			CampfireSetsOnFire.class,
			DoorBells.class,
			// misc
			BetterTimeSetCommand.class,
			// player
			HidePlayerNameTags.class,
			// world
			CustomDayCycleCleanup.class
		);
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static @NotNull MiscMe instance() {
		return MiscMe.instance;
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
