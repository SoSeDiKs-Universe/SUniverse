package me.sosedik.utilizer.listener.player;

import me.sosedik.utilizer.Utilizer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;

/**
 * Updating item translations on locale change
 */
public class UpdateInventoryOnLocaleChange implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLocaleChange(PlayerLocaleChangeEvent event) {
		Utilizer.scheduler().sync(() -> event.getPlayer().updateInventory(), 1L);
	}

}
