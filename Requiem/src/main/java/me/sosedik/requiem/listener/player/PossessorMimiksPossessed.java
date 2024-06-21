package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Possessor mimiks actions of possessed mob
 */
public class PossessorMimiksPossessed implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCombust(@NotNull EntityCombustEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;

		Player rider = entity.getRider();
		if (rider == null) return;
		if (!PossessingPlayer.isPossessing(rider)) return;
		if (PossessingPlayer.getPossessed(rider) != entity) return;

		rider.setFireTicks((int) (event.getDuration() * 20));
	}

}
