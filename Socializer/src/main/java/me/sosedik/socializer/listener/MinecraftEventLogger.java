package me.sosedik.socializer.listener;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.util.DiscordUtil;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
public class MinecraftEventLogger implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Component message = event.deathMessage();
		if (message == null) message = event.originalDeathMessage();
		if (message == null) return;

		message = GlobalTranslator.render(message, Locale.US);
		sendDiscordMessage(event.getPlayer(), ChatUtil.getPlainText(message));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAdvancement(PlayerAdvancementDoneEvent event) {
		Component message = event.message();
		if (message == null) return;

		message = GlobalTranslator.render(message, Locale.US);
		sendDiscordMessage(event.getPlayer(), ChatUtil.getPlainText(message));
	}

	private void sendDiscordMessage(Player who, String message) {
		message = DiscordUtil.parseMentions(message);

		String nickname = who.getName();
		var builder = new WebhookMessageBuilder()
			.setUsername(nickname)
			.setContent(DiscordUtil.formatGameMessage(who, message));

		DiscordBot.sendMessage(who.getName(), who.getUniqueId().toString(), builder, true);
	}

}
