package me.sosedik.trappednewbie.impl.entity.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class NonBowRangedAttackGoal<T extends Monster & RangedAttackMob> extends RangedBowAttackGoal<T> {

	public NonBowRangedAttackGoal(T mob, double speedModifier, int attackIntervalMin, float attackRadius) {
		super(mob, speedModifier, attackIntervalMin, attackRadius);
	}

	@Override
	protected boolean isHoldingBow() {
		return !this.mob.getMainHandItem().isEmpty() || !this.mob.getOffhandItem().isEmpty();
	}

	@Override
	protected InteractionHand getBowHoldingHand() {
		return this.mob.getMainHandItem().isEmpty() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
	}

}
