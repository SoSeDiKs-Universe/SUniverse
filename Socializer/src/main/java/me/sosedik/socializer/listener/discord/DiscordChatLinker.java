package me.sosedik.socializer.listener.discord;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.discord.Discorder;
import me.sosedik.socializer.util.DiscordUtil;
import me.sosedik.socializer.util.MinecraftChatRenderer;
import me.sosedik.uglychatter.api.markdown.MiniMarkdown;
import me.sosedik.utilizer.Utilizer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Sends messages from Discord's chat into other supported chats
 */
@NullMarked
public class DiscordChatLinker extends ListenerAdapter {

	private static final Pattern IMAGE_URL = Pattern.compile("(http(s?):)([/.\\w\\s:-])*\\.(?:jpg|gif|png)(\\S*)");
	private static final String DISCORD_EMOTE = "<:discord:971000408124293151>";
	private static final int EMBED_COLOR = 3158326;
	private static final String DEFAULT_AUTHOR_NAME = "Bot";
	private static final long CLEANUP_INTERVAL_TICKS = 60 * 20L;
	private static final long MAX_ATTACHMENT_SIZE = 20 * 1024 * 1024; // 20MB
	private static final long DOWNLOAD_TIMEOUT_SECONDS = 30;
	private static final LoadingCache<Long, String> CHAT_USERS = CacheBuilder.newBuilder()
			.expireAfterAccess(5, TimeUnit.MINUTES)
			.build(
				new CacheLoader<>() {
					public String load(Long id) {
						return fetchUserById(id);
					}
				}
			);

	private static TextChannel serverChat;

	private final MinecraftChatRenderer chatRenderer;

	/**
	 * Constructs a new DiscordChatLinker to handle Discord chat events.
	 *
	 * @param plugin the plugin instance
	 * @param chatRenderer the renderer for sending messages to Minecraft
	 */
	public DiscordChatLinker(Socializer plugin, MinecraftChatRenderer chatRenderer) {
		this.chatRenderer = chatRenderer;
		serverChat = DiscordBot.getDiscordBot().getTextChannelById(plugin.getConfig().getLong("discord.channels.server-chat"));
		if (serverChat == null) {
			Socializer.logger().error("Failed to find Discord server chat channel with ID {}", plugin.getConfig().getLong("discord.channels.server-chat"));
			return;
		}
		DiscordBot.getDiscordBot().addEventListener(this);
		runCleanupTask();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (serverChat == null) return;
		if (event.getAuthor().isBot()) return;
		if (!event.isFromGuild()) return;
		if (event.getChannel().getIdLong() != serverChat.getIdLong()) return;

		Member member = event.getMember();
		if (member == null) return;

		Message message = event.getMessage();
		String nickname = CHAT_USERS.getUnchecked(member.getIdLong());
		if (nickname.isEmpty()) nickname = member.getEffectiveName();
		String rawMessage = message.getContentRaw();
		String gameMessage = message.getContentDisplay();

		var webhookMessageBuilder = new WebhookMessageBuilder();
		webhookMessageBuilder.setUsername(nickname);

		// Parse attachments
		for (Message.Attachment attachment : message.getAttachments()) {
			try {
				if (attachment.isImage()) {
					// ToDo: in-game images
					webhookMessageBuilder.addEmbeds(new WebhookEmbedBuilder().setImageUrl(attachment.getUrl()).setColor(EMBED_COLOR).build());
				} else if (attachment.getSize() <= MAX_ATTACHMENT_SIZE) {
					webhookMessageBuilder.addFile(attachment.getProxy().downloadToFile(new File(attachment.getFileName())).get(DOWNLOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS));
				} else {
					Socializer.logger().warn("Attachment {} is too large ({} bytes), skipping", attachment.getFileName(), attachment.getSize());
				}
			} catch (Exception e) {
				Socializer.logger().warn("Could not download the file! {} : {}", attachment.getFileName(), attachment.getUrl(), e);
			}
		}

		// Parse embeds
		for (MessageEmbed embed : message.getEmbeds())
			webhookMessageBuilder.addEmbeds(convertEmbed(embed));

		// Parse image links in message
		var matcher = IMAGE_URL.matcher(rawMessage);
		while (matcher.find()) {
			String imageUrl = matcher.group();
			// ToDo: in-game images
			rawMessage = rawMessage.replace(imageUrl, "[image]");
			gameMessage = gameMessage.replace(imageUrl, "[image]");
			webhookMessageBuilder.addEmbeds(new WebhookEmbedBuilder().setImageUrl(imageUrl).setColor(EMBED_COLOR).build());
		}

		processAttachments(message, webhookMessageBuilder);
		processEmbeds(message, webhookMessageBuilder);
		String[] processedMessages = processImageLinks(rawMessage, gameMessage, webhookMessageBuilder);
		rawMessage = processedMessages[0];
		gameMessage = processedMessages[1];

		buildAndSendWebhook(nickname, rawMessage, webhookMessageBuilder);
		message.delete().queue();

		logAndSendToGame(nickname, gameMessage);
	}

