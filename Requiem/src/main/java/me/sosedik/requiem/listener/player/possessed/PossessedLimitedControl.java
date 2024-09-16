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
import org.jetbrains.annotations.NotNull;

/**
 * Possessed have limited physical control in world:
 * - Can't pickup items and exp
 * - Can't be damaged
 * - Can't interact with entities
 * - Can't sleep
 * - Proxy potion effect to the possessed mob
 */
public class PossessedLimitedControl implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPossessedPickup(@NotNull EntityPickupItemEvent event) {
		LivingEntity entity = event.getEntity();
		Player player = entity.getRider();
		if (player == null) return;
		if (!PossessingPlayer.isPossessing(player)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(@NotNull PlayerPickupExperienceEvent event) {
		if (!PossessingPlayer.isPossessing(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamage(@NotNull EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!PossessingPlayer.isPossessing(player)) return;

		event.setDamage(0);
		if (event.getCause() != EntityDamageEvent.DamageCause.CUSTOM)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteractEntity(@NotNull PlayerInteractEntityEvent event) {
		if (!PossessingPlayer.isPossessing(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteractEntity(@NotNull PlayerInteractAtEntityEvent event) {
		if (!PossessingPlayer.isPossessing(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSleep(@NotNull PlayerBedEnterEvent event) {
		if (!PossessingPlayer.isPossessing(event.getPlayer())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEffect(@NotNull EntityPotionEffectEvent event) {
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
