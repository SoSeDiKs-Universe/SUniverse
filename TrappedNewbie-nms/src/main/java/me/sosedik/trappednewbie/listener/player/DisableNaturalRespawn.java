package me.sosedik.trappednewbie.listener.player;

import com.destroystokyo.paper.event.player.PlayerSetSpawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Beds and anchors are no longer responsible for respawn
 */
@NullMarked
public class DisableNaturalRespawn implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onRespawnSet(PlayerSetSpawnEvent event) {
		if (!isNatural(event.getCause())) return;

		event.setCancelled(true);
	}

	private boolean isNatural(PlayerSetSpawnEvent.Cause cause) {
		return cause == PlayerSetSpawnEvent.Cause.BED || cause == PlayerSetSpawnEvent.Cause.RESPAWN_ANCHOR;
	}

}
