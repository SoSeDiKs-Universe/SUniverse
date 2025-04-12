package me.sosedik.requiem.listener.player.ghost;

import me.sosedik.requiem.feature.GhostyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Ghosts can't sprint
 */
@NullMarked
public class GhostsDontSprint implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSprintToggle(PlayerToggleSprintEvent event) {
		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;

		boolean sprinting = event.isSprinting();
		float speed = sprinting ? 0.1F : 0.2F;
		player.setWalkSpeed(speed);
		player.setFlySpeed(speed);
	}

}
