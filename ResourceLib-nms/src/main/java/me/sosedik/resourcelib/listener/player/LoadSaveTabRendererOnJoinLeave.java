package me.sosedik.resourcelib.listener.player;

import me.sosedik.resourcelib.feature.TabRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Loads and saved hud messenger
 */
@NullMarked
public class LoadSaveTabRendererOnJoinLeave implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		TabRenderer.of(event.getPlayer()).run();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		TabRenderer.removePlayer(event.getPlayer());
	}

}
