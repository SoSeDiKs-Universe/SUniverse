package me.sosedik.fancymotd.listener;

import me.sosedik.fancymotd.Pinger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Invalidates Pinger data on join and refreshes it on quit
 */
public class PingerRefresher implements Listener {

	@EventHandler
	public void onPreJoin(@NotNull AsyncPlayerPreLoginEvent event) {
		String ip = event.getAddress().getHostAddress();
		Pinger.removePinger(ip);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLeave(@NotNull PlayerQuitEvent event) {
		Pinger.addPinger(event.getPlayer());
	}

}
