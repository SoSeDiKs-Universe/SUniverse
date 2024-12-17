package me.sosedik.delightfulfarming;

import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class DelightfulFarming extends JavaPlugin {

	private static DelightfulFarming instance;

	private Scheduler scheduler;

	@Override
	public void onLoad() {
		DelightfulFarming.instance = this;
		this.scheduler = new Scheduler(this);

		TranslationHolder.extractLocales(this);
		ResourceLib.loadDefaultResources(this);
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static DelightfulFarming instance() {
		return DelightfulFarming.instance;
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
	public static NamespacedKey delightfulFarmingKey(String value) {
		return new NamespacedKey("delightful_farming", value);
	}

}
