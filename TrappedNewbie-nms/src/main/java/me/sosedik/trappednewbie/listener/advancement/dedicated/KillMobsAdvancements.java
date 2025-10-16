package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Various advancements related to killing mobs
 */
@NullMarked
public class KillMobsAdvancements implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHalloweenKill(EntityDeathEvent event) {
		if (!UtilizerTags.HALLOWEEN_PUMPKIN_WEARERS.isTagged(event.getEntityType())) return;

		LivingEntity entity = event.getEntity();
		Player killer = entity.getKiller();
		if (killer == null) return;
		if (entity.getEquipment() == null) return;
		if (!ItemStack.isType(entity.getEquipment().getHelmet(), Material.JACK_O_LANTERN)) return;

		TrappedNewbieAdvancements.KILL_1K_MOBS_WEARING_A_JACK_O_LANTERN.awardNextCriterion(killer);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLingeringWitch(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.getType() != EntityType.WITCH) return;

		Player killer = entity.getKiller();
		if (killer == null) return;

		DamageSource damageSource = event.getDamageSource();
		Entity directEntity = damageSource.getDirectEntity();
		if (directEntity == null) return;
		if (directEntity.getType() != EntityType.LINGERING_POTION && directEntity.getType() != EntityType.AREA_EFFECT_CLOUD) return;

		TrappedNewbieAdvancements.KILL_500_WITCHES_WITH_LINGERING_POTIONS.awardNextCriterion(killer);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJockeyKill(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Mob entity)) return;

		Player killer = entity.getKiller();
		if (killer == null) return;
		if (!isJockey(entity)) return;

		TrappedNewbieAdvancements.KILL_500_JOCKEYS.awardNextCriterion(killer);
	}

	private boolean isJockey(Mob entity) {
		if (entity.getVehicle() instanceof Mob) return true;

		List<Entity> passengers = entity.getPassengers();
		if (passengers.isEmpty()) return false;

		for (Entity passenger : passengers) {
			if (passenger instanceof Mob)
				return true;
		}
		return false;
	}

}
