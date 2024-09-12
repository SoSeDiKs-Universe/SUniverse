package me.sosedik.requiem.effect;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.resourcelib.api.effect.TickableCustomEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class AttritionEffect extends TickableCustomEffect {

	public AttritionEffect() {
		super(40);
	}

	@Override
	public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
		return entity instanceof Player player && GhostyPlayer.isGhost(player);
	}

}
