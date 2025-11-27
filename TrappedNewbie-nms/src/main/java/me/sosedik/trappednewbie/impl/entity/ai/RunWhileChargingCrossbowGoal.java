package me.sosedik.trappednewbie.impl.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class RunWhileChargingCrossbowGoal extends RandomStrollGoal {

	public RunWhileChargingCrossbowGoal(PathfinderMob mob, double speedModifier) {
		super(mob, speedModifier);
	}

	@Override
	public boolean canUse() {
		return this.mob.isUsingItem() && this.mob.getUseItem().is(Items.CROSSBOW)
			&& this.mob.getTarget() != null && !CrossbowItem.isCharged(this.mob.getUseItem())
			&& !EnchantmentHelper.has(this.mob.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this.mob, Items.CROSSBOW)), EnchantmentEffectComponents.PROJECTILE_COUNT)
			&& this.findPosition();
	}

	public boolean findPosition() {
		Vec3 vector3d = this.getPosition();
		if (vector3d == null) {
			return false;
		} else {
			this.wantedX = vector3d.x;
			this.wantedY = vector3d.y;
			this.wantedZ = vector3d.z;
			return true;
		}
	}

	@Override
	public void start() {
		super.start();
		this.mob.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.mob, Items.CROSSBOW));
		if (this.mob instanceof Pillager pillager)
			pillager.setChargingCrossbow(true);
		if (this.mob.getTarget() != null) {
			this.mob.lookAt(this.mob.getTarget(), 30.0F, 30.0F);
			this.mob.getLookControl().setLookAt(this.mob.getTarget(), 30.0F, 30.0F);
		}
	}

	@Override
	public void tick() {
		int i = this.mob.getTicksUsingItem();
		ItemStack itemstack = this.mob.getUseItem();
		if (i >= CrossbowItem.getChargeDuration(itemstack, this.mob)) {
			this.mob.releaseUsingItem();
			if (this.mob instanceof Pillager pillager)
				pillager.setChargingCrossbow(false);
		}
	}

	@Override
	public void stop() {
		super.stop();
		this.mob.releaseUsingItem();
		if (this.mob instanceof Pillager pillager)
			pillager.setChargingCrossbow(false);
	}

	@Override
	public boolean canContinueToUse() {
		return !CrossbowItem.isCharged(this.mob.getUseItem()) && this.mob.isUsingItem()
			&& this.mob.getUseItem().getItem() instanceof CrossbowItem && !this.mob.isVehicle();
	}

	@Override
	protected @Nullable Vec3 getPosition() {
		LivingEntity target = this.mob.getTarget();
		if (target == null) return null;
		return DefaultRandomPos.getPosAway(this.mob, 16, 7, target.position());
	}

}
