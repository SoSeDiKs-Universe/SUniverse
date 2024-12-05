package me.sosedik.socializer;

import me.sosedik.socializer.command.DiscordCommand;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.discord.DiscordChatRenderer;
import me.sosedik.socializer.listener.DiscordServerStatusUpdater;
import me.sosedik.socializer.listener.LoadSaveDiscordersOnJoinLeave;
import me.sosedik.socializer.listener.MinecraftChatLinker;
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
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Socializer extends JavaPlugin {

	private static Socializer instance;

	private Scheduler scheduler;
	private Database database;

	@Override
	public void onLoad() {
		Socializer.instance = this;
		this.scheduler = new Scheduler(this);
		this.database = Database.prepareDatabase(this, "socializers");

		TranslationHolder.extractLocales(this);

		if (getConfig().getBoolean("discord.run-bot")) {
			DiscordBot.setupBot(this);
		}
	}

	@Override
	public void onEnable() {
		// Discord listeners
		if (getConfig().getBoolean("discord.run-bot")) {
			new AccountLinking(this);
			new ConsoleCommands(this);
			new DiscordChatLinker(this, new DiscordChatRenderer());
			new MembershipGaining(this);
			new NoNicknameChange();

			EventUtil.registerListeners(this,
				DiscordServerStatusUpdater.class
			);
		}

		EventUtil.registerListeners(this,
			LoadSaveDiscordersOnJoinLeave.class,
			MinecraftChatLinker.class
		);

		CommandManager.commandManager().registerCommands(this,
			DiscordCommand.class
		);
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
	public static @NotNull Socializer instance() {
		return Socializer.instance;
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
	public static @NotNull NamespacedKey socializerKey(@NotNull String value) {
		return new NamespacedKey("socializer", value);
	}

	/**
	 * Gets the plugin's database
	 *
	 * @return the plugin's database
	 */
	public static @NotNull Database database() {
		return instance().database;
	}

}
