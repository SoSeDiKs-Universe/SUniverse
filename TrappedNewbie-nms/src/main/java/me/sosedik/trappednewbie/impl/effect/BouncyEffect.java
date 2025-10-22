package me.sosedik.trappednewbie.impl.effect;

import com.destroystokyo.paper.ParticleBuilder;
import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class BouncyEffect implements KiterinoMobEffectBehaviourWrapper {

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		if (!entity.isOnGround()) return true;
		if (entity.isInWater()) return true;

		Vector velocity = entity.getVelocity();
		if (velocity.getY() > 0.2) return true;

		entity.setVelocity(velocity.setY(1.5));
		entity.emitSound(Sound.ENTITY_SLIME_JUMP, 1F, 1F);
		new ParticleBuilder(Particle.ITEM_SLIME)
			.location(entity.getLocation())
			.offset(0.5, 0, 0.5)
			.count(8)
			.spawn();

		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

}
