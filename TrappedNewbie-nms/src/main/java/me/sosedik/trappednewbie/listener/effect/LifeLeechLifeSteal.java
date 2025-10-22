package me.sosedik.trappednewbie.listener.effect;

import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Life leech effect handler
 */
@NullMarked
public class LifeLeechLifeSteal implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHit(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player damager)) return;
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (!damager.hasPotionEffect(TrappedNewbieEffects.LIFE_LEECH)) return;

		double damageAmount = event.getFinalDamage();
		double healthStealAmount = Math.min(6, damageAmount / 4);
		if (healthStealAmount < 1) return;

		entity.emitSound(Sound.ENTITY_PHANTOM_BITE, 1F, 1F);
		damager.setHealth(Math.min(damager.getMaxHealth(), damager.getHealth() + healthStealAmount));
	}

}
