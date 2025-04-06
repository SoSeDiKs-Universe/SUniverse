package me.sosedik.miscme.listener.block;

import io.papermc.paper.event.player.PlayerOpenSignEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Don't edit signs on place
 */
@NullMarked
public class DontEditSignsOnPlace implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onOpen(PlayerOpenSignEvent event) {
		if (event.getCause() != PlayerOpenSignEvent.Cause.PLACE) return;

		event.setCancelled(true);
	}

}
