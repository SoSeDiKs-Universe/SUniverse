package me.sosedik.miscme.listener.projectile;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BurningLitsProjectiles implements Listener {

	@EventHandler
	public void onShootWhileBurning(ProjectileLaunchEvent event) {
		var projectile = event.getEntity();
		if (projectile.getFireTicks() > 0) return;
		if (!(projectile.getShooter() instanceof LivingEntity shooter)) return;
		if (shooter.getFireTicks() <= 0) return;

		projectile.setFireTicks(projectile.getMaxFireTicks());
	}

}
