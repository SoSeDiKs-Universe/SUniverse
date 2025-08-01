package me.sosedik.moves;

import me.sosedik.moves.command.LayCommand;
import me.sosedik.moves.command.SitCommand;
import me.sosedik.moves.listener.block.FallThroughLeaves;
import me.sosedik.moves.listener.block.WaterPuddleHurts;
import me.sosedik.moves.listener.entity.ShulkerCrawlerHandler;
import me.sosedik.moves.listener.movement.CrawlingMechanics;
import me.sosedik.moves.listener.movement.DontLoseAirOnTopOfWater;
import me.sosedik.moves.listener.movement.FallSoftener;
import me.sosedik.moves.listener.movement.FreeFall;
import me.sosedik.moves.listener.movement.HigherWaterJump;
import me.sosedik.moves.listener.movement.LayingMechanics;
import me.sosedik.moves.listener.movement.PlayerFallTicker;
import me.sosedik.moves.listener.movement.RollOnFall;
import me.sosedik.moves.listener.movement.SittingMechanics;
import me.sosedik.moves.listener.movement.SneakCounter;
import me.sosedik.moves.listener.movement.StickingToBlocks;
import me.sosedik.moves.listener.movement.SwimmingInOneBlockSpace;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Moves extends JavaPlugin {

	private static @UnknownNullability Moves instance;

	private @UnknownNullability Scheduler scheduler;

	@Override
	public void onLoad() {
		Moves.instance = this;
		this.scheduler = new Scheduler(this);

		TranslationHolder.extractLocales(this);
	}

	@Override
	public void onEnable() {
		CommandManager.commandManager().registerCommands(this,
			LayCommand.class,
			SitCommand.class
		);

		EventUtil.registerListeners(this,
			// block
			FallThroughLeaves.class,
			WaterPuddleHurts.class,
			// entity
			ShulkerCrawlerHandler.class,
			// movement
			CrawlingMechanics.class,
			DontLoseAirOnTopOfWater.class,
			FallSoftener.class,
			FreeFall.class,
			HigherWaterJump.class,
			LayingMechanics.class,
			PlayerFallTicker.class,
			RollOnFall.class,
			SittingMechanics.class,
			SneakCounter.class,
			StickingToBlocks.class,
			SwimmingInOneBlockSpace.class
		);
	}

	@Override
	public void onDisable() {
		SittingMechanics.removeChairs();
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static Moves instance() {
		return Moves.instance;
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
	public static NamespacedKey movesKey(String value) {
		return new NamespacedKey("moves", value);
	}

}
