package me.sosedik.essence;

import me.sosedik.essence.command.BedSpawnCommand;
import me.sosedik.essence.command.GravityCommand;
import me.sosedik.essence.command.HealCommand;
import me.sosedik.essence.command.HealthCommand;
import me.sosedik.essence.command.HungerCommand;
import me.sosedik.essence.command.ItemCommand;
import me.sosedik.essence.command.MoreCommand;
import me.sosedik.essence.command.TopCommand;
import me.sosedik.essence.command.UpCommand;
import me.sosedik.essence.command.WorldCommand;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Essence extends JavaPlugin {

	private static @UnknownNullability Essence instance;

	private @UnknownNullability Scheduler scheduler;

	@Override
	public void onLoad() {
		Essence.instance = this;
		this.scheduler = new Scheduler(this);

		TranslationHolder.extractLocales(this);
	}

	@Override
	public void onEnable() {
		registerCommands();
	}

	private void registerCommands() {
		CommandManager.commandManager().registerCommands(this,
			BedSpawnCommand.class,
			GravityCommand.class,
			HealCommand.class,
			HealthCommand.class,
			HungerCommand.class,
			ItemCommand.class,
			MoreCommand.class,
			TopCommand.class,
			UpCommand.class,
			WorldCommand.class
		);
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static Essence instance() {
		return Essence.instance;
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

}
