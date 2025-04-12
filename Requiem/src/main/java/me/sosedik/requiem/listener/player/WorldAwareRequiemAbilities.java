package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.jspecify.annotations.NullMarked;

/**
 * World-aware ghost/possessed abilities
 */
@NullMarked
public class WorldAwareRequiemAbilities implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFlyToggle(PlayerToggleFlightEvent event) {
		if (event.isFlying()) return;

		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(true);
		GhostyPlayer.checkCanGhostFly(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (GhostyPlayer.isGhost(player)) {
			GhostyPlayer.checkCanGhostFly(player);
			GhostyPlayer.checkCanHoldGhostItems(player);
		} else if (PossessingPlayer.isPossessing(player)) {
			PossessingPlayer.checkPossessedExtraItems(player);
		}
	}

}
