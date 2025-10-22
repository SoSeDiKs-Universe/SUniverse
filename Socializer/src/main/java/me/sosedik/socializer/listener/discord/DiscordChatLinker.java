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

	public DiscordChatLinker(Socializer plugin, MinecraftChatRenderer chatRenderer) {
		this.chatRenderer = chatRenderer;
		serverChat = DiscordBot.getDiscordBot().getTextChannelById(plugin.getConfig().getLong("discord.channels.server-chat"));
		DiscordBot.getDiscordBot().addEventListener(this);
		runCleanupTask();
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
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
					webhookMessageBuilder.addEmbeds(new WebhookEmbedBuilder().setImageUrl(attachment.getUrl()).setColor(3158326).build());
				} else
					webhookMessageBuilder.addFile(attachment.getProxy().downloadToFile(new File(attachment.getFileName())).get());
			} catch (Exception ignored) {
				Socializer.logger().warn("Could not download the file! {} : {}", attachment.getFileName(), attachment.getUrl());
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
			webhookMessageBuilder.addEmbeds(new WebhookEmbedBuilder().setImageUrl(imageUrl).setColor(3158326).build());
		}

		String[] lines = rawMessage.split("\n");
		webhookMessageBuilder.append(DiscordUtil.formatGameMessage(DISCORD_EMOTE, lines[0]));
		for (int i = 1; i < lines.length; i++)
			webhookMessageBuilder.append("\n       " + DiscordUtil.LARGE_EMOJI_DISABLER).append(lines[i]);

		try {
			DiscordBot.sendMessage(nickname, null, webhookMessageBuilder, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.delete().queue();

		String logMessage = gameMessage.isBlank() ? "[some embeds]" : MiniMarkdown.markdownToMini(gameMessage);
		logMessage = "[D] " + nickname + " said: " + logMessage;
		Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(logMessage));

		chatRenderer.sendBukkitMessage(nickname, gameMessage);
	}

	private WebhookEmbed convertEmbed(MessageEmbed embed) {
		var embedBuilder = new WebhookEmbedBuilder()
				.setDescription(embed.getDescription())
				.setTimestamp(embed.getTimestamp());
		var author = embed.getAuthor();
		if (author != null)
			embedBuilder.setAuthor(new WebhookEmbed.EmbedAuthor(author.getName() == null ? "Bot" : author.getName(), author.getIconUrl(), author.getUrl()));

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
		String selectSql = "SELECT `UUID` FROM `" + Discorder.DATABASE_NAME + "` WHERE `DiscordId` = '" + id + "';";
		try (Connection connection = Socializer.database().openConnection();
			 PreparedStatement ps = connection.prepareStatement(selectSql)) {
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) return "";

			UUID uuid = UUID.fromString(rs.getString("UUID"));
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
			String name = offlinePlayer.getName();
			return name == null ? "" : name;
		} catch (SQLException | IllegalArgumentException ex) {
			ex.printStackTrace();
			return "";
		}
	}

	private static void runCleanupTask() {
		long cleanupInterval = 60 * 20L;
		Utilizer.scheduler().async(CHAT_USERS::cleanUp, cleanupInterval, cleanupInterval);
	}

}
