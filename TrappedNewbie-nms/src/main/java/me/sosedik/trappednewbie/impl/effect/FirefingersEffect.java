package me.sosedik.trappednewbie.impl.effect;

import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import net.kyori.adventure.util.TriState;
import org.bukkit.entity.LivingEntity;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FirefingersEffect implements KiterinoMobEffectBehaviourWrapper {

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		entity.setVisualFire(TriState.TRUE);
		entity.setImmuneToFire(true);
		return true;
	}

	@Override
	public void onEffectRemoved(LivingEntity entity, int amplifier) {
		entity.setVisualFire(TriState.NOT_SET);
		entity.setImmuneToFire(null);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

}
