package me.sosedik.utilizer;

import io.leangen.geantyref.TypeToken;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.utilizer.api.command.parser.AnyString;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.command.LangCommand;
import me.sosedik.utilizer.command.TranslatorCommand;
import me.sosedik.utilizer.impl.item.modifier.CustomTotemOfUndyingModifier;
import me.sosedik.utilizer.impl.item.modifier.DyedItemNamesModifier;
import me.sosedik.utilizer.impl.item.modifier.GlowingItemModifier;
import me.sosedik.utilizer.impl.item.modifier.HiddenTooltipsModifier;
import me.sosedik.utilizer.impl.message.tag.DiscordResolver;
import me.sosedik.utilizer.impl.message.tag.KaomojiTag;
import me.sosedik.utilizer.impl.message.tag.LocaleResolver;
import me.sosedik.utilizer.impl.message.tag.PluralTag;
import me.sosedik.utilizer.impl.message.tag.RandomColorTag;
import me.sosedik.utilizer.listener.BlockStorage;
import me.sosedik.utilizer.listener.block.CustomBlockStorageLoadSaveInteract;
import me.sosedik.utilizer.listener.entity.EntityGlowTracker;
import me.sosedik.utilizer.listener.entity.EntityMetadataClearer;
import me.sosedik.utilizer.listener.entity.SprayItemDrops;
import me.sosedik.utilizer.listener.item.AutoReleasingItems;
import me.sosedik.utilizer.listener.item.BowUsableWithoutArrows;
import me.sosedik.utilizer.listener.item.NotDroppableItems;
import me.sosedik.utilizer.listener.item.PlaceableBlockItems;
import me.sosedik.utilizer.listener.misc.CustomRecipeLeftovers;
import me.sosedik.utilizer.listener.misc.DelayedActions;
import me.sosedik.utilizer.listener.misc.DurabilityRecipeLeftovers;
import me.sosedik.utilizer.listener.misc.ExtraChatTabSuggestions;
import me.sosedik.utilizer.listener.misc.ExtraRecipeHandlers;
import me.sosedik.utilizer.listener.misc.FixLeftAirClickWhenRightClickingEntity;
import me.sosedik.utilizer.listener.misc.MilkImmuneEffects;
import me.sosedik.utilizer.listener.player.CleanupPlayerScoreboards;
import me.sosedik.utilizer.listener.player.PlayerDataLoadSave;
import me.sosedik.utilizer.listener.player.PlayerLanguageLoadSave;
import me.sosedik.utilizer.listener.player.PlayerOptions;
import me.sosedik.utilizer.listener.player.SetupPlayerScoreboards;
import me.sosedik.utilizer.listener.player.UpdateInventoryOnLocaleChange;
import me.sosedik.utilizer.listener.recipe.FireCrafting;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.ServerLinks;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.bukkit.internal.BukkitBrigadierMapper;
import org.incendo.cloud.parser.ParserDescriptor;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.net.URI;

@NullMarked
public final class Utilizer extends JavaPlugin {

	private static @UnknownNullability Utilizer instance;

	private @UnknownNullability Scheduler scheduler;

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
			RandomColorTag.RANDOM_COLOR,
			PluralTag.PLURALS
		);
		Mini.registerViewerAwareTagResolvers(
			DiscordResolver::new,
			LocaleResolver::new
		);
		URI discordUrl = URI.create(DiscordResolver.DISCORD_URL.startsWith("http") ? DiscordResolver.DISCORD_URL : "https://" + DiscordResolver.DISCORD_URL);
		Bukkit.getServer().getServerLinks().addLink(ServerLinks.Type.REPORT_BUG, discordUrl);
		Bukkit.getServer().getServerLinks().addLink(ServerLinks.Type.COMMUNITY, discordUrl);
		Bukkit.getServer().getServerLinks().addLink(ServerLinks.Type.WEBSITE, URI.create("https://sosedik.com"));

		new CustomTotemOfUndyingModifier(utilizerKey("custom_totem_display")).register();
		new DyedItemNamesModifier(utilizerKey("dyed_item_name")).register();
		new GlowingItemModifier(utilizerKey("glowing_item")).register();
		new HiddenTooltipsModifier(utilizerKey("hidden_tooltips")).register();

		registerCommands();

		EventUtil.registerListeners(this,
			// block
			CustomBlockStorageLoadSaveInteract.class,
			// entity
			EntityGlowTracker.class,
			EntityMetadataClearer.class,
			SprayItemDrops.class,
			// item
			AutoReleasingItems.class,
			BowUsableWithoutArrows.class,
			NotDroppableItems.class,
			PlaceableBlockItems.class,
			// misc
			CustomRecipeLeftovers.class,
			DelayedActions.class,
			DurabilityRecipeLeftovers.class,
			ExtraChatTabSuggestions.class,
			ExtraRecipeHandlers.class,
			FixLeftAirClickWhenRightClickingEntity.class,
			MilkImmuneEffects.class,
			// player
			CleanupPlayerScoreboards.class,
			PlayerDataLoadSave.class,
			PlayerLanguageLoadSave.class,
			PlayerOptions.class,
			SetupPlayerScoreboards.class,
			UpdateInventoryOnLocaleChange.class,
			// recipe
			FireCrafting.class
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
		BlockStorage.saveAllData();
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
