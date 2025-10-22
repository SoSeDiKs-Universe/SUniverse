package me.sosedik.trappednewbie.listener.effect;

import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.jspecify.annotations.NullMarked;

/**
 * Bonebreaker effect paralyzes entities
 */
@NullMarked
public class BoneBreakerFreezing implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHit(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player damager)) return;
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (!damager.hasPotionEffect(TrappedNewbieEffects.BONE_BREAKING)) return;

		entity.addPotionEffect(new PotionEffect(TrappedNewbieEffects.PARALYZED, 3 * 20, 0));
	}

}
