package me.sosedik.requiem.listener.player;

import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Entity transformations keep the possessing player
 */
public class TransformationsKeepPossessor implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTransform(@NotNull EntityTransformEvent event) {
		if (!(event.getTransformedEntity() instanceof LivingEntity transformed)) return;
		if (!(event.getEntity() instanceof LivingEntity entity)) return;

		Player rider = entity.getRider();
		if (rider == null) return;
		if (!PossessingPlayer.isPossessing(rider)) return;

		PossessingPlayer.migrateStatsToEntity(rider, transformed);
		PossessingPlayer.startPossessing(rider, transformed);
	}

}
