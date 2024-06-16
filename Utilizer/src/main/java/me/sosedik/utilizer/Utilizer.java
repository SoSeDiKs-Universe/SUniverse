package me.sosedik.utilizer;

import me.sosedik.utilizer.api.language.LangKeysStorage;
import me.sosedik.utilizer.listener.player.PlayerDataLoadSave;
import me.sosedik.utilizer.listener.player.PlayerLanguageLoadSave;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class Utilizer extends JavaPlugin {

	private static Utilizer instance;

	private Scheduler scheduler;

	@Override
	public void onLoad() {
		Utilizer.instance = this;
		scheduler = new Scheduler(this);

		CommandManager.init(this);
	}

	@Override
	public void onEnable() {
		LangKeysStorage.init(this);
		EventUtil.registerListeners(this,
			// player
			PlayerDataLoadSave.class,
			PlayerLanguageLoadSave.class
		);
	}

	@Override
	public void onDisable() {
		PlayerDataLoadSave.saveAllData();
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static @NotNull Utilizer instance() {
		return Utilizer.instance;
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
