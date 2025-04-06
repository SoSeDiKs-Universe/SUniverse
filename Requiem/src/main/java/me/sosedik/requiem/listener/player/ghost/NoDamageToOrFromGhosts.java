package me.sosedik.requiem.listener.player.ghost;

import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import me.sosedik.requiem.feature.GhostyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Ghosts can't take or deal physical damage
 */
@NullMarked
public class NoDamageToOrFromGhosts implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onHurt(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!GhostyPlayer.isGhost(player)) return;

		event.setDamage(0);
		if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onHurt(EntityCombustEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamage(PrePlayerAttackEntityEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.setCancelled(true);
	}

}
