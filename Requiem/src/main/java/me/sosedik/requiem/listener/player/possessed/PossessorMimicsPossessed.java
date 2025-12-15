package me.sosedik.requiem.listener.player.possessed;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Possessor mimics actions of possessed mob
 */
@NullMarked
public class PossessorMimicsPossessed implements Listener {

	private static final Set<UUID> DAMAGER_CACHE = new HashSet<>();

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

			PossessingPlayer.migrateInvFromEntity(player, possessed, false);
		});
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamageAnotherEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof LivingEntity damager)) return;
		if (!(event.getEntity() instanceof LivingEntity damaged)) return;

		Player rider = damager.getRider();
		if (rider == null) return;
		if (PossessingPlayer.getPossessed(rider) != damager) return;

		event.setCancelled(true);

		DamageSource damageSource = event.getDamageSource();
		var source = DamageSource.builder(damageSource.getDamageType());

		Entity directEntity = damageSource.getDirectEntity();
		if (directEntity != null) source.withDirectEntity(directEntity == damager ? rider : directEntity);

		Entity causingEntity = damageSource.getCausingEntity();
		if (causingEntity != null) source.withDirectEntity(causingEntity == damager ? rider : causingEntity);

		Location damageLocation = damageSource.getDamageLocation();
		if (damageLocation != null) source.withDamageLocation(damageLocation);

		DAMAGER_CACHE.add(rider.getUniqueId());
		damaged.damage(event.getDamage(), source.build());
		DAMAGER_CACHE.remove(rider.getUniqueId());
	}

	public static boolean isDamaging(Player player) {
		return DAMAGER_CACHE.contains(player.getUniqueId());
	}

}
