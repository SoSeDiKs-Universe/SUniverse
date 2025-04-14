package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.api.event.player.PlayerPossessedTransformEvent;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Entity transformations keep the possessing player
 */
@NullMarked
public class TransformationsKeepPossessor implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTransform(EntityTransformEvent event) {
		if (!(event.getTransformedEntity() instanceof LivingEntity transformed)) return;
		if (!(event.getEntity() instanceof LivingEntity entity)) return;

		Player rider = entity.getRider();
		if (rider == null) return;
		if (!PossessingPlayer.isPossessing(rider)) return;

		if (PossessingPlayer.isResurrected(entity))
			PossessingPlayer.markResurrected(transformed);

		PossessingPlayer.migrateStatsToEntity(rider, transformed);
		PossessingPlayer.startPossessing(rider, transformed);

		new PlayerPossessedTransformEvent(rider, transformed, entity).callEvent();
	}

}
