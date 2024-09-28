package me.sosedik.utilizer;

import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.impl.item.modifier.HiddenTooltipsModifier;
import me.sosedik.utilizer.impl.message.tag.KaomojiTag;
import me.sosedik.utilizer.impl.message.tag.LocaleResolver;
import me.sosedik.utilizer.impl.message.tag.RandomColorTag;
import me.sosedik.utilizer.listener.entity.EntityGlowTracker;
import me.sosedik.utilizer.listener.entity.EntityMetadataClearer;
import me.sosedik.utilizer.listener.misc.CustomRecipeLeftovers;
import me.sosedik.utilizer.listener.misc.DurabilityRecipeLeftovers;
import me.sosedik.utilizer.listener.misc.ExtraChatTabSuggestions;
import me.sosedik.utilizer.listener.misc.ExtraRecipeHandlers;
import me.sosedik.utilizer.listener.misc.MilkImmuneEffects;
import me.sosedik.utilizer.listener.player.CleanupPlayerScoreboards;
import me.sosedik.utilizer.listener.player.PlayerDataLoadSave;
import me.sosedik.utilizer.listener.player.PlayerLanguageLoadSave;
import me.sosedik.utilizer.listener.player.SetupPlayerScoreboards;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Utilizer extends JavaPlugin {

	private static Utilizer instance;

	private Scheduler scheduler;

	@Override
	public void onLoad() {
		Utilizer.instance = this;

		setupDefaultConfig();

		scheduler = new Scheduler(this);
	}

	@Override
	public void onEnable() {
		CommandManager.init(this);
		LangOptionsStorage.init(this);

		Mini.registerTagResolvers(
			TagResolver.resolver("discord", Tag.selfClosingInserting(Component.text(getConfig().getString("discord", "discord.com")))),
			KaomojiTag.KAOMOJI,
			RandomColorTag.RANDOM_COLOR
		);
		Mini.registerViewerAwareTagResolvers(
			LocaleResolver::new
		);

		new HiddenTooltipsModifier(utilizerKey("hidden_tooltips")).register();

		EventUtil.registerListeners(this,
			// entity
			EntityGlowTracker.class,
			EntityMetadataClearer.class,
			// misc
			CustomRecipeLeftovers.class,
			DurabilityRecipeLeftovers.class,
			ExtraRecipeHandlers.class,
			ExtraChatTabSuggestions.class,
			MilkImmuneEffects.class,
			// player
			CleanupPlayerScoreboards.class,
			PlayerDataLoadSave.class,
			PlayerLanguageLoadSave.class,
			SetupPlayerScoreboards.class
		);
		saveConfig();
	}

	private void setupDefaultConfig() {
		FileConfiguration config = getConfig();
		if (!config.contains("discord")) config.set("discord", "discord.com");
	}

	@Override
	public void onDisable() {
		PlayerDataLoadSave.saveAllData();
		EntityGlowTracker.unregisterTeams();
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

	/**
	 * Makes a namespaced key with this plugin's namespace
	 *
	 * @param value value
	 * @return namespaced key
	 */
	public static @NotNull NamespacedKey utilizerKey(@NotNull String value) {
		return new NamespacedKey("utilizer", value);
	}

}
