package me.sosedik.uglychatter.api.chat;

import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import static me.sosedik.resourcelib.util.SpacingUtil.iconize;
import static me.sosedik.utilizer.api.message.Mini.combined;
import static me.sosedik.utilizer.api.message.Mini.component;
import static me.sosedik.utilizer.api.message.Mini.raw;

@NullMarked
public class PlayerDisplayFormatter {

	private PlayerDisplayFormatter() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Gets the formated content with the ability to PM the player
	 *
	 * @param content text content
	 * @param color content color
	 * @param player player to PM to
	 * @param receiver component viewer
	 * @return formatted component
	 */
	public static Component getPMComponent(String content, @Nullable TextColor color, Player player, Player receiver) {
		return Component.text().content(content).color(color)
				.hoverEvent(combined(iconize(Messenger.messenger(receiver), asIcon("✉"), "chat.pm", raw("player", player.getName()))))
				.clickEvent(ClickEvent.suggestCommand("/w " + player.getName() + " "))
				.build();
	}

	/**
	 * Gets the formated playtime count
	 *
	 * @param sender the player to format against
	 * @param receiver component viewer
	 * @return formatted component
	 */
	public static Component getPlayTime(OfflinePlayer sender, Player receiver) {
		var messenger = Messenger.messenger(receiver);
		int ticks = sender.getStatistic(Statistic.PLAY_ONE_MINUTE);
		ticks /= 20; // seconds
		ticks /= 60; // minutes
		if (ticks <= 300)
			return combined(iconize(messenger, asIcon("⌚"), "chat.playtime", component("playtime", messenger.getMessage("chat.playtime.m")), raw("time", ticks)));
		ticks /= 60;
		return combined(iconize(messenger, asIcon("⌚"), "chat.playtime", component("playtime", messenger.getMessage("chat.playtime.h")), raw("time", ticks)));
	}

	/**
	 * Gets the formated deaths count
	 *
	 * @param sender the player to format against
	 * @param receiver component viewer
	 * @return formatted component
	 */
	public static Component getDeaths(OfflinePlayer sender, Player receiver) {
		int deaths = sender.getStatistic(Statistic.DEATHS);
		return combined(asIcon("☠"), Component.space(), Messenger.messenger(receiver).getMessage("chat.deaths", raw("deaths", deaths)));
	}

	private static Component asIcon(String icon) {
		return combined(SpacingUtil.getSpacing(1), Mini.asIcon(Component.text(icon)));
	}

}
