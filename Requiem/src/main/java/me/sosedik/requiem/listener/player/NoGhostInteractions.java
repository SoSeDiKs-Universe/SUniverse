package me.sosedik.requiem.listener.player;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import me.sosedik.requiem.feature.GhostyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Prevent ghosts from interacting with world
 */
public class NoGhostInteractions implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteractWorld(@NotNull PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInteractEntity(@NotNull PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInteractEntity(@NotNull PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSleep(@NotNull PlayerBedEnterEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDrop(@NotNull PlayerDropItemEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(@NotNull PlayerAttemptPickupItemEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(@NotNull PlayerPickupArrowEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(@NotNull PlayerPickupExperienceEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

}
