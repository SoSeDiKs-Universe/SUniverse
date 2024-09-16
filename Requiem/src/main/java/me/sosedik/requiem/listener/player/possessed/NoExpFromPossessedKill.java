package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Mobs killed by possessing players should not drop exp
 */
public class NoExpFromPossessedKill implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onDeath(@NotNull EntityDeathEvent event) {
		if (event.getDroppedExp() == 0) return;

		Player killer = EntityUtil.getDamager(event.getEntity());
		if (killer == null) return;
		if (!PossessingPlayer.isPossessing(killer)) return;

		event.setDroppedExp(0);
	}

}
