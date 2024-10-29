package me.sosedik.miscme.listener.entity;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * There's a chance to set entity on fire
 * when attacking while burning
 */
public class BurningSpreadsWhenAttacking implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onAttackWhileBurning(@NotNull EntityDamageByEntityEvent event) {
		if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
		if (!(event.getDamager() instanceof LivingEntity damager)) return;
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (damager.getFireTicks() <= 0) return;
		if (damager.getEquipment() == null) return;
		if (damager.getEquipment().getItemInMainHand().getType() != Material.AIR) return;

		if (entity.getFireTicks() >= 100) return;
		if (Math.random() > 0.3) return;
		if (entity.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) return;

		entity.setFireTicks(100);
	}

}
