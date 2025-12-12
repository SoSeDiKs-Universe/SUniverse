package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jspecify.annotations.NullMarked;

/**
 * No trading when possessed
 */
@NullMarked
public class LimitedPossessedTrades implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onTrade(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof AbstractVillager)) return;

		Player player = event.getPlayer();
		if (PossessingPlayer.canTrade(player)) return;

		event.setCancelled(true);
	}

}
