package me.sosedik.requiem.effect;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.resourcelib.api.effect.TickableCustomEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class AttritionEffect extends TickableCustomEffect {

	public AttritionEffect() {
		super(40);
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		return entity instanceof Player player && GhostyPlayer.isGhost(player);
	}

}
