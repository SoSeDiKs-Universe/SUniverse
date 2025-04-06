package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Mobs killed by possessing players should not drop exp
 */
@NullMarked
public class NoExpFromPossessedKill implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		if (event.getDroppedExp() == 0) return;

		if (!(EntityUtil.getCausingDamager(event.getEntity()) instanceof Player killer)) return;
		if (!PossessingPlayer.isPossessing(killer)) return;

		event.setDroppedExp(0);
	}

}
