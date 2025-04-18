package me.sosedik.miscme.listener.entity;

import com.destroystokyo.paper.event.entity.CreeperIgniteEvent;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Creeper's explosion radius scales from health
 */
@NullMarked
public class DynamicCreeperExplosion implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onIgnite(CreeperIgniteEvent event) {
		Creeper creeper = event.getEntity();
		int radius = (int) (3 * (creeper.getHealth() / creeper.getMaxHealth()));
		creeper.setExplosionRadius(radius);
	}

}
