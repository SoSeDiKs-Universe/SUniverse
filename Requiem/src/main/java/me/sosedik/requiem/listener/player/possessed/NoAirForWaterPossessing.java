package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Disables air requirement while possessing water mobs
 */
@NullMarked
public class NoAirForWaterPossessing implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAirChange(EntityAirChangeEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getAmount() >= player.getRemainingAir()) return;
		if (!PossessingPlayer.isPossessing(player)) return;
		if (!EntityUtil.isWaterMob(PossessingPlayer.getPossessed(player))) return;

		if (player.getRemainingAir() == player.getMaximumAir()) {
			event.setCancelled(true);
		}

		event.setAmount(player.getMaximumAir());
	}

}
