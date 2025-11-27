package me.sosedik.trappednewbie.impl.effect;

import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

import java.util.List;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;

@NullMarked
public class ParalyzedEffect implements KiterinoMobEffectBehaviourWrapper {

	private static final AttributeModifier SPEED = new AttributeModifier(trappedNewbieKey("paralyzed"), -100, AttributeModifier.Operation.ADD_NUMBER);
	private static final List<PotionEffect> EFFECTS = List.of(
		new PotionEffect(PotionEffectType.SLOWNESS, 60, 4, false, false, true),
		new PotionEffect(PotionEffectType.MINING_FATIGUE, 60, 0, false, false, true),
		new PotionEffect(PotionEffectType.BLINDNESS, 60, 2, false, false, true)
	);

	private boolean removedAI = false;

	@Override
	public void onEffectAdded(LivingEntity entity, int amplifier) {
		this.removedAI = entity.hasAI();
	}

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		AttributeInstance attribute = entity.getAttribute(Attribute.MOVEMENT_SPEED);
		if (attribute != null && attribute.getModifier(SPEED.key()) == null)
			attribute.addTransientModifier(SPEED);
		entity.addPotionEffects(EFFECTS);

		if (this.removedAI)
			entity.setAI(false);

		return true;
	}

	@Override
	public void onEffectRemoved(LivingEntity entity, int amplifier) {
		AttributeInstance attribute = entity.getAttribute(Attribute.MOVEMENT_SPEED);
		if (attribute != null)
			attribute.removeModifier(SPEED);

		if (this.removedAI)
			entity.setAI(true);
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

}
