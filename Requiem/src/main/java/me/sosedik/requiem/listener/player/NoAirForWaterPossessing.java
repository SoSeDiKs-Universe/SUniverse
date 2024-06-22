package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Turtle;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Disables air requirement while possessing water mobs
 */
public class NoAirForWaterPossessing implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAirChange(@NotNull EntityAirChangeEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getAmount() >= player.getRemainingAir()) return;
		if (!PossessingPlayer.isPossessing(player)) return;
		if (!isWaterMob(PossessingPlayer.getPossessed(player))) return;

		if (player.getRemainingAir() == player.getMaximumAir()) {
			event.setCancelled(true);
		}

		event.setAmount(player.getMaximumAir());
	}

	private boolean isWaterMob(@Nullable LivingEntity entity) {
		return entity instanceof WaterMob || entity instanceof Turtle || entity instanceof Drowned;
	}

}
