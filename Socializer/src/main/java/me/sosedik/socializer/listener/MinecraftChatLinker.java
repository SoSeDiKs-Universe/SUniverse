package me.sosedik.socializer.listener;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.util.DiscordUtil;
import me.sosedik.uglychatter.api.chat.FancyMessageRenderer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Sends messages from Minecraft's chat into messenger chats
 */
@NullMarked
public class MinecraftChatLinker implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(AsyncChatEvent event) {
		if (DiscordBot.isEnabled()) Socializer.scheduler().async(() -> sendDiscordMessage(event.getPlayer(), FancyMessageRenderer.getRawInput(event.message())));
	}

	private void sendDiscordMessage(Player who, String message) {
		message = DiscordUtil.parseMentions(message);

		String nickname = who.getName();
		var builder = new WebhookMessageBuilder()
			.setUsername(nickname)
			.setContent(DiscordUtil.formatGameMessage(who, message));

		DiscordBot.sendMessage(who.getName(), who.getUniqueId().toString(), builder, false);
	}

}
