package me.sosedik.trappednewbie.entity.nms;

import me.sosedik.trappednewbie.impl.entity.ai.AvoidDamageGoal;
import me.sosedik.trappednewbie.impl.entity.ai.MutantMeleeAttackGoal;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NullMarked;

import java.util.EnumSet;

@NullMarked
public class MutantZombieImpl extends Zombie {

	private int animationTick;

	public MutantZombieImpl(EntityType<? extends MutantZombieImpl> type, Level level) {
		super(type, level);
		this.expToDrop = Enemy.XP_REWARD_HUGE;
		setShouldBurnInDay(false);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Zombie.createAttributes()
			.add(Attributes.MAX_HEALTH, 150.0)
			.add(Attributes.ATTACK_DAMAGE, 12.0)
			.add(Attributes.FOLLOW_RANGE, 35.0)
			.add(Attributes.MOVEMENT_SPEED, 0.26)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
			.add(Attributes.STEP_HEIGHT, 1.0);
	}

	@Override
	public void initAttributes() {
	}

	@Override
	protected void registerGoals() {
//		this.goalSelector.addGoal(0, new SlamGroundGoal(this));
//		this.goalSelector.addGoal(0, new RoarGoal(this));
//		this.goalSelector.addGoal(0, new ThrowAttackGoal(this));
		this.goalSelector.addGoal(1, new MutantMeleeAttackGoal(this, 1.2).setMaxAttackTick(0));
		this.goalSelector.addGoal(2, new AvoidDamageGoal(this, 1.0));
		this.goalSelector.addGoal(3, new MoveThroughVillageGoal(this, 1.0, true, 4, () -> {
			return false;
		}));
		this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this, WitherBoss.class).setAlertOthers());
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true).setUnseenMemoryTicks(300));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
	}

	@Override
	public int getMaxFallDistance() {
		LivingEntity target = this.getTarget();
		return target != null ? (int) this.distanceTo(target) : 3;
	}

	@Override
	public void tick() {
		super.tick();
		this.animationTick++;
		if (this.animationTick > 16)
			this.animationTick = 0;
	}

	@Override
	protected void handleAttributes(float difficulty) {
	}

	@Override
	protected boolean convertsInWater() {
		return false;
	}

	static class SlamGroundGoal extends Goal {

		private final MutantZombieImpl mob;
		private double dirX = -1.0;
		private double dirZ = -1.0;

		public SlamGroundGoal(MutantZombieImpl mob) {
			this.mob = mob;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
		}

//		@Override
//		protected EntityAnimation getAnimation() {
//			return MutantZombie.SLAM_GROUND_ANIMATION;
//		}

		@Override
		public boolean canUse() {
			return this.mob.getTarget() != null;
		}

		@Override
		public void start() {
			super.start();
			this.mob.ambientSoundTime = -this.mob.getAmbientSoundInterval();
			// todo sound
//			this.mob.playSound(ModSoundEvents.ENTITY_MUTANT_ZOMBIE_ATTACK_SOUND_EVENT.value(),
//				0.3F,
//				0.8F + this.mob.random.nextFloat() * 0.4F);
		}

		@Override
		public void tick() {
			LivingEntity target = this.mob.getTarget();
			if (target == null) return;

			this.mob.getNavigation().stop();
			if (this.mob.animationTick < 8) {
				this.mob.lookControl.setLookAt(target, 30.0F, 30.0F);
			}

			if (this.mob.animationTick == 8) {
				double x = target.getX() - this.mob.getX();
				double z = target.getZ() - this.mob.getZ();
				double d = Math.sqrt(x * x + z * z);
				this.dirX = x / d;
				this.dirZ = z / d;
			}

			if (this.mob.animationTick == 12) {
				int x = (int) Math.floor(this.mob.getX() + this.dirX * 2.0);
				int y = (int) Math.floor(this.mob.getBoundingBox().minY);
				int z = (int) Math.floor(this.mob.getZ() + this.dirZ * 2.0);
				int x1 = (int) Math.floor(this.mob.getX() + this.dirX * 8.0);
				int z1 = (int) Math.floor(this.mob.getZ() + this.dirZ * 8.0);
//					SeismicWave.createWaves(this.mob.level(), this.mob.seismicWaveList, x, z, x1, z1, y); // todo waves
				this.mob.playSound(SoundEvents.GENERIC_EXPLODE.value(),
					0.5F,
					0.8F + this.mob.random.nextFloat() * 0.4F);
			}
		}

		@Override
		public void stop() {
			super.stop();
			this.dirX = -1.0;
			this.dirZ = -1.0;
		}
	}

}
