package me.sosedik.miscme.listener.projectile;

import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BurningLitsProjectiles implements Listener {

	@EventHandler
	public void onShootWhileBurning(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		if (projectile.isImmuneToFire()) return;
		if (!(projectile.getShooter() instanceof LivingEntity shooter)) return;
		if (shooter.getFireTicks() <= 0) return;

		projectile.setFireTicks(Integer.MAX_VALUE);
		if (projectile instanceof AbstractArrow arrow && !(arrow instanceof Trident))
			arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
	}

}
