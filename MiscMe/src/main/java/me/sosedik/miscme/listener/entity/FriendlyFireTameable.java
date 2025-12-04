package me.sosedik.miscme.listener.entity;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Don't hurt your loved ones :(
 */
@NullMarked
public class FriendlyFireTameable implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDamage(PrePlayerAttackEntityEvent event) {
		if (!(event.getAttacked() instanceof Tameable tameable)) return;

		Player damager = event.getPlayer();
		if (damager.isSneaking()) return;
		if (!damager.getUniqueId().equals(tameable.getOwnerUniqueId())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDamage(ProjectileHitEvent event) {
		if (!(event.getHitEntity() instanceof Tameable tameable)) return;
		// AbstractArrow includes Trident
		if (!(event.getEntity() instanceof AbstractArrow projectile)) return;
		if (!(projectile.getShooter() instanceof Player shooter)) return;
		if (!shooter.getUniqueId().equals(tameable.getOwnerUniqueId())) return;

		event.setCancelled(true);
	}

}
