package me.sosedik.resourcelib.listener.player;

import me.sosedik.resourcelib.feature.HudMessenger;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Loads and saves hud messenger
 */
@NullMarked
public class LoadSaveHudMessengerOnJoinLeave implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		HudMessenger.of(event.getPlayer()).displayMessage(Component.empty());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		HudMessenger.removePlayer(event.getPlayer());
	}

}
