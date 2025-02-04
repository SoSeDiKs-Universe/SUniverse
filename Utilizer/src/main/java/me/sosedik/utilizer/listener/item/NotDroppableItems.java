package me.sosedik.utilizer.listener.item;

import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Some items shouldn't be dropped
 */
@NullMarked
public class NotDroppableItems implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(ItemSpawnEvent event) {
		if (UtilizerTags.NOT_DROPPABLE.isTagged(event.getEntity().getItemStack().getType()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(EntityDeathEvent event) {
		event.getDrops().removeIf(item -> UtilizerTags.NOT_DROPPABLE.isTagged(item.getType()));
	}

}
