package me.sosedik.resourcelib.api.effect;

import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.jetbrains.annotations.NotNull;

public abstract class TickableCustomEffect implements KiterinoMobEffectBehaviourWrapper {

	private final int tickDelay;
	private int nextTickDelay;

	protected TickableCustomEffect(int tickDelay) {
		this.tickDelay = tickDelay;
		this.nextTickDelay = tickDelay;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		this.nextTickDelay--;
		if (this.nextTickDelay == 0) {
			this.nextTickDelay = this.tickDelay;
			return true;
		}
		return false;
	}

}
