package me.sosedik.utilizer;

import io.leangen.geantyref.TypeToken;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.utilizer.api.command.parser.AnyString;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.command.LangCommand;
import me.sosedik.utilizer.command.TranslatorCommand;
import me.sosedik.utilizer.impl.item.modifier.GlowingItemModifier;
import me.sosedik.utilizer.impl.item.modifier.HiddenTooltipsModifier;
import me.sosedik.utilizer.impl.message.tag.DiscordResolver;
import me.sosedik.utilizer.impl.message.tag.KaomojiTag;
import me.sosedik.utilizer.impl.message.tag.LocaleResolver;
import me.sosedik.utilizer.impl.message.tag.RandomColorTag;
import me.sosedik.utilizer.listener.entity.EntityGlowTracker;
import me.sosedik.utilizer.listener.entity.EntityMetadataClearer;
import me.sosedik.utilizer.listener.item.BowUsableWithoutArrows;
import me.sosedik.utilizer.listener.item.NotDroppableItems;
import me.sosedik.utilizer.listener.misc.CustomRecipeLeftovers;
import me.sosedik.utilizer.listener.misc.DelayedActions;
import me.sosedik.utilizer.listener.misc.DurabilityRecipeLeftovers;
import me.sosedik.utilizer.listener.misc.ExtraChatTabSuggestions;
import me.sosedik.utilizer.listener.misc.ExtraRecipeHandlers;
import me.sosedik.utilizer.listener.misc.MilkImmuneEffects;
import me.sosedik.utilizer.listener.player.CleanupPlayerScoreboards;
import me.sosedik.utilizer.listener.player.PlayerDataLoadSave;
import me.sosedik.utilizer.listener.player.PlayerLanguageLoadSave;
import me.sosedik.utilizer.listener.player.SetupPlayerScoreboards;
import me.sosedik.utilizer.listener.player.UpdateInventoryOnLocaleChange;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
import org.incendo.cloud.parser.ParserDescriptor;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Utilizer extends JavaPlugin {

	private static Utilizer instance;

	private Scheduler scheduler;

	@Override
	public void onLoad() {
		Utilizer.instance = this;

		setupDefaultConfig();

		LangOptionsStorage.init(this);
		TranslationHolder.extractLocales(this);

		scheduler = new Scheduler(this);
	}

	@Override
	public void onEnable() {
		CommandManager.init(this);

		Mini.registerTagResolvers(
			KaomojiTag.KAOMOJI,
			RandomColorTag.RANDOM_COLOR
		);
		Mini.registerViewerAwareTagResolvers(
			DiscordResolver::new,
			LocaleResolver::new
		);

		new GlowingItemModifier(utilizerKey("glowing_item")).register();
		new HiddenTooltipsModifier(utilizerKey("hidden_tooltips")).register();

		registerCommands();

		EventUtil.registerListeners(this,
			// entity
			EntityGlowTracker.class,
			EntityMetadataClearer.class,
			// item
			BowUsableWithoutArrows.class,
			NotDroppableItems.class,
			// misc
			CustomRecipeLeftovers.class,
			DelayedActions.class,
			DurabilityRecipeLeftovers.class,
			ExtraRecipeHandlers.class,
			ExtraChatTabSuggestions.class,
			MilkImmuneEffects.class,
			// player
			CleanupPlayerScoreboards.class,
			PlayerDataLoadSave.class,
			PlayerLanguageLoadSave.class,
			SetupPlayerScoreboards.class,
			UpdateInventoryOnLocaleChange.class
		);
		saveConfig();
	}

	private void registerCommands() {
		var commandManager = CommandManager.commandManager();

		BukkitBrigadierMapper<CommandSourceStack> mapper = new BukkitBrigadierMapper<>(
			getLogger(),
			commandManager.manager().brigadierManager()
		);
		var anyStringTypeToken = new TypeToken<AnyString.AnyStringParser<CommandSourceStack>>() {};
		mapper.mapSimpleNMS(anyStringTypeToken, "nbt_path", true);
		commandManager.manager().parserRegistry().registerParser(ParserDescriptor.of(new AnyString.AnyStringParser<>(), AnyString.class));

		commandManager.registerCommands(this,
			LangCommand.class,
			TranslatorCommand.class
		);
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
	public static Utilizer instance() {
		return Utilizer.instance;
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
	public static NamespacedKey utilizerKey(String value) {
		return new NamespacedKey("utilizer", value);
	}

}
