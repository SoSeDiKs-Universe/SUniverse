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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Goat;
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
// MCCheck: 1.21.11, new animals
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
		if (!shouldHaveAggroTraits(mob)) return;
		if (!(((CraftMob) mob).getHandle() instanceof PathfinderMob nms)) return;

		setAttribute(mob, Attribute.ATTACK_DAMAGE, getAttackDamage(mob));
		setAttribute(mob, Attribute.ATTACK_SPEED, 1);
		setAttribute(mob, Attribute.ATTACK_KNOCKBACK, 0.1);

		if (!isAngryTowardPlayer(mob)) {
			Bukkit.getMobGoals().addGoal(mob, 1, new PaperGoal<>(new HurtByTargetGoal(nms)));
			if (!(mob instanceof Fox) && !(!(mob instanceof Creature creature) || Bukkit.getMobGoals().hasGoal(creature, VanillaGoal.MELEE_ATTACK))) {
				Bukkit.getMobGoals().addGoal(mob, 1, new PaperGoal<>(new MeleeAttackGoal(nms, EntityUtil.getAttackSpeedBonus(mob), shouldFollowTarget(mob))));
			}
		}
		if (mob instanceof Chicken || mob instanceof Rabbit) Bukkit.getMobGoals().addGoal(mob, 4, new PaperGoal<>(new LeapAtTargetGoal(nms, 0.3F)));
	}

	private boolean isAngryTowardPlayer(Mob mob) {
		if (mob instanceof Rabbit rabbit) return rabbit.getRabbitType() == Rabbit.Type.THE_KILLER_BUNNY;
		return mob instanceof Bee
			|| mob instanceof Panda
			|| mob instanceof PolarBear;
	}

	private double getAttackDamage(Mob mob) {
		if (mob instanceof AbstractCow) return 3;
		if (mob instanceof Pig) return 3;
		if (mob instanceof Sheep) return 3;
		if (mob instanceof AbstractHorse) return 4;
		if (mob instanceof Goat) return 5;
		return 2;
	}

	private boolean shouldFollowTarget(Mob mob) {
		return mob instanceof Dolphin
			|| mob instanceof Chicken;
	}

	private void setAttribute(Mob mob, Attribute attribute, double value) {
		AttributeInstance mobAttribute = mob.getAttribute(attribute);
		if (mobAttribute != null) {
			// Some entities may already have the attribute, but equal to 0
			// (e.g., chickens have attack damage, due to Purpur's "chickens can retaliate" feature)
			if (mobAttribute.getBaseValue() != 0) return;
		} else {
			mob.registerAttribute(attribute);
			mobAttribute = mob.getAttribute(attribute);
			if (mobAttribute == null) return;
		}

		mobAttribute.setBaseValue(value);
	}

	private void aggro(Mob damagedEntity) {
		if (!(EntityUtil.getCausingDamager(damagedEntity) instanceof LivingEntity damager)) return;
		if (damager.isInvulnerable()) return;
		if (damager instanceof Player player && player.getGameMode().isInvulnerable()) return;
		if (damagedEntity instanceof Tameable tameable && tameable.isTamed() && damager.getUniqueId().equals(tameable.getOwnerUniqueId())) return;

		damagedEntity.getWorld().getNearbyEntitiesByType(Mob.class, damagedEntity.getLocation(), 20, mob -> shouldAggro(damagedEntity, mob)).forEach(mob -> {
			if (isSameType(damagedEntity, mob))
				EntityUtil.setTarget(mob, damager);
			else if (mob instanceof Animals)
				mob.setPanicTicks(40);
		});
	}

	private boolean isSameType(Mob damaged, Mob targeting) {
		final EntityType targetingType = targeting.getType();
		return switch (damaged.getType()) {
			case CAT, OCELOT -> targetingType == EntityType.CAT || targetingType == EntityType.OCELOT;
			case SQUID, GLOW_SQUID -> targetingType == EntityType.SQUID || targetingType == EntityType.GLOW_SQUID;
			case HORSE, DONKEY, MULE -> targetingType == EntityType.HORSE || targetingType == EntityType.DONKEY || targetingType == EntityType.MULE;
			case LLAMA, TRADER_LLAMA -> targetingType == EntityType.LLAMA || targetingType == EntityType.TRADER_LLAMA;
			case COW, MOOSHROOM -> targetingType == EntityType.COW || targetingType == EntityType.MOOSHROOM;
			case NAUTILUS, ZOMBIE_NAUTILUS -> targetingType == EntityType.NAUTILUS || targetingType == EntityType.ZOMBIE_NAUTILUS;
			case CAMEL, CAMEL_HUSK -> targetingType == EntityType.CAMEL || targetingType == EntityType.CAMEL_HUSK;
			default -> damaged.getType() == targetingType;
		};
	}

	private boolean shouldAggro(Mob attackedMob, Mob friendlyMob) {
		if (attackedMob == friendlyMob) return false;
		if (friendlyMob.getTarget() != null) return false;
		if (friendlyMob instanceof Tameable tameable && tameable.isTamed()) return false;
		if (!friendlyMob.getPassengers().isEmpty()) return false;
		return (attackedMob instanceof Animals && friendlyMob instanceof Animals)
			|| (attackedMob instanceof WaterMob && friendlyMob instanceof WaterMob);
	}

	private boolean shouldHaveAggroTraits(Mob mob) {
		return mob instanceof Animals
			|| mob instanceof WaterMob;
	}

}
