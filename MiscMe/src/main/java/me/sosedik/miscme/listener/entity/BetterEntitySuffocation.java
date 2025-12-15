package me.sosedik.miscme.listener.entity;

import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Prevent suffocation damage for mounts that are mostly inside air
 */
@NullMarked
public class BetterEntitySuffocation implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSuffocation(EntityDamageEvent event) {
		if (!EntityDamageEvent.DamageCause.SUFFOCATION.equals(event.getCause())) return;

		Entity entity = event.getEntity();
		if (LocationUtil.isTrulySolid(entity, entity.getLocation().getBlock())) return;

		event.setCancelled(true);
	}

}
