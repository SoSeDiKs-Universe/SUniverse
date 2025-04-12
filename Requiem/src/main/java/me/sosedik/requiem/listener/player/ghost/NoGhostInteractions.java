package me.sosedik.requiem.listener.player.ghost;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import me.sosedik.requiem.feature.GhostyPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Prevent ghosts from interacting with a world
 */
@NullMarked
public class NoGhostInteractions implements Listener {

	@EventHandler(priority = EventPriority.LOWEST) // Interact has special cancellation
	public void onInteractWorld(PlayerInteractEvent event) {
		if (event.getPlayer().getGameMode().isInvulnerable()) return;
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInteractEntity(PlayerInteractAtEntityEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteractWorld(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode().isInvulnerable()) return;
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSleep(PlayerBedEnterEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(PlayerAttemptPickupItemEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(PlayerPickupArrowEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(PlayerPickupExperienceEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

}
