package me.sosedik.miscme.listener.player;

import me.sosedik.miscme.MiscMe;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

/**
 * Players are forced to run when burning
 */
@NullMarked
public class BurningForcesToRun implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBurn(EntityCombustEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		MiscMe.scheduler().sync(task -> {
			if (!player.isValid()) return true;
			if (player.getFireTicks() <= 0) return true;
			if (player.isInvulnerable()) return false;
			if (player.isSprinting()) return false;
			if (!player.isOnGround()) return false;
			if (player.isInsideVehicle()) return false;
			if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) return true;
			if (player.isInLava()) return true;

			Location loc = player.getLocation();
			loc.setPitch(0);
			player.setVelocity(loc.getDirection());
			return false;
		}, 5L, 4L);
	}

}
