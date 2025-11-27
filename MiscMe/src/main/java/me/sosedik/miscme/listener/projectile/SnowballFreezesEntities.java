package me.sosedik.miscme.listener.projectile;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Snowballs freeze entities upon hit.
 * <p>Also adds a slight cooldown to snowballs.
 */
@NullMarked
public class SnowballFreezesEntities implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onHit(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Snowball snowball)) return;
		if (!(event.getHitEntity() instanceof LivingEntity entity)) return;
		if (Tag.ENTITY_TYPES_FREEZE_IMMUNE_ENTITY_TYPES.isTagged(entity.getType())) return;
		if (snowball.getItem().getType() != Material.SNOWBALL) return;

		entity.setFreezeTicks(entity.getFreezeTicks() + 100);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onThrow(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof Snowball snowball)) return;

		ItemStack item = snowball.getItem();
		if (item.getType() != Material.SNOWBALL) return;
		if (!(snowball.getShooter() instanceof Player player)) return;

		player.setCooldown(item, 4);
	}

}
