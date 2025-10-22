package me.sosedik.requiem.listener.player.possessed;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.jspecify.annotations.NullMarked;

/**
 * Possessor mimics actions of possessed mob
 */
@NullMarked
public class PossessorMimicsPossessed implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEffect(EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;

		Player rider = entity.getRider();
		if (rider == null) return;
		if (!PossessingPlayer.isPossessing(rider)) return;

		PotionEffect newEffect = event.getNewEffect();
		if (newEffect == null) {
			PotionEffect oldEffect = event.getOldEffect();
			if (oldEffect != null)
				rider.removePotionEffect(oldEffect.getType());
			return;
		}

		Requiem.scheduler().sync(() -> rider.addPotionEffect(newEffect.withDuration(newEffect.getDuration() - 1)), 1L);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onTick(ServerTickEndEvent event) {
		Bukkit.getOnlinePlayers().forEach(player -> {
			LivingEntity possessed = PossessingPlayer.getPossessed(player);
			if (possessed == null) return;

			PossessingPlayer.migrateInvFromEntity(player, possessed);
		});
	}

}
