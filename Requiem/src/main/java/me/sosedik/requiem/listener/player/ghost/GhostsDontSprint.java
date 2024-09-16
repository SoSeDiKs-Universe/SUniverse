package me.sosedik.requiem.listener.player.ghost;

import me.sosedik.requiem.feature.GhostyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Ghosts can't sprint and always fly
 */
public class GhostsDontSprint implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSprintToggle(@NotNull PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;

		boolean sprinting = event.isSprinting();
		float speed = sprinting ? 0.1F : 0.2F;
		player.setWalkSpeed(speed);
		player.setFlySpeed(speed);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFlyToggle(@NotNull PlayerToggleFlightEvent event) {
		if (event.isFlying()) return;

		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldChange(@NotNull PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;

		player.setAllowFlight(true);
		player.setFlying(true);
	}

}
