package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.feature.GhostyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Ghosts don't starve of hunger or air
 */
public class GhostsDontStarveOrChoke implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onStarve(@NotNull FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onAirChange(@NotNull EntityAirChangeEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getAmount() > player.getRemainingAir()) return;
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(true);
	}

}
