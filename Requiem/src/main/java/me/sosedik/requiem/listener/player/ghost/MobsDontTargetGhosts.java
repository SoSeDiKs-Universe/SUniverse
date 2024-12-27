package me.sosedik.requiem.listener.player.ghost;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Mobs do not target ghosts and possessors
 */
public class MobsDontTargetGhosts implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onTarget(@NotNull EntityTargetLivingEntityEvent event) {
		if (!(event.getTarget() instanceof Player player)) return;

		if (GhostyPlayer.isGhost(player)) {
			event.setCancelled(true);
			return;
		}

		if (!PossessingPlayer.isPossessing(player)) return;

		event.setCancelled(true);
		if (!(event.getEntity() instanceof Mob entity)) return;
		if (EntityUtil.getCausingDamager(entity) != player) return;

		event.setTarget(PossessingPlayer.getPossessed(player));
		event.setCancelled(false);
	}

}
