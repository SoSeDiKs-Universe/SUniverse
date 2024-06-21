package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.Bukkit;
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

		Player killer = EntityUtil.getDamager(event.getEntity()); // TODO if possessed kills entity, properly set the killer player (& award stats)
		Bukkit.broadcastMessage("Killer: " + (killer == null ? "null" : killer.getName()));
		if (killer == null) return;
		if (!PossessingPlayer.isPossessing(killer)) return;

		event.setDroppedExp(0);
	}

}
