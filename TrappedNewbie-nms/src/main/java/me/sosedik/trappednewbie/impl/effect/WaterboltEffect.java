package me.sosedik.trappednewbie.impl.effect;

import me.sosedik.kiterino.registry.wrapper.KiterinoMobEffectBehaviourWrapper;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class WaterboltEffect implements KiterinoMobEffectBehaviourWrapper {

	private int boltTick = 0;

	@Override
	public boolean applyEffectTick(LivingEntity entity, int amplifier) {
		entity.setRemainingAir(entity.getMaximumAir());

		if (!(entity instanceof Player)) return true;
		if (++this.boltTick != 10) return true;

		this.boltTick = 0;

		if (!entity.isUnderWater()) return true;
		if (!entity.isSwimming()) return true;

		float yRot = entity.getBodyYaw();
		float xRot = entity.getPitch();
		double f1 = -Math.sin(yRot * (Math.PI / 180F)) * Math.cos(xRot * (Math.PI / 180F));
		double f2 = -Math.sin(xRot * (Math.PI / 180F));
		double f3 = Math.cos(yRot * (Math.PI / 180F)) * Math.cos(xRot * (Math.PI / 180F));
		double f4 = Math.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
		float f5 = 1.5F;
		f1 *= f5 / f4;
		f2 *= f5 / f4;
		f3 *= f5 / f4;
		entity.setVelocity(entity.getVelocity().add(new Vector(f1, f2, f3)));
		((CraftPlayer) entity).getHandle().startAutoSpinAttack(20, 0, ItemStack.EMPTY);
		return true;
	}

	@Override
	public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
		return true;
	}

}
