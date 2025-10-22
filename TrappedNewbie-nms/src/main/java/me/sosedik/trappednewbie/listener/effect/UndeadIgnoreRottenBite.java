package me.sosedik.trappednewbie.listener.effect;

import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.Tag;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Undead mobs ignore rotten bite effect
 */
@NullMarked
public class UndeadIgnoreRottenBite implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onTarget(EntityTargetLivingEntityEvent event) {
		if (!(event.getTarget() instanceof Player player)) return;
		if (!(event.getEntity() instanceof Mob entity)) return;
		if (!Tag.ENTITY_TYPES_UNDEAD.isTagged(entity.getType())) return;
		if (!player.hasPotionEffect(TrappedNewbieEffects.ROTTEN_BITE)) return;
		if (EntityUtil.getCausingDamager(entity) == player) return;

		event.setCancelled(true);
	}

}
