package me.sosedik.trappednewbie.impl.entity.ai;

import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.bukkit.inventory.EntityEquipment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TrumpetAttackGoal<T extends Monster & RangedAttackMob> extends RangedBowAttackGoal<T> {

	public TrumpetAttackGoal(T mob, double speedModifier, int attackIntervalMin, float attackRadius) {
		super(mob, speedModifier, attackIntervalMin, attackRadius);
	}

	@Override
	protected boolean isHoldingBow() {
		return isHoldingTrumpet();
	}

	@Override
	protected InteractionHand getBowHoldingHand() {
		return this.mob.getMainHandItem().isEmpty() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
	}

	protected boolean isHoldingTrumpet() {
		EntityEquipment equipment = this.mob.getBukkitLivingEntity().getEquipment();
		return equipment.getItemInMainHand().getType() == TrappedNewbieItems.TRUMPET
				|| equipment.getItemInOffHand().getType() == TrappedNewbieItems.TRUMPET;
	}

	@Override
	protected void performAttack(LivingEntity target, int ticksUsingItem) {
		this.mob.completeUsingItem();
	}

}