	private void processAttachments(Message message, WebhookMessageBuilder webhookMessageBuilder) {
		for (Message.Attachment attachment : message.getAttachments()) {
			try {
				if (attachment.isImage()) {
					// ToDo: in-game images
					webhookMessageBuilder.addEmbeds(new WebhookEmbedBuilder().setImageUrl(attachment.getUrl()).setColor(EMBED_COLOR).build());
				} else if (attachment.getSize() <= MAX_ATTACHMENT_SIZE) {
					webhookMessageBuilder.addFile(attachment.getProxy().downloadToFile(new File(attachment.getFileName())).get(DOWNLOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS));
				} else {
					Socializer.logger().warn("Attachment {} is too large ({} bytes), skipping", attachment.getFileName(), attachment.getSize());
				}
			} catch (Exception e) {
				Socializer.logger().warn("Could not download the file! {} : {}", attachment.getFileName(), attachment.getUrl(), e);
			}
		}
	}

	private void processEmbeds(Message message, WebhookMessageBuilder webhookMessageBuilder) {
		for (MessageEmbed embed : message.getEmbeds())
			webhookMessageBuilder.addEmbeds(convertEmbed(embed));
	}

	private String[] processImageLinks(String rawMessage, String gameMessage, WebhookMessageBuilder webhookMessageBuilder) {
		// Parse image links in message
		var matcher = IMAGE_URL.matcher(rawMessage);
		while (matcher.find()) {
			String imageUrl = matcher.group();
			// ToDo: in-game images
			rawMessage = rawMessage.replace(imageUrl, "[image]");
			gameMessage = gameMessage.replace(imageUrl, "[image]");
			webhookMessageBuilder.addEmbeds(new WebhookEmbedBuilder().setImageUrl(imageUrl).setColor(EMBED_COLOR).build());
		}
		return new String[]{rawMessage, gameMessage};
	}

	private void buildAndSendWebhook(String nickname, String rawMessage, WebhookMessageBuilder webhookMessageBuilder) {
		String[] lines = rawMessage.split("\n");
		webhookMessageBuilder.append(DiscordUtil.formatGameMessage(DISCORD_EMOTE, lines[0]));
		for (int i = 1; i < lines.length; i++)
			webhookMessageBuilder.append("\n       " + DiscordUtil.LARGE_EMOJI_DISABLER).append(lines[i]);

		try {
			DiscordBot.sendMessage(nickname, null, webhookMessageBuilder, false);
		} catch (Exception e) {
			Socializer.logger().error("Failed to send Discord message", e);
		}
	}

	private void logAndSendToGame(String nickname, String gameMessage) {
		Socializer.instance().getServer().getScheduler().runTask(Socializer.instance(), () -> {
			String logMessage = gameMessage.isBlank() ? "[some embeds]" : MiniMarkdown.markdownToMini(gameMessage);
			logMessage = "[D] " + nickname + " said: " + logMessage;
			Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(logMessage));

			chatRenderer.sendBukkitMessage(nickname, gameMessage);
		});
	}

	private WebhookEmbed convertEmbed(MessageEmbed embed) {
		var embedBuilder = new WebhookEmbedBuilder()
				.setDescription(embed.getDescription())
				.setTimestamp(embed.getTimestamp());
		var author = embed.getAuthor();
		if (author != null)
			embedBuilder.setAuthor(new WebhookEmbed.EmbedAuthor(author.getName() == null ? DEFAULT_AUTHOR_NAME : author.getName(), author.getIconUrl(), author.getUrl()));

		var footer = embed.getFooter();
		if (footer != null)
			embedBuilder.setFooter(new WebhookEmbed.EmbedFooter(footer.getText() == null ? "" : footer.getText(), footer.getIconUrl()));

		String title = embed.getTitle();
		if (title != null)
			embedBuilder.setTitle(new WebhookEmbed.EmbedTitle(title, embed.getUrl()));

		// ToDo: in-game image

		embedBuilder.setColor(embed.getColorRaw());

		var image = embed.getImage();
		if (image != null)
			embedBuilder.setImageUrl(image.getUrl());

		var thumbnail = embed.getThumbnail();
		if (thumbnail != null)
			embedBuilder.setThumbnailUrl(thumbnail.getUrl());

		return embedBuilder.build();
	}

	private static String fetchUserById(long id) {
		String selectSql = "SELECT `UUID` FROM `" + Discorder.DATABASE_NAME + "` WHERE `DiscordId` = ?;";
		try (Connection connection = Socializer.database().openConnection();
			 PreparedStatement ps = connection.prepareStatement(selectSql)) {
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) return "";

			UUID uuid = UUID.fromString(rs.getString("UUID"));
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			String name = offlinePlayer.getName();
			return name == null ? "" : name;
		} catch (SQLException | IllegalArgumentException ex) {
			Socializer.logger().error("Failed to fetch user by Discord ID: {}", id, ex);
			return "";
		}
	}

	private static void runCleanupTask() {
		Utilizer.scheduler().async(CHAT_USERS::cleanUp, CLEANUP_INTERVAL_TICKS, CLEANUP_INTERVAL_TICKS);
	}

}
