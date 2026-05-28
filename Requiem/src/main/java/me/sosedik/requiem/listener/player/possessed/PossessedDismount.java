package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Custom dismount logic for possessed mobs
 */
@NullMarked
public class PossessedDismount implements Listener {

	private final Set<UUID> cooldowns = new HashSet<>();
	private final Set<UUID> weirdLimboStateNeedsDelayedCheck = new HashSet<>();

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!this.weirdLimboStateNeedsDelayedCheck.contains(player.getUniqueId())) return;
		if (PossessingPlayer.getPossessed(player) != null) return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onDismount(EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getDismounted() instanceof LivingEntity vehicle)) return;
		if (!PossessingPlayer.isPossessingSoft(player)) return;

		if (!vehicle.isValid()) {
			PossessingPlayer.stopPossessing(player, vehicle, false, false);
			GhostyPlayer.markGhost(player);
			return;
		}

		UUID playerUuid = player.getUniqueId();
		if (event.isCancellable()) {
			if (this.cooldowns.contains(playerUuid)) {
				event.setCancelled(true);
			} else if (PossessingPlayer.isPossessable(vehicle)) {
				event.setCancelled(true);
				this.cooldowns.add(playerUuid);
				Requiem.scheduler().sync(() -> this.cooldowns.remove(playerUuid), 20L);
			}
		}

		// Play safe, check if still mounted a tick later
		// Even in non-cancellable cases (e.g., teleports between worlds) the player may still end up riding the entity
		if (!this.weirdLimboStateNeedsDelayedCheck.add(playerUuid)) return;

		Requiem.scheduler().sync(() -> {
			this.weirdLimboStateNeedsDelayedCheck.remove(playerUuid);
			if (!PossessingPlayer.isPossessingSoft(player)) return;

			LivingEntity possessed = PossessingPlayer.getPossessed(player);
			if (possessed != null) return;

			boolean validPossessed = vehicle.isValid() && !vehicle.hasRider();
			PossessingPlayer.stopPossessing(player, validPossessed ? vehicle : null, false, validPossessed);
			GhostyPlayer.markGhost(player);
		}, 1L);
	}

}
