package me.sosedik.miscme.listener.item;

import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Hitting entities with burning items will set them on fire
 */
@NullMarked
public class FireHitLitsEntities implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onAttackWithTorch(EntityDamageByEntityEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
		if (!(event.getDamager() instanceof LivingEntity damager)) return;
		if (damager.getEquipment() == null) return;
		if (!ItemUtil.isBurningItem(damager.getEquipment().getItemInMainHand())) return;

		Entity entity = event.getEntity();
		if (entity.getFireTicks() > 100) return;

		entity.setFireTicks(100);
	}

}
