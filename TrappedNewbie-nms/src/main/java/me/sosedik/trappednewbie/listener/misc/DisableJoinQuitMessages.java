package me.sosedik.trappednewbie.listener.misc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Because they are annoying
 */
@NullMarked
public class DisableJoinQuitMessages implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		event.joinMessage(null);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLeave(PlayerQuitEvent event) {
		event.quitMessage(null);
	}

}
