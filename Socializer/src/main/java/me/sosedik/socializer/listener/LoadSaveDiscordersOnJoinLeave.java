package me.sosedik.socializer.listener;

import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.discord.Discorder;
import me.sosedik.socializer.util.DiscordUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Load and save Discorders upon join/quit
 */
public class LoadSaveDiscordersOnJoinLeave implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(@NotNull PlayerJoinEvent event) {
		Player player = event.getPlayer();
		var discorder = Discorder.getDiscorder(player);
		Socializer.scheduler().async(() -> {
			if (!discorder.hasDiscord()) return;
			DiscordUtil.modifyNickname(discorder.getDiscordId(), event.getPlayer().getName());
		}, 40L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(@NotNull PlayerQuitEvent event) {
		Discorder.removePlayer(event.getPlayer());
	}

}
