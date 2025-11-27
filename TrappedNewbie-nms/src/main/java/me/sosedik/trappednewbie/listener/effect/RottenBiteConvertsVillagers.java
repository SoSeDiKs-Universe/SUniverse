package me.sosedik.trappednewbie.listener.effect;

import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Rotten bite converts villagers into zombies
 */
@NullMarked
public class RottenBiteConvertsVillagers implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHit(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player damager)) return;
		if (!(event.getEntity() instanceof Villager villager)) return;
		if (!damager.hasPotionEffect(TrappedNewbieEffects.ROTTEN_BITE)) return;
		if (!damager.getInventory().getItemInMainHand().isEmpty()) return;

		ZombieVillager zombieVillager = villager.zombify();
		if (zombieVillager == null) return;

		zombieVillager.emitSound(Sound.ENTITY_ZOMBIE_VILLAGER_HURT, 1F, 1F);
	}

}
