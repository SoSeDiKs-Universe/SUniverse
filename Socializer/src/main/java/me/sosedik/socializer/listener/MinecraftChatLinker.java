package me.sosedik.socializer.listener;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.util.DiscordUtil;
import me.sosedik.uglychatter.api.chat.FancyMessageRenderer;
import me.sosedik.uglychatter.api.chat.FancyRendererTag;
import me.sosedik.uglychatter.api.mini.placeholder.EmojiPlaceholder;
import net.kyori.adventure.text.Component;
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
		if (!DiscordBot.isEnabled()) return;

		Player player = event.getPlayer();
		Component message = event.message();
		String rawMessage = FancyMessageRenderer.getRawInput(message, FancyRendererTag.SKIP_MARKDOWN);
		Socializer.scheduler().async(() -> sendDiscordMessage(player, rawMessage));
	}

	private void sendDiscordMessage(Player who, String message) {
		message = EmojiPlaceholder.applyReverseMappings(message);
		message = DiscordUtil.parseMentions(message);

		String nickname = who.getName();
		var builder = new WebhookMessageBuilder()
			.setUsername(nickname)
			.setContent(DiscordUtil.formatGameMessage(who, message));

		DiscordBot.sendMessage(who.getName(), who.getUniqueId().toString(), builder, false);
	}

}
