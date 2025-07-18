package me.sosedik.requiem.listener.player.possessed;

import com.destroystokyo.paper.event.entity.EntityZapEvent;
import me.sosedik.requiem.Requiem;
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
	public void onTransform(EntityZapEvent event) {
		if (!(event.getTransformedEntity() instanceof LivingEntity transformed)) return;
		if (!(event.getEntity() instanceof LivingEntity entity)) return;

		onTransform(transformed, entity);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTransform(EntityTransformEvent event) {
		if (!(event.getTransformedEntity() instanceof LivingEntity transformed)) return;
		if (!(event.getEntity() instanceof LivingEntity entity)) return;

		onTransform(transformed, entity);
	}

	private void onTransform(LivingEntity transformed, LivingEntity entity) {
		Player rider = entity.getRider();
		if (rider == null) return;
		if (!PossessingPlayer.isPossessingSoft(rider)) return;

		if (PossessingPlayer.isResurrected(entity))
			PossessingPlayer.markResurrected(transformed);

		PossessingPlayer.migrateStatsToEntity(rider, transformed);
		if (transformed.isValid()) transformed(rider, transformed, entity);
		else Requiem.scheduler().sync(() -> transformed(rider, transformed, entity), 1L); // TODO Find a way to remove delay?
	}

	private void transformed(Player possessor, LivingEntity transformed, LivingEntity entity) {
		PossessingPlayer.startPossessing(possessor, transformed);
		new PlayerPossessedTransformEvent(possessor, transformed, entity).callEvent();
	}

}
