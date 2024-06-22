package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Custom dismount logic for possessed mobs
 */
public class PossessedDismount implements Listener {

	private final Set<UUID> cooldowns = new HashSet<>();

	@EventHandler(ignoreCancelled = true)
	public void onDismount(@NotNull EntityDismountEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getDismounted() instanceof LivingEntity vehicle)) return;
		if (!PossessingPlayer.isPossessingSoft(player)) return;

		if (event.isCancellable()) {
			if (cooldowns.contains(player.getUniqueId())) {
				event.setCancelled(true);
			} else if (PossessingPlayer.hasSoul(vehicle)) {
				event.setCancelled(true);
				cooldowns.add(player.getUniqueId());
				Requiem.scheduler().sync(() -> cooldowns.remove(player.getUniqueId()), 20L);
			}

			if (event.isCancelled()) {
				// Play safe, check if still mounted tick later
				Requiem.scheduler().sync(() -> {
					if (!PossessingPlayer.isPossessingSoft(player)) return;

					LivingEntity possessed = PossessingPlayer.getPossessed(player);
					if (possessed == vehicle) return;

					PossessingPlayer.stopPossessing(player, possessed, false);
					GhostyPlayer.markGhost(player);
				}, 1L);
				return;
			}
		}

		PossessingPlayer.stopPossessing(player, vehicle, false);
		GhostyPlayer.markGhost(player);
	}

}
