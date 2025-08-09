package me.sosedik.trappednewbie.impl.effect;

import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ThirstEffect implements KiterinoMobEffectBehaviourWrapper {

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		return entity instanceof Player player;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

}
