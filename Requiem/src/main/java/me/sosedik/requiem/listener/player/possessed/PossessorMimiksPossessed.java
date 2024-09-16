package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEffect(@NotNull EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;

		Player rider = entity.getRider();
		if (rider == null) return;
		if (!PossessingPlayer.isPossessing(rider)) return;
		if (PossessingPlayer.getPossessed(rider) != entity) return;

		PotionEffect newEffect = event.getNewEffect();
		if (newEffect == null) {
			PotionEffect oldEffect = event.getOldEffect();
			if (oldEffect != null)
				rider.removePotionEffect(oldEffect.getType());
			return;
		}

		Requiem.scheduler().sync(() -> rider.addPotionEffect(newEffect.withDuration(newEffect.getDuration() - 1)), 1L);
	}

}
