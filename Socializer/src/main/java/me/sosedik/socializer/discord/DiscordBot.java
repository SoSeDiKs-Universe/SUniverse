package me.sosedik.socializer.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.util.DiscordUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@NullMarked
public class DiscordBot {

	private DiscordBot() {
		throw new IllegalStateException("Utility class");
	}

	private static @Nullable JDA discordBot;
	private static @Nullable Guild guild;
	private static @Nullable WebhookClient chatHook;
	private static @Nullable WebhookClient chatThreadHook;
	private static final String AVATAR_BASE_URL = "https://minotar.net/helm/";

	/**
	 * Setups a Discord bot
	 *
	 * @param plugin plugin instance
	 */
	public static void setupBot(Socializer plugin) {
		if (isEnabled()) return;

		ConfigurationSection config = plugin.getConfig().getConfigurationSection("discord");
		if (config == null) {
			Socializer.logger().error("Discord configuration section is missing");
			return;
		}
		if (!config.getBoolean("run-bot")) return;

		if (!config.contains("token") || config.getString("token", "").isBlank()) {
			Socializer.logger().error("Discord token is missing or empty");
			return;
		}
		if (!config.contains("guild") || config.getLong("guild") == 0) {
			Socializer.logger().error("Discord guild ID is missing or invalid");
			return;
		}
		if (!config.contains("channels.server-chat") || config.getLong("channels.server-chat") == 0) {
			Socializer.logger().error("Discord server chat channel ID is missing or invalid");
			return;
		}

		try {
			discordBot = JDABuilder.createDefault(config.getString("token"), GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.build();
			discordBot.awaitReady();
			guild = discordBot.getGuildById(config.getLong("guild"));
			if (guild == null) {
				Socializer.logger().error("Failed to find Discord guild with ID {}", config.getLong("guild"));
				shutdown();
				return;
			}

			String chatHookUrl = config.getString("chat-hook", "");
			if (!chatHookUrl.isBlank()) {
				chatHook = WebhookClient.withUrl(chatHookUrl);
				long threadId = config.getLong("chat-hook-thread-id", -1L);
				if (threadId != -1L)
					chatThreadHook = WebhookClient.withUrl(chatHookUrl).onThread(threadId);
			}

			DiscordUtil.setupUtils(plugin);
			Discorder.setupDatabase();
		} catch (InterruptedException e) {
			Socializer.logger().error("Failed to setup Discord bot", e);
		}
	}

	/**
	 * Shuts down Discord bot and cleans up resources
	 */
	public static synchronized void shutdown() {
		if (!isEnabled()) return;

		DiscordUtil.updateStatus(":coffee: The server is temporary offline.");

		assert discordBot != null;
		discordBot.shutdown();
		if (chatHook != null)
			chatHook.close();

		if (chatThreadHook != null)
			chatThreadHook.close();

		discordBot = null;
		guild = null;
		chatHook = null;
		chatThreadHook = null;
	}

	/**
	 * Sends a webhook message into the chat channel
	 *
	 * @param nickname sender's nickname
	 * @param uuid sender's in-game uuid
	 * @param builder message
	 */
	public static void sendMessage(String nickname, @Nullable String uuid, WebhookMessageBuilder builder, boolean thread) {
		if (thread && chatThreadHook == null) return;

		// Setting chat hook's avatar seems to break things :(
		String avatarParam = uuid != null ? uuid : URLEncoder.encode(nickname, StandardCharsets.UTF_8);
		builder.setAvatarUrl(AVATAR_BASE_URL + avatarParam);
		try {
			if (thread)
				chatThreadHook.send(builder.build());
			else if (chatHook != null)
				chatHook.send(builder.build());
		} catch (Exception e) {
			Socializer.logger().error("Failed to send Discord message", e);
		}
	}

	/**
	 * Checks whether the Discord bot is enabled
	 *
	 * @return whether the Discord bot is enabled
	 */
	public static boolean isEnabled() {
		return discordBot != null;
	}

	/**
	 * Gets the running JDA instance
	 *
	 * @return the JDA instance
	 * @see #isEnabled()
	 * @throws NullPointerException if not enabled
	 */
	public static JDA getDiscordBot() {
		return Objects.requireNonNull(discordBot);
	}

	/**
	 * Gets the server Discord guild
	 *
	 * @return the server guild
	 * @throws NullPointerException if not enabled
	 */
	public static Guild getGuild() {
		return Objects.requireNonNull(guild);
	}

}
