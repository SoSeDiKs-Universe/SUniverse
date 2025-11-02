package me.sosedik.trappednewbie.listener.entity;

import com.destroystokyo.paper.entity.ai.PaperGoal;
import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import me.sosedik.trappednewbie.impl.entity.ai.RangedMultishotCrossbowAttackGoal;
import me.sosedik.trappednewbie.impl.entity.ai.RunWhileChargingCrossbowGoal;
import me.sosedik.trappednewbie.impl.entity.ai.ZoomInAtRandomGoal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPillager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Various improvements for pillagers:
 * - Stroll around while charging the crossbow (unless multishot)
 * - If the crossbow has multishot, use closer range
 * - Patrol leaders may spawn with a Spyglass
 */
@NullMarked
public class BetterPillagers implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onSpawn(EntityAddToWorldEvent event) {
		if (!(event.getEntity() instanceof CraftPillager entity)) return;

		Bukkit.getMobGoals().addGoal(entity, 1, new PaperGoal<>(new RunWhileChargingCrossbowGoal(entity.getHandle(), 0.9)));
		Bukkit.getMobGoals().addGoal(entity, 2, new PaperGoal<>(new RangedMultishotCrossbowAttackGoal<>(entity.getHandle(), 1, 3F)));
		Bukkit.getMobGoals().addGoal(entity, 3, new ZoomInAtRandomGoal(entity));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSpawn(CreatureSpawnEvent event) {
		if (!(event.getEntity() instanceof Pillager pillager)) return;
		if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.PATROL) return;
		if (!pillager.isPatrolLeader()) return;
		if (Math.random() > 0.5) return;

		EntityEquipment equipment = pillager.getEquipment();
		if (equipment.getItemInOffHand().isEmpty()) {
			equipment.setItemInMainHand(ItemStack.of(Material.CROSSBOW));
			equipment.setItemInOffHand(ItemStack.of(Material.SPYGLASS));
			equipment.setItemInOffHandDropChance(0.25F);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEvent(EntityTargetLivingEntityEvent event) {
		if (!(event.getEntity() instanceof Pillager pillager)) return;
		if (!pillager.isPatrolling()) return;

		LivingEntity target = event.getTarget();
		if (target == null) return;

		ItemStack activeItem = pillager.getActiveItem();
		if (activeItem.getType() != Material.SPYGLASS) return;

		pillager.setAggressive(true);
		if (!pillager.getPathfinder().hasPath())
			pillager.getPathfinder().moveTo(target);

		pillager.getWorld().getNearbyEntitiesByType(Raider.class, pillager.getLocation(), 16,
			entity -> entity != pillager && entity.getTarget() != target)
			.forEach(raider -> {
				raider.setAggressive(true);
				if (raider.isPatrolling()) return;

				ItemStack item = raider.getActiveItem();
				if (item.getType() == Material.SPYGLASS) return;

				raider.setTarget(target);
		});
	}

}
