package me.sosedik.trappednewbie.impl.effect;

import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import me.sosedik.trappednewbie.api.event.player.PlayerComfortEraseEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ComfortEffect implements KiterinoMobEffectBehaviourWrapper {

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		if (!(entity instanceof Player player)) return false;
		if (new PlayerComfortEraseEvent(player).callEvent()) return false;

		addPotionEffect(player, amplifier);

		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

	private void addPotionEffect(Player player, int amplifier) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30, amplifier, true, false, false));
	}

}
