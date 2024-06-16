package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.feature.GhostyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

/**
 * New players should start as ghosts
 */
public class StartAsGhost implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(@NotNull PlayerJoinEvent event) {
		Player player = event.getPlayer();
		GhostyPlayer.markGhost(player);
	}

}
