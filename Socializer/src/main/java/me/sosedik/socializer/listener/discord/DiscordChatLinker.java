package me.sosedik.socializer.listener.discord;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.util.DiscordUtil;
import me.sosedik.socializer.util.MinecraftChatRenderer;
import me.sosedik.uglychatter.api.markdown.MiniMarkdown;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Sends messages from Discord's chat into other supported chats
 */
public class DiscordChatLinker extends ListenerAdapter {

	private static final Pattern IMAGE_URL = Pattern.compile("(http(s?):)([/.\\w\\s:-])*\\.(?:jpg|gif|png)(\\S*)");
	private static final String DISCORD_EMOTE = "<:discord:971000408124293151>";

	private static TextChannel serverChat;

	private final MinecraftChatRenderer chatRenderer;

	public DiscordChatLinker(@NotNull Socializer plugin, MinecraftChatRenderer chatRenderer) {
		this.chatRenderer = chatRenderer;
		serverChat = DiscordBot.getDiscordBot().getTextChannelById(plugin.getConfig().getLong("discord.channels.server-chat"));
		DiscordBot.getDiscordBot().addEventListener(this);
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		if (!event.isFromGuild()) return;
		if (event.getChannel().getIdLong() != serverChat.getIdLong()) return;

		Member member = event.getMember();
		if (member == null) return;

		Message message = event.getMessage();
		String nickname = member.getEffectiveName();
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
			DiscordBot.sendMessage(nickname, null, webhookMessageBuilder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.delete().queue();

		String logMessage = gameMessage.isBlank() ? "[some embeds]" : MiniMarkdown.markdownToMini(gameMessage);
		logMessage = "[D] " + nickname + " said: " + logMessage;
		Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(logMessage));

		chatRenderer.sendBukkitMessage(nickname, gameMessage);
	}

	private @NotNull WebhookEmbed convertEmbed(@NotNull MessageEmbed embed) {
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

}