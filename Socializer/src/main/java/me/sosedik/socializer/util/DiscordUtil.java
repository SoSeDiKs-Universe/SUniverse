package me.sosedik.socializer.util;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.discord.Discorder;
import me.sosedik.socializer.listener.discord.NoNicknameChange;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.listener.misc.ExtraChatTabSuggestions;
import me.sosedik.utilizer.util.ChatUtil;
import me.sosedik.utilizer.util.EntityUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordUtil {

	private DiscordUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Invisible symbol used to disable large emoji render in Discord chat
	 */
	public static final char LARGE_EMOJI_DISABLER = '\u200E';

	private static final String OVERWORLD_EMOTE = "<:overworld:971000028040659014>";
	private static final String CUSTOM_WORLD_EMOTE = "<:space:1029768302152646736>";
	private static final String NETHER_EMOTE = "<:nether:971002411470688296>";
	private static final String THE_END_EMOTE = "<:the_end:971002551900180570>";
	private static final String UNDER_WATER_EMOTE = "<:under_water:971002883086618644>";
	private static final String NIGHT_EMOTE = "<:night:1029773504305954856>";

	// @ + any non-space character + one or more characters UNLESS end OR one of those in set
	private static final Pattern MENTION_PATTERN = Pattern.compile("@(\\S+?(?=$|[\\s,.!?&…;:¡¿‽⸘⁇⁈⸮‼⁉]))");

	private static TextChannel floodChat;
	private static boolean updateStatus = true;
	private static @Nullable Message onlineMessage;
	private static Role verifiedRole;
	private static Role staffRole;
	private static Role ownerRole;

	public static void setupUtils(@NotNull Socializer plugin) {
		ConfigurationSection config = plugin.getConfig().getConfigurationSection("discord");
		assert config != null;

		JDA discordBot = DiscordBot.getDiscordBot();
		floodChat = discordBot.getTextChannelById(config.getLong("channels.flood-chat"));
		TextChannel onlineChannel = discordBot.getTextChannelById(config.getLong("channels.online-chat"));
		assert onlineChannel != null;

		updateStatus = config.getBoolean("update-status", true);

		long onlineMessageId = config.getLong("messages.online", -1L);
		if (updateStatus && onlineMessageId != -1L) onlineMessage = onlineChannel.retrieveMessageById(config.getLong("messages.online")).complete();

		verifiedRole = DiscordBot.getDiscordBot().getRoleById(config.getLong("roles.verified"));
		staffRole = DiscordBot.getDiscordBot().getRoleById(config.getLong("roles.staff"));
		ownerRole = DiscordBot.getDiscordBot().getRoleById(config.getLong("roles.owner"));

		ExtraChatTabSuggestions.addTabSuggestion("@Admin");
		ExtraChatTabSuggestions.addTabSuggestion("@Staff");
		ExtraChatTabSuggestions.addTabSuggestion("@Owner");

		updateStatus();
	}

	/**
	 * Parses Discord mentions in the message
	 *
	 * @param message message
	 * @return message with mentions
	 */
	public static @NotNull String parseMentions(@NotNull String message) {
		List<String> mentions = null;
		Matcher matcher = MENTION_PATTERN.matcher(message);
		while (matcher.find()) {
			if (mentions == null) mentions = new ArrayList<>();
			mentions.add(matcher.group(1));
		}
		if (mentions == null) return message;
		Map<String, Map.Entry<Component, String>> mentionMappings = getMentions(mentions);
		for (Map.Entry<String, Map.Entry<Component, String>> entry : mentionMappings.entrySet())
			message = message.replace("@" + entry.getKey(), entry.getValue().getValue());
		return message;
	}

	/**
	 * Finds Discord mentions in the message
	 *
	 * @param message message
	 * @return found mentions
	 */
	public static @Nullable Map<String, Map.Entry<Component, String>> getMentions(@NotNull Component message) {
		List<String> mentions = null;
		Matcher matcher = MENTION_PATTERN.matcher(ChatUtil.getPlainText(message));
		while (matcher.find()) {
			if (mentions == null) mentions = new ArrayList<>();
			mentions.add(matcher.group(1));
		}
		return mentions == null ? null : getMentions(mentions);
	}

	// <in_message, <name, mention>>
	private static @NotNull Map<String, Map.Entry<Component, String>> getMentions(@NotNull List<String> mentions) {
		Map<String, Map.Entry<Component, String>> mentionMappings = new HashMap<>();

		// Custom aliases
		for (String mention : mentions) {
			if (mention.equalsIgnoreCase("admin"))
				mentionMappings.put(mention, Map.entry(Component.text("@Admin", NamedTextColor.RED), staffRole.getAsMention()));
			if (mention.equalsIgnoreCase("staff"))
				mentionMappings.put(mention, Map.entry(Component.text("@Staff", NamedTextColor.RED), staffRole.getAsMention()));
			if (mention.equalsIgnoreCase("owner"))
				mentionMappings.put(mention, Map.entry(Component.text("@Owner", NamedTextColor.GOLD), ownerRole.getAsMention()));
		}

		// Server roles
		for (Role role : DiscordBot.getGuild().getRoles()) {
			for (String mention : mentions) {
				if (mentionMappings.containsKey(mention)) continue;
				if (role.getName().equalsIgnoreCase(mention)) {
					Color color = role.getColor();
					mentionMappings.put(mention, Map.entry(Component.text("@", color == null ? NamedTextColor.YELLOW : TextColor.color(color.getRGB())), role.getAsMention()));
				}
			}
		}

		// Discord users
		DiscordBot.getGuild().findMembers(member -> {
			for (String mention : mentions) {
				if (mentionMappings.containsKey(mention)) continue;
				if (member.getEffectiveName().equalsIgnoreCase(mention)) {
					TextColor color = TextColor.fromHexString("#7289da");
					String nickname = "@" + member.getEffectiveName();
					Component display = Component.text(nickname, color)
						.hoverEvent(Component.text("Discord"))
						.clickEvent(ClickEvent.suggestCommand(nickname));
					mentionMappings.put(mention, Map.entry(display, member.getAsMention()));
					return true;
				}
			}
			return false;
		}).get();

		return mentionMappings;
	}

	/**
	 * Formats game message for sending to the chat channel
	 *
	 * @param message message
	 * @return formatted message
	 */
	public static @NotNull String formatGameMessage(@NotNull Player sender, @NotNull String message) {
		return formatGameMessage(getEmote(sender), message);
	}

	/**
	 * Formats game message for sending to the chat channel
	 *
	 * @param emote emote prefix
	 * @param message message
	 * @return formatted message
	 */
	public static @NotNull String formatGameMessage(@NotNull String emote, @NotNull String message) {
		return emote + " " + LARGE_EMOJI_DISABLER + message;
	}

	private static @NotNull String getEmote(@NotNull Player sender) {
		if (sender.isUnderWater()) return UNDER_WATER_EMOTE;
		if (!EntityUtil.canSee(sender)) return NIGHT_EMOTE;

		World.Environment environment = sender.getWorld().getEnvironment();
		if (environment == World.Environment.NETHER) return NETHER_EMOTE;
		if (environment == World.Environment.THE_END) return THE_END_EMOTE;
		if (environment == World.Environment.CUSTOM) return CUSTOM_WORLD_EMOTE;

		return OVERWORLD_EMOTE;
	}

	/**
	 * Modifies user's nickname on the Discord server
	 *
	 * @param id Discord user id
	 * @param nickname nickname
	 */
	public static void modifyNickname(long id, @NotNull String nickname) {
		DiscordBot.getGuild().retrieveMemberById(id)
			.queue(member -> modifyNickname(member, nickname),
				error -> {
					if (!(error instanceof ErrorResponseException e)) {
						error.printStackTrace();
						return;
					}
					if (e.getErrorCode() == ErrorResponse.UNKNOWN_MEMBER.getCode()
							|| e.getErrorCode() == ErrorResponse.UNKNOWN_USER.getCode()) {
						Socializer.logger().info("Couldn't find user {} (id: {}), removing", nickname, id);
						Socializer.scheduler().sync(() -> {
							Player player = Bukkit.getPlayerExact(nickname);
							if (player != null)
								player.performCommand("discord --unverify --sure");
						});
					}
				}
			);
	}

	/**
	 * Modifies user's nickname on the Discord server
	 *
	 * @param member server member
	 * @param nickname nickname
	 */
	public static void modifyNickname(@NotNull Member member, @Nullable String nickname) {
		if (nickname != null) NoNicknameChange.whitelist(member.getIdLong(), nickname);
		if (PermissionUtil.canInteract(Objects.requireNonNull(DiscordBot.getGuild().getMember(DiscordBot.getDiscordBot().getSelfUser())), member))
			member.modifyNickname(nickname).queue();
	}

	/**
	 * Updates server status message with currently online players
	 */
	public static synchronized void updateStatus() {
		if (!updateStatus) return;

		JDA discordBot = DiscordBot.getDiscordBot();
		if (Bukkit.hasWhitelist()) {
			discordBot.getPresence().setPresence(OnlineStatus.IDLE, Activity.playing("Maintenance"));
			updateStatus(":heart_exclamation: Server is under maintenance.");
			return;
		}

		int size = Bukkit.getOnlinePlayers().size();
		int max = Bukkit.getMaxPlayers();
		discordBot.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(size + " / " + max));
		if (size == 0) {
			updateStatus(":zero: Players online.");
			return;
		}

		var sb = new StringBuilder();
		for (char ch : String.valueOf(size).toCharArray()) {
			sb.append(switch (ch) {
				case '1' -> ":one:";
				case '2' -> ":two:";
				case '3' -> ":three:";
				case '4' -> ":four:";
				case '5' -> ":five:";
				case '6' -> ":six:";
				case '7' -> ":seven:";
				case '8' -> ":eight:";
				case '9' -> ":nine:";
				default -> ":zero:";
			});
			sb.append(' ');
		}
		sb.append("Player");
		if (size != 1)
			sb.append('s');
		sb.append(" online: ");

		for (Player player : Bukkit.getOnlinePlayers()) {
			var discorder = Discorder.getDiscorder(player);
			sb.append("**");
			if (discorder.hasDiscord())
				sb.append("<@").append(discorder.getDiscordId()).append(">");
			else
				sb.append(player.getName().replace("_", "\\_"));
			sb.append("**, ");
		}
		sb.setLength(sb.length() - 2);
		sb.append(".");

		updateStatus(sb.toString());
	}

	/**
	 * Updates server status message
	 *
	 * @param description message
	 */
	public static synchronized void updateStatus(@NotNull String description) {
		if (onlineMessage != null) onlineMessage.editMessageEmbeds(
			new EmbedBuilder()
				.setColor(8027237)
				.setTitle("Server status")
				.setDescription(description)
				.setThumbnail("https://raw.githubusercontent.com/SoSeDiK-Universe/Graphics-Place/main/ServerOnlineThumbnail.png")
				.setFooter("⛏ We wish you a nice game!")
				.build()
		).complete();
	}

	/**
	 * Adds verified role to the user and announced it
	 *
	 * @param id Discord user id
	 * @param uuid player's in-game uuid
	 * @param nickname player's nickname
	 */
	public static void announceVerify(long id, @NotNull String uuid, @NotNull String nickname) {
		DiscordBot.getGuild().retrieveMemberById(id).queue(member -> {
			DiscordBot.getGuild().addRoleToMember(member, verifiedRole).queue();
			floodChat.createWebhook(nickname + "-verify").queue(hook -> {
				if (hook.getToken() == null) return;
				var message = new WebhookEmbedBuilder()
					.setTitle(new WebhookEmbed.EmbedTitle("User " + nickname + " has verified their account!", "https://sosedik.com"))
					.setDescription(member.getAsMention() + " is now a " + verifiedRole.getAsMention())
					.setFooter(new WebhookEmbed.EmbedFooter("Thank you for playing on SoSeDiK's Universe!", "https://github.com/twitter/twemoji/raw/master/assets/72x72/1f49c.png"))
					.setThumbnailUrl("https://minotar.net/armor/body/" + uuid)
					.setColor(3066993)
					.build();
				var builder = new WebhookMessageBuilder()
					.setUsername(nickname)
					.addEmbeds(message);
				try {
					hook.getManager().setAvatar(Icon.from(new URI("https://minotar.net/helm/" + uuid).toURL().openStream())).queue();
				} catch (IOException ignored) {
					builder.setAvatarUrl("https://minotar.net/helm/" + nickname);
				} catch (URISyntaxException e) {
					throw new RuntimeException(e);
				}
				Socializer.scheduler().async(() -> {
					try (var client = WebhookClient.withId(hook.getIdLong(), hook.getToken())) {
						client.send(builder.build());
					} finally {
						hook.delete().queueAfter(1L, TimeUnit.SECONDS);
					}
				}, 10L);
			});
			modifyNickname(member, nickname);
		});
	}

	/**
	 * Removes verified role from the user
	 *
	 * @param id user id
	 */
	public static void unverify(long id) {
		if (id <= 0) return;

		DiscordBot.getGuild().retrieveMemberById(id)
			.queue(
				member -> DiscordBot.getGuild().removeRoleFromMember(member, verifiedRole).queue(),
				error -> {
					if (error instanceof ErrorResponseException e) {
						if (e.getErrorCode() == ErrorResponse.UNKNOWN_MEMBER.getCode()) return;
						if (e.getErrorCode() == ErrorResponse.UNKNOWN_USER.getCode()) return;
					}
					error.printStackTrace();
				}
			);
	}

	public static void sendGameProgressResetRequest(@NotNull Player player, @NotNull Discorder discorder) {
		if (!discorder.hasDiscord()) return;

		long id = discorder.getDiscordId();
		DiscordBot.getGuild().retrieveMemberById(id).queue(member -> {
			member.getUser().openPrivateChannel().queue(chat -> {
				var message = new MessageCreateBuilder()
					.mentionUsers(id)
					.setContent("‼️ Do you want to reset your in-game progress?")
					.addActionRow(Button.danger("reset_progress", "Reset"));
				chat.sendMessage(message.build()).queue();
			}, error -> Messenger.messenger(player).sendMessage("discord.pm.error"));
		}, error -> {
			Messenger.messenger(player).sendMessage("discord.pm.error");
			if (error instanceof ErrorResponseException e) {
				if (e.getErrorCode() == ErrorResponse.UNKNOWN_MEMBER.getCode()) return;
				if (e.getErrorCode() == ErrorResponse.UNKNOWN_USER.getCode()) return;
			}
			error.printStackTrace();
		});
	}

	/**
	 * Gets the role used for verified players
	 *
	 * @return the verified player role
	 */
	public static @NotNull Role getVerifiedRole() {
		return verifiedRole;
	}

	/**
	 * Gets the role used for owner perms
	 *
	 * @return the owner role
	 */
	public static @NotNull Role getOwnerRole() {
		return ownerRole;
	}

}
