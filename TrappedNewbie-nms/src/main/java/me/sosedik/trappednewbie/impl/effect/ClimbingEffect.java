package me.sosedik.trappednewbie.impl.effect;

import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

import java.util.Random;

@NullMarked
public class ClimbingEffect implements KiterinoMobEffectBehaviourWrapper {

	private static final Random RANDOM = new Random();

	private int ambientSoundTime = 0;

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		if (hasHorizontalCollision(entity)) {
			Vector velocity = entity.getVelocity();
			entity.setVelocity(new Vector(velocity.getX() * 0.91D, 0.2D, velocity.getZ() * 0.91D));
		}
		if (RANDOM.nextInt(1000) < this.ambientSoundTime++) {
			this.ambientSoundTime = -40;
			entity.emitSound(Sound.ENTITY_SPIDER_AMBIENT, 1F, 1F);
		}
		return true;
	}

	private boolean hasHorizontalCollision(Entity entity) {
		if (entity instanceof Player player) {
			var bb = player.getBoundingBox().expand(0.05, 0, 0.05);
			return player.getWorld().hasCollisionsIn(bb);
		}
		return ((CraftEntity) entity).getHandle().horizontalCollision;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

}
