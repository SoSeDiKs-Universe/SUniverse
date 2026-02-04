package me.sosedik.trappednewbie.impl.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BooleanSupplier;

@NullMarked
public class AvoidDamageGoal extends PanicGoal {

	private final BooleanSupplier avoidsAttacker;

	public AvoidDamageGoal(PathfinderMob creature, double speed) {
		this(creature, speed, () -> false);
	}

	public AvoidDamageGoal(PathfinderMob creature, double speed, BooleanSupplier avoidsAttacker) {
		super(creature, speed);
		this.avoidsAttacker = avoidsAttacker;
	}

	public boolean canUse() {
		if (this.mob.isOnFire() && !this.mob.isSensitiveToWater()) {
			if (this.mob.level().isRaining()) {
				for (int i = 0; i < 10; ++i) {
					BlockPos blockpos1 = this.mob.blockPosition().offset(this.mob.getRandom().nextInt(20) - 10, this.mob.getRandom().nextInt(6) - 3, this.mob.getRandom().nextInt(20) - 10);
					if (this.mob.level().isRainingAt(blockpos1) && this.mob.getWalkTargetValue(blockpos1) >= 0.0F) {
						return this.hasPosition(Vec3.atBottomCenterOf(blockpos1));
					}
				}
			}

			BlockPos blockpos = this.lookForWater(this.mob.level(), this.mob, 15);
			return blockpos != null && this.mob.getNavigation().createPath(blockpos, 0) != null && this.hasPosition(Vec3.atLowerCornerOf(blockpos)) || this.findRandomPosition();
		}

		if (this.avoidsAttacker.getAsBoolean() && this.mob.getLastHurtByMob() != null) {
			return this.hasPosition(DefaultRandomPos.getPosAway(this.mob, 10, 9, this.mob.getLastHurtByMob().position()));
		}

		if (this.mob.getLastDamageSource() != null && this.shouldAvoidDamage(this.mob.getLastDamageSource())) {
			Vec3 damageVec = this.mob.getLastDamageSource().getSourcePosition();
			return damageVec != null ? this.hasPosition(DefaultRandomPos.getPosAway(this.mob, 8, 5, damageVec)) : this.findRandomPosition();
		}

		return false;
	}

	private boolean hasPosition(@Nullable Vec3 vec3d) {
		if (vec3d == null)
			return false;

		this.posX = vec3d.x;
		this.posY = vec3d.y;
		this.posZ = vec3d.z;
		return true;
	}

	protected boolean shouldAvoidDamage(DamageSource source) {
		if (source.getEntity() != null)
			return false;

		if (source.is(DamageTypeTags.WITCH_RESISTANT_TO) && source.getDirectEntity() == null)
			return false;

		return !source.is(DamageTypes.DROWN) && !source.is(DamageTypes.FALL) && !source.is(DamageTypes.STARVE) && !source.is(DamageTypes.FELL_OUT_OF_WORLD);
	}

}
