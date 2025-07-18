package me.sosedik.trappednewbie.listener.entity;

import com.destroystokyo.paper.entity.ai.PaperGoal;
import com.destroystokyo.paper.entity.ai.VanillaGoal;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.util.EntityUtil;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.entity.CraftMob;
import org.bukkit.entity.AbstractCow;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;

/**
 * Well, you are the only one to blame, really
 */
// MCCheck: 1.21.8, new animals
public class AngryAnimals implements Listener {

	@EventHandler
	public void onSpawn(EntitiesLoadEvent event) {
		for (Entity entity : event.getEntities()) {
			if (!(entity instanceof Mob mob)) continue;

			applyAggroTraits(mob);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onSpawn(EntitySpawnEvent event) {
		if (event.getEntity() instanceof Mob mob)
			applyAggroTraits(mob);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Mob entity)) return;

		TrappedNewbie.scheduler().sync(() -> aggro(entity), 1L);
	}

	private void applyAggroTraits(Mob mob) {
		if (!shouldAggro(mob, mob)) return;
		if (!(((CraftMob) mob).getHandle() instanceof PathfinderMob nms)) return;

		setAttribute(mob, Attribute.ATTACK_DAMAGE, getAttackDamage(mob));
		setAttribute(mob, Attribute.ATTACK_SPEED, 1);
		setAttribute(mob, Attribute.ATTACK_KNOCKBACK, 0.1);

		if (!(mob instanceof Rabbit rabbit && rabbit.getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY)
			&& !(mob instanceof Bee)
			&& !(mob instanceof Panda)
			&& !(mob instanceof PolarBear)
		) {
			Bukkit.getMobGoals().addGoal(mob, 1, new PaperGoal<>(new HurtByTargetGoal(nms)));
			if (!(mob instanceof Fox) && !(!(mob instanceof Creature creature) || Bukkit.getMobGoals().hasGoal(creature, VanillaGoal.MELEE_ATTACK))) {
				Bukkit.getMobGoals().addGoal(mob, 1, new PaperGoal<>(new MeleeAttackGoal(nms, getAttackSpeed(mob), shouldFollowTarget(mob))));
			}
		}
		if (mob instanceof Chicken || mob instanceof Rabbit) Bukkit.getMobGoals().addGoal(mob, 4, new PaperGoal<>(new LeapAtTargetGoal(nms, 0.3F)));
	}

	private double getAttackSpeed(Mob mob) {
		if (mob instanceof Chicken) return 0.95;
		if (mob instanceof AbstractHorse) return 1.5;
		if (mob instanceof Rabbit) return 1.8;
		return 1;
	}

	private double getAttackDamage(Mob mob) {
		if (mob instanceof AbstractCow) return 2;
		if (mob instanceof Pig) return 2;
		if (mob instanceof Sheep) return 2;
		if (mob instanceof AbstractHorse) return 3;
		return 1;
	}

	private boolean shouldFollowTarget(Mob mob) {
		return mob instanceof Dolphin;
	}

	private void setAttribute(Mob mob, Attribute attribute, double value) {
		if (mob.getAttribute(attribute) != null) return;

		mob.registerAttribute(attribute);
		AttributeInstance attributeInstance = mob.getAttribute(attribute);
		if (attributeInstance == null) return;

		attributeInstance.setBaseValue(value);
	}

	private void aggro(Mob entity) {
		if (!(EntityUtil.getCausingDamager(entity) instanceof LivingEntity damager)) return;
		if (damager.isInvulnerable()) return;
		if (damager instanceof Player player && player.getGameMode().isInvulnerable()) return;
		if (entity instanceof Tameable tameable && tameable.isTamed() && damager.getUniqueId().equals(tameable.getOwnerUniqueId())) return;

		entity.getWorld().getNearbyEntitiesByType(Mob.class, entity.getLocation(), 20, mob -> shouldAggro(entity, mob)).forEach(mob -> {
			if (mob.getTarget() == null)
				mob.setTarget(damager);
		});
	}

	private boolean shouldAggro(Mob attackedMob, Mob friendlyMob) {
		return (attackedMob instanceof Animals && friendlyMob instanceof Animals)
			|| (attackedMob instanceof WaterMob && friendlyMob instanceof WaterMob);
	}

}
