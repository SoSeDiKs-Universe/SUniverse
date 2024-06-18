package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.Requiem;
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
		if (!(event.getDismounted() instanceof LivingEntity riding)) return;
		if (!PossessingPlayer.isPossessingSoft(player)) return;

		if (event.isCancellable()) {
			if (cooldowns.contains(player.getUniqueId())) {
				event.setCancelled(true);
				return;
			}
			if (riding.getVehicle() != null) {
				event.setCancelled(true);
				riding.leaveVehicle();
				cooldowns.add(player.getUniqueId());
				Requiem.scheduler().sync(() -> cooldowns.remove(player.getUniqueId()), 20L);
				return;
			}
			if (PossessingPlayer.hasSoul(riding)) {
				event.setCancelled(true);
				return;
			}
		}

		PossessingPlayer.stopPossessing(player, riding, false);
	}

}
