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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DiscordBot {

	private DiscordBot() {
		throw new IllegalStateException("Utility class");
	}

	private static JDA discordBot;
	private static Guild guild;
	private static WebhookClient chatHook;

	/**
	 * Setups a Discord bot
	 *
	 * @param plugin plugin instance
	 */
	public static void setupBot(@NotNull Socializer plugin) {
		if (discordBot != null) return;

		ConfigurationSection config = plugin.getConfig().getConfigurationSection("discord");
		if (config == null) return;
		if (!config.getBoolean("run-bot")) return;

		try {
			discordBot = JDABuilder.createDefault(config.getString("token"), GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
				.setMemberCachePolicy(MemberCachePolicy.ALL)
				.build();
			discordBot.awaitReady();
			guild = discordBot.getGuildById(config.getLong("guild"));
			chatHook = WebhookClient.withUrl(config.getString("chat-hook", ""));

			DiscordUtil.setupUtils(plugin);
			Discorder.setupDatabase();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shuts down Discord bot
	 */
	public static void shutdown() {
		if (!isEnabled()) return;

		DiscordUtil.updateStatus(":coffee: The server is temporary offline.");
		discordBot.shutdown();
	}

	/**
	 * Sends a webhook message into the chat channel
	 *
	 * @param nickname sender's nickname
	 * @param uuid sender's in-game uuid
	 * @param builder message
	 */
	public static void sendMessage(@NotNull String nickname, @Nullable String uuid, @NotNull WebhookMessageBuilder builder) {
		// Setting chat hook's avatar seems to break things :(
		builder.setAvatarUrl("https://minotar.net/helm/" + Objects.requireNonNullElse(uuid, nickname));
		chatHook.send(builder.build());
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
	 */
	public static @NotNull JDA getDiscordBot() {
		return discordBot;
	}

	/**
	 * Gets the server Discord guild
	 *
	 * @return the server guild
	 */
	public static @NotNull Guild getGuild() {
		return guild;
	}

}
