package me.sosedik.utilizer.listener.item;

import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Automatically releasing items on use
 */
public class AutoReleasingItems implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onLoad(EntityLoadCrossbowEvent event) {
		if (!UtilizerTags.AUTO_RELEASING.isTagged(event.getCrossbow().getType())) return;

		if (UtilizerTags.AUTO_RELEASING_NO_CONSUME.isTagged(event.getCrossbow().getType())) {
			event.setCancelled(true);
			event.setConsumeItem(false);
		}
		event.getEntity().completeUsingActiveItem();
	}

}
