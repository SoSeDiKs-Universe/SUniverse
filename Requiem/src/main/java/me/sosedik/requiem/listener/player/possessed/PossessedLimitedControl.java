package me.sosedik.requiem.listener.player.possessed;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.jspecify.annotations.NullMarked;

/**
 * Possessed have limited physical control in world:
 * - Can't pick up items and experience orbs
 * - Can't be damaged
 * - Can't interact with entities
 * - Can't sleep
 * - Proxy potion effect to the possessed mob
 */
@NullMarked
public class PossessedLimitedControl implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPossessedPickup(EntityPickupItemEvent event) {
		LivingEntity entity = event.getEntity();
		Player player = entity.getRider();
		if (player == null) return;
		if (!PossessingPlayer.isPossessing(player)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(PlayerPickupExperienceEvent event) {
		if (!PossessingPlayer.isPossessing(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!PossessingPlayer.isPossessing(player)) return;

		event.setDamage(0);
		if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		if (!PossessingPlayer.isPossessing(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteractEntity(PlayerInteractAtEntityEvent event) {
		if (!PossessingPlayer.isPossessing(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSleep(PlayerBedEnterEvent event) {
		if (!PossessingPlayer.isPossessing(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEffect(EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!PossessingPlayer.isPossessing(player)) return;

		PotionEffect potionEffect = event.getNewEffect();
		if (potionEffect == null) return;

		LivingEntity possessed = PossessingPlayer.getPossessed(player);
		if (possessed == null) return;

		if (event.getCause() == EntityPotionEffectEvent.Cause.PLUGIN && possessed.hasPotionEffect(potionEffect.getType())) return;

		event.setCancelled(true);
		if (event.getCause() != EntityPotionEffectEvent.Cause.PLUGIN)
			possessed.addPotionEffect(potionEffect);
	}

}
