package me.sosedik.fancymotd;

import io.papermc.paper.ServerBuildInfo;
import me.sosedik.fancymotd.feature.MotdIconStorage;
import me.sosedik.fancymotd.listener.MotdRandomizer;
import me.sosedik.fancymotd.listener.NotWhitelistedKick;
import me.sosedik.fancymotd.listener.PaperMotdRandomizer;
import me.sosedik.fancymotd.listener.PingerRefresher;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FancyMotd extends JavaPlugin {

	private static @UnknownNullability FancyMotd instance;

	@Override
	public void onLoad() {
		FancyMotd.instance = this;

		if (getServer().getPluginManager().getPlugin("Utilizer") != null) {
			TranslationHolder.extractLocales(this);
		}
		Pinger.setupDatabase();
	}

	@Override
	public void onEnable() {
		MotdIconStorage.refreshIcons(this);
		if (getServer().getPluginManager().getPlugin("Utilizer") == null || !ServerBuildInfo.buildInfo().isBrandCompatible(Key.key("suniverse", "kiterino"))) {
			getLogger().warning("Current server software does not support Kiterino API");
			getServer().getPluginManager().registerEvents(new PaperMotdRandomizer(), this);
			return;
		}
		EventUtil.registerListeners(this,
			MotdRandomizer.class,
			NotWhitelistedKick.class,
			PingerRefresher.class
		);
		Pinger.runCleanupTask();
	}

	@Override
	public void onDisable() {
		Pinger.closeDatabase();
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static FancyMotd instance() {
		return FancyMotd.instance;
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
