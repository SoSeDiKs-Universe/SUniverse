package me.sosedik.fancymotd.listener;

import me.sosedik.fancymotd.Pinger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Invalidates Pinger data on join and refreshes it on quit
 */
@NullMarked
public class PingerRefresher implements Listener {

	@EventHandler
	public void onPreJoin(AsyncPlayerPreLoginEvent event) {
		String ip = event.getAddress().getHostAddress();
		Pinger.removePinger(ip);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLeave(PlayerQuitEvent event) {
		Pinger.addPinger(event.getPlayer());
	}

}
