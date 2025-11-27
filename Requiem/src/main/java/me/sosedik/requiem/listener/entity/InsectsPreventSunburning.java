package me.sosedik.requiem.listener.entity;

import me.sosedik.kiterino.event.entity.EntitySunburnEvent;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.List;

/**
 * Mobs like silverfishes prevent sunburning by being "hats"
 */
public class InsectsPreventSunburning implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onSunburn(EntitySunburnEvent event) {
		LivingEntity entity = event.getEntity();
		List<Entity> passengers = entity.getPassengers();
		if (passengers.isEmpty()) return;
		if (!isInsect(passengers.getFirst())) return;

		event.setCancelled(true);
	}

	private boolean isInsect(Entity entity) {
		return entity instanceof Silverfish || entity instanceof Endermite;
	}

}
