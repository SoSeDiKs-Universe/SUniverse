package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
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

	@EventHandler(ignoreCancelled = true)
	public void onDismount(EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getDismounted() instanceof LivingEntity vehicle)) return;
		if (!PossessingPlayer.isPossessingSoft(player)) return;

		if (event.isCancellable()) {
			if (this.cooldowns.contains(player.getUniqueId())) {
				event.setCancelled(true);
			} else if (PossessingPlayer.isPossessable(vehicle)) {
				event.setCancelled(true);
				this.cooldowns.add(player.getUniqueId());
				Requiem.scheduler().sync(() -> this.cooldowns.remove(player.getUniqueId()), 20L);
			}
		}

		// Play safe, check if still mounted a tick later
		// Even in non-cancellable cases (e.g., teleports between worlds) the player may still end up riding the entity
		Requiem.scheduler().sync(() -> {
			if (!PossessingPlayer.isPossessingSoft(player)) return;

			LivingEntity possessed = PossessingPlayer.getPossessed(player);
			if (possessed != null) return;

			PossessingPlayer.stopPossessing(player, vehicle, false);
			GhostyPlayer.markGhost(player);
		}, 1L);
	}

}
