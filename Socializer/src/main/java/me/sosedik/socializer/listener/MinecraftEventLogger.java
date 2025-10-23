package me.sosedik.socializer.listener;

import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import me.sosedik.socializer.discord.DiscordBot;
import me.sosedik.socializer.util.DiscordUtil;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import net.kyori.adventure.text.event.HoverEvent;
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
		sendDiscordMessage(event.getPlayer(), ":skull_crossbones:", ChatUtil.getPlainText(message));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAdvancement(PlayerAdvancementDoneEvent event) {
		Component message = event.message();
		if (message == null) return;

		message = GlobalTranslator.render(message, Locale.US);
		StringBuilder sb = new StringBuilder(ChatUtil.getPlainText(message));
		if (message instanceof TranslatableComponent translatable) {
			goThoughChildren(translatable, sb);
		} else {
			goThoughChildren(message, sb);
		}
		sendDiscordMessage(event.getPlayer(), ":medal:", sb.toString());
	}

	private void goThoughChildren(TranslatableComponent message, StringBuilder sb) {
		for (TranslationArgument text : message.arguments()) {
			Component component = text.asComponent();
			HoverEvent<?> hoverEvent = component.hoverEvent();
			if (hoverEvent == null || !HoverEvent.Action.SHOW_TEXT.equals(hoverEvent.action())) {
				goThoughChildren(component, sb);
				continue;
			}

			addAdvLore(((HoverEvent<Component>) hoverEvent).value(), sb);
		}
	}

	private void goThoughChildren(Component message, StringBuilder sb) {
		for (Component text : message.children()) {
			HoverEvent<?> hoverEvent = text.hoverEvent();
			if (hoverEvent == null || !HoverEvent.Action.SHOW_TEXT.equals(hoverEvent.action())) {
				goThoughChildren(text, sb);
				continue;
			}

			addAdvLore(((HoverEvent<Component>) hoverEvent).value(), sb);
		}
	}

	private void addAdvLore(Component message, StringBuilder sb) {
		String plain = ChatUtil.getPlainText(message);
		String[] split = plain.split("\n");
		if (split.length < 2) return;

		for (int i = 1; i < split.length; i++)
			sb.append("\n> ").append(split[i]);
	}

	private void sendDiscordMessage(Player who, String emote, String message) {
		message = DiscordUtil.parseMentions(message);

		String nickname = who.getName();
		var builder = new WebhookMessageBuilder()
			.setUsername(nickname)
			.setContent(DiscordUtil.formatGameMessage(emote, message));

		DiscordBot.sendMessage(who.getName(), who.getUniqueId().toString(), builder, true);
	}

}
