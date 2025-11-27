package me.sosedik.socializer;

import me.sosedik.socializer.command.DiscordCommand;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.discord.DiscordChatRenderer;
import me.sosedik.socializer.listener.DiscordServerStatusUpdater;
import me.sosedik.socializer.listener.FriendlyPlayers;
import me.sosedik.socializer.listener.LoadSaveDiscordersOnJoinLeave;
import me.sosedik.socializer.listener.MinecraftChatLinker;
import me.sosedik.socializer.listener.MinecraftEventLogger;
import me.sosedik.socializer.listener.discord.AccountLinking;
import me.sosedik.socializer.listener.discord.ConsoleCommands;
import me.sosedik.socializer.listener.discord.DiscordChatLinker;
import me.sosedik.socializer.listener.discord.MembershipGaining;
import me.sosedik.socializer.listener.discord.NoNicknameChange;
import me.sosedik.utilizer.CommandManager;
import me.sosedik.utilizer.api.database.Database;
import me.sosedik.utilizer.api.language.TranslationHolder;
import me.sosedik.utilizer.util.EventUtil;
import me.sosedik.utilizer.util.Scheduler;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.internal.managers.AudioManagerImpl;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class Socializer extends JavaPlugin {

	private static @UnknownNullability Socializer instance;

	private @UnknownNullability Scheduler scheduler;
	private @UnknownNullability Database database;

	@Override
	public void onLoad() {
		Socializer.instance = this;
		this.scheduler = new Scheduler(this);
		this.database = Database.prepareDatabase(this, "socializers");

		TranslationHolder.extractLocales(this);

		if (getConfig().getBoolean("discord.run-bot", false)) {
			DiscordBot.setupBot(this);
		}
	}

	@Override
	public void onEnable() {
		// Discord listeners
		if (getConfig().getBoolean("discord.run-bot", false)) {
			new AccountLinking(this);
			new ConsoleCommands(this);
			new DiscordChatLinker(this, new DiscordChatRenderer());
			new MembershipGaining(this);
			new NoNicknameChange();

			EventUtil.registerListeners(this,
				DiscordServerStatusUpdater.class
			);

			// Load early, so it's available in classpath during disable
			loadClasses(ShutdownEvent.class, AudioManagerImpl.class);
		}

		EventUtil.registerListeners(this,
			FriendlyPlayers.class,
			LoadSaveDiscordersOnJoinLeave.class,
			MinecraftChatLinker.class,
			MinecraftEventLogger.class
		);

		CommandManager.commandManager().registerCommands(this,
			DiscordCommand.class
		);
	}

	private void loadClasses(Class<?>... classes) {
		// Yay!
	}

	@Override
	public void onDisable() {
		DiscordBot.shutdown();
	}

	/**
	 * Gets the plugin instance
	 *
	 * @return the plugin instance
	 */
	public static Socializer instance() {
		return Socializer.instance;
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
	public static NamespacedKey socializerKey(String value) {
		return new NamespacedKey("socializer", value);
	}

	/**
	 * Gets the plugin's database
	 *
	 * @return the plugin's database
	 */
	public static Database database() {
		return instance().database;
	}

}
