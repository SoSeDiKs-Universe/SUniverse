package me.sosedik.trappednewbie.impl.entity.ai;

import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jspecify.annotations.NullMarked;

// Mostly copy-paste from RangedCrossbowAttackGoal, except requirement for no multishot
@NullMarked
public class RangedMultishotCrossbowAttackGoal<T extends Monster & RangedAttackMob & CrossbowAttackMob> extends RangedCrossbowAttackGoal<T> {

	private final T mob;

	public RangedMultishotCrossbowAttackGoal(T mob, double speedModifier, float attackRadius) {
		super(mob, speedModifier, attackRadius);
		this.mob = mob;
	}

	@Override
	public boolean canUse() {
		return isValidTarget() && isHoldingCrossbow();
	}

	private boolean isHoldingCrossbow() {
		ItemStack crossbow = this.mob.getMainHandItem();
		if (!crossbow.is(Items.CROSSBOW)) {
			crossbow = this.mob.getOffhandItem();
			if (!crossbow.is(Items.CROSSBOW)) return false;
		}
		return EnchantmentHelper.has(crossbow, EnchantmentEffectComponents.PROJECTILE_COUNT);
	}

	private boolean isValidTarget() {
		return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
	}

}
