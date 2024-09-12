package me.sosedik.resourcelib.listener.player;

import me.sosedik.resourcelib.feature.TabRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Loads and saved hud messenger
 */
public class LoadSaveTabRendererOnJoinLeave implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(@NotNull PlayerJoinEvent event) {
		TabRenderer.of(event.getPlayer()).run();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(@NotNull PlayerQuitEvent event) {
		TabRenderer.removePlayer(event.getPlayer());
	}

}
