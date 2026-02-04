package me.sosedik.trappednewbie.listener.player;

import me.sosedik.moves.api.event.PlayerStartCrawlingEvent;
import me.sosedik.requiem.feature.GhostyPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * No parkour for ghosts
 */
@NullMarked
public class NoGhostParkour implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onCrawl(PlayerStartCrawlingEvent event) {
		if (GhostyPlayer.isGhost(event.getPlayer()))
			event.setCancelled(true);
	}

}
