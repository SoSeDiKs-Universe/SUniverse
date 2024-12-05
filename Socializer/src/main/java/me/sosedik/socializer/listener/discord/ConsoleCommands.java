package me.sosedik.socializer.listener.discord;

import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.util.DiscordUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Run commands in console from Discord chat
 */
public class ConsoleCommands extends ListenerAdapter {

	private final long consoleChatId;

	public ConsoleCommands(@NotNull Socializer plugin) {
		consoleChatId = plugin.getConfig().getLong("discord.channels.console-chat", -1L);
		if (this.consoleChatId > 0L) DiscordBot.getDiscordBot().addEventListener(this);
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		if (event.getChannel().getIdLong() != consoleChatId) return;

		Member member = event.getMember();
		if (member == null) return;
		if (!member.getRoles().contains(DiscordUtil.getOwnerRole())) return;

		String text = event.getMessage().getContentRaw();
		if (!text.startsWith("/")) return;

		String command = text.substring(1);
		Socializer.scheduler().sync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
	}

}
