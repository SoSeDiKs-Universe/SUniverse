package me.sosedik.miscme.listener.block;

import io.papermc.paper.event.player.PlayerOpenSignEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Don't edit signs on place
 */
public class DontEditSignsOnPlace implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onOpen(@NotNull PlayerOpenSignEvent event) {
		if (event.getCause() != PlayerOpenSignEvent.Cause.PLACE) return;

		event.setCancelled(true);
	}

}
