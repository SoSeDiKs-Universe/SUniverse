package me.sosedik.trappednewbie.entity.nms;

import com.destroystokyo.paper.ParticleBuilder;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEntities;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieSoundKeys;
import me.sosedik.trappednewbie.entity.api.Glider;
import me.sosedik.trappednewbie.impl.item.nms.HangGliderItem;
import me.sosedik.utilizer.util.InventoryUtil;
import net.kyori.adventure.sound.Sound;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Interaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class GliderEntityImpl extends Display.ItemDisplay {

	private static final boolean ALLOW_SNEAK_RELEASE = true;
	private static final double INITIAL_VELOCITY_GLIDER_DAMAGE = 30;
	private static final double FIRE_BOOST = 0.06;
	private static final double LAVA_BOOST = 0.02;
	private static final String NO_ITEM_DAMAGE_TAG = "no_item_damage";

	private @Nullable Interaction hitbox;
	private float roll = 0F;
	private int damageTimer;
	private int soundTimer;
	private int lastAttack = -999;
	private int attacks;
	private boolean noItemDamage = false;
	private boolean hasLanded = false;

	public GliderEntityImpl(EntityType<GliderEntityImpl> entityType, Level level) {
		super(entityType, level);
		this.noPhysics = false;
		this.setInvisible(true);
		getBukkitEntity().setPersistent(false);

		if (getBukkitEntity() instanceof Glider entity) {
			entity.setItemStack(ItemStack.of(TrappedNewbieItems.CHERRY_HANG_GLIDER));
			entity.setTeleportDuration(2);
			entity.setInterpolationDuration(3);
			Transformation transformation = entity.getTransformation();
			transformation.getTranslation().set(0, 1.2F, -0.05F);
			transformation.getScale().set(1.5F);
			entity.setTransformation(transformation);
		}
	}

	public static net.minecraft.world.item.ItemStack createDispenser(BlockSource blockSource, net.minecraft.world.item.ItemStack item) {
		Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
		ServerLevel level = blockSource.level();
		Vec3 vec = blockSource.center();

		var entity = new GliderEntityImpl(TrappedNewbieEntities.GLIDER, level);
		entity.setItemStack(item.copyWithCount(1));
		entity.setPos(vec.x, vec.y - 0.5f, vec.z);
		entity.setYRot(direction.getAxis() == Direction.Axis.Y ? 0 : direction.get2DDataValue());
		entity.setXRot(direction.getAxis() != Direction.Axis.Y ? 0 : (direction == Direction.UP ? -90 : 90));
		entity.setDeltaMovement(Vec3.atLowerCornerOf(direction.getUnitVec3i()).scale(0.6));

		level.addFreshEntity(entity);
		level.getWorld().playSound(Sound.sound(TrappedNewbieSoundKeys.HAND_GLIDER_OPEN, Sound.Source.NEUTRAL, 0.8F, entity.random.nextFloat() * 0.2F + 1.2F), entity.getBukkitEntity());
		item.shrink(1);
		return item;
	}

	public static boolean create(ServerLevel level, LivingEntity rider, net.minecraft.world.item.ItemStack item, InteractionHand hand) {
		if (rider.isPassenger()) return false;
		if (rider.isShiftKeyDown() && !ALLOW_SNEAK_RELEASE) return false;

		boolean noDamage = rider instanceof ServerPlayer player && player.isCreative();
		if (!noDamage) {
			item.hurtAndBreak(
				(int) Math.max(0, -rider.getDeltaMovement().y
					* INITIAL_VELOCITY_GLIDER_DAMAGE
					* (90 - Math.abs(Math.clamp(rider.getXRot(), -30, 80))) / 90),
				rider,
				hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
		}

		if (item.isEmpty()) return false;

		var entity = new GliderEntityImpl(TrappedNewbieEntities.GLIDER, level);
		EntityDimensions sitting = rider.getDimensions(Pose.SITTING);
		EntityDimensions currentDim = rider.getDimensions(rider.getPose());
		entity.setItemStack(item);
		entity.setPos(rider.position().add(0, currentDim.height() - sitting.height(), 0));
		entity.setYRot(rider.getYRot());
		entity.setXRot(rider.getXRot());
		entity.setDeltaMovement(rider.getDeltaMovement().add(rider.getLookAngle().multiply(0.2, 0.02, 0.2).scale(rider.isShiftKeyDown() ? 2 : 1)));
		entity.noItemDamage = noDamage;
		level.addFreshEntity(entity);
		level.getWorld().playSound(Sound.sound(TrappedNewbieSoundKeys.HAND_GLIDER_OPEN, Sound.Source.NEUTRAL, 0.8F, entity.random.nextFloat() * 0.2F + 1.2F), entity.getBukkitEntity());

		if (!rider.isShiftKeyDown())
			rider.startRiding(entity);

		return true;
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
		return Vec3.ZERO;
	}

	@Override
	public void tick() {
		super.tick();
		if (!(level() instanceof ServerLevel level)) return;

		Entity passenger = getFirstPassenger();
		if (passenger != null && !passenger.isAlive()) {
			passenger.stopRiding(true);
			passenger = null;
		}

		if (passenger == null) {
			if (this.hitbox == null || !this.hitbox.isValid()) {
				this.hitbox = level.getWorld().spawn(CraftLocation.toBukkit(position(), level.getWorld()), Interaction.class, entity -> {
					entity.setPersistent(false);
					entity.setInteractionWidth(0.8F);
					entity.setInteractionHeight(1.2F);
				});
				getBukkitEntity().addPassenger(this.hitbox);
			}
		} else {
			if (passenger.getBukkitEntity() instanceof Interaction) {
				passenger = null;
			} else if (this.hitbox != null) {
				this.hitbox.remove();
				this.hitbox = null;
			}
		}


		if ((onGround() || (passenger != null && passenger.onGround())) && this.tickCount > 10) {
			this.hasLanded = true;
			giveOrDrop(passenger);
			return;
		}

		if (passenger != null) {
			setYRot(Mth.rotLerp(0.175F, getYRot(), passenger.getYRot()));
			setXRot(Mth.rotLerp(0.175F, getXRot(), Math.clamp(passenger.getXRot(), -30, 80)));
			float newRoll = Math.clamp((-Mth.degreesDifference(passenger.getYRot(), this.getYRot())) * Mth.DEG_TO_RAD * 0.5F, -1f, 1F);

			if (Math.abs(newRoll - this.roll) > Mth.DEG_TO_RAD / 2) {
				updateRoll(newRoll);
			}
		} else {
			float newRoll = this.roll * 0.98F;

			if (Math.abs(newRoll - this.roll) > Mth.DEG_TO_RAD / 2 || Math.abs(newRoll) > Mth.DEG_TO_RAD / 2) {
				updateRoll(newRoll);
			}
		}

		int dmgTimeout = level.dimension() == Level.NETHER ? 12 : 20;
		int dmgFrequency = 2;
		int dmg = 1;

		BlockPos.MutableBlockPos mut = blockPosition().mutable();
		for (int i = 0; i < 32; i++) {
			var state = level.getBlockState(mut);
			if (state.is(Blocks.FIRE) || state.is(Blocks.CAMPFIRE) && i < 24) {
				push(0, ((32 - i) / 32F) * FIRE_BOOST, 0);
				dmgFrequency = 1;
			} else if (state.is(Blocks.LAVA) && i < 6) {
				push(0, ((6 - i) / 6F) * LAVA_BOOST, 0);
				dmgTimeout = 2;
				dmgFrequency = 1;
				dmg = 2;
			} else if (state.isFaceSturdy(level, mut, Direction.UP)) {
				break;
			}

			mut.move(0, -1, 0);
		}


		double gravity = 0.068;
		if (level.isRainingAt(blockPosition())) {
			gravity = 0.09;
		} else if (hasHighGravity(level)) {
			gravity = 0.084;
		} else if (hasLowGravity(level)) {
			gravity = 0.056;
		}

		resetFallDistance();
		if (passenger != null)
			passenger.resetFallDistance();

		Vec3 velocity = getDeltaMovement();
		Vec3 rotationVector = getLookAngle();
		float pitch = getXRot() * (float) (Math.PI / 180.0);
		double rotationLength = Math.sqrt(rotationVector.x * rotationVector.x + rotationVector.z * rotationVector.z);
		double horizontalVelocity = velocity.horizontalDistance();
		double rotationVectorLength = rotationVector.length();
		double cosPitch = Math.cos(pitch);
		cosPitch = cosPitch * cosPitch * Math.min(1.0, rotationVectorLength / 0.4);
		velocity = getDeltaMovement().add(0.0, gravity * (-1.0 + cosPitch * 0.75), 0.0);
		if (velocity.y < 0.0 && rotationLength > 0.0) {
			double m = velocity.y * -0.1 * cosPitch;
			velocity = velocity.add(rotationVector.x * m / rotationLength, m, rotationVector.z * m / rotationLength);
		}

		if (pitch < 0.0F && rotationLength > 0.0) {
			double m = horizontalVelocity * (double) (-Mth.sin(pitch)) * 0.04;
			velocity = velocity.add(-rotationVector.x * m / rotationLength, m * 3.2, -rotationVector.z * m / rotationLength);
		}

		if (rotationLength > 0.0) {
			velocity = velocity.add((rotationVector.x / rotationLength * horizontalVelocity - velocity.x) * 0.1, 0.0, (rotationVector.z / rotationLength * horizontalVelocity - velocity.z) * 0.1);
		}

		if (isInLiquid()) {
			setDeltaMovement(velocity.multiply(0.8F, 0.8F, 0.8F));

			if (getDeltaMovement().horizontalDistanceSqr() < 0.0016) {
				if (passenger != null)
					passenger.stopRiding();

				this.giveOrDrop(passenger);
				return;
			}
		} else {
			setDeltaMovement(velocity.multiply(0.985F, 0.96F, 0.985F));
		}

		if (passenger instanceof ServerPlayer player && this.tickCount > 20 * 5 && this.soundTimer++ % 20 * 4 == 0) {
			double l = getDeltaMovement().length();
			if (l > 0.05)
				player.getBukkitEntity().playSound(Sound.sound(TrappedNewbieSoundKeys.AMBIENT_WIND, Sound.Source.AMBIENT, (float) Math.clamp(l, 0.05, 0.8), this.random.nextFloat() * 0.2F + 0.9F));
		}

		net.minecraft.world.item.ItemStack item = getItemStack();
		if (item.getItem() instanceof HangGliderItem gliderItem) {
			gliderItem.tickGlider(level, this, item);
		}

		move(MoverType.SELF, getDeltaMovement());
		if (this.horizontalCollision && passenger != null) {
			double m = getDeltaMovement().horizontalDistance();
			double n = horizontalVelocity - m;
			float o = (float) (n * 10.0 - 3.0);
			if (o > 0.0F) {
				if (passenger instanceof LivingEntity livingEntity)
					playSound(livingEntity.getFallDamageSound((int) o), 1F, 1F);
				passenger.hurtServer(level, damageSources().flyIntoWall(), o);
			}
		}

		int i = ++this.damageTimer;
		if ((i % dmgTimeout == 0 && (i / dmgFrequency) % dmgTimeout == 0) || isOnFire()) {
			damageStack(dmg);
			gameEvent(GameEvent.ELYTRA_GLIDE);
		}
	}

	@Override
	public void onBelowWorld() {
		giveOrDrop(getFirstPassenger());
	}

	private boolean hasHighGravity(ServerLevel level) {
		return level.dimension() == Level.END;
	}

	private boolean hasLowGravity(ServerLevel level) {
		return TrappedNewbie.limboWorld() == level.getWorld();
	}

	private void updateRoll(float roll) {
		this.roll = roll;
		if (!(getBukkitEntity() instanceof org.bukkit.entity.ItemDisplay entity)) return;

		Transformation transformation = entity.getTransformation();
		transformation.getLeftRotation().set(new Quaternionf().rotateZ(this.roll));
		entity.setTransformation(transformation);
		entity.setInterpolationDelay(0);
	}

	private void giveOrDrop(@Nullable Entity passenger) {
		if (isRemoved()) return;

		discard();

		if (!(getBukkitEntity() instanceof Glider glider)) return;

		ItemStack item = glider.getItemStack();
		if (item.isEmpty()) return;

		if (passenger == null)
			level().getWorld().dropItemNaturally(CraftLocation.toBukkit(position(), level()), item);
		else
			InventoryUtil.replaceOrAdd(passenger.getBukkitEntity(), item);

		glider.setItemStack(null);
	}

	public void damageStack(int damage) {
		if (this.noItemDamage) return;
		if (!(level() instanceof ServerLevel level)) return;
		if (!(getBukkitEntity() instanceof Glider glider)) return;

		ItemStack item = glider.getItemStack();
		net.minecraft.world.item.ItemStack itemStack = getItemStack();
		itemStack.hurtAndBreak(damage, level, getFirstPassenger() instanceof LivingEntity livingEntity ? livingEntity : null, (nmsItem) -> {
			if (getFirstPassenger() != null)
				getFirstPassenger().stopRiding();

			new ParticleBuilder(Particle.ITEM)
				.data(item)
				.location(level.getWorld(), getX(), getY() + 0.5, getZ())
				.offset(1, 1, 1)
				.count(80)
				.extra(0.1)
				.spawn();

			discard();
		});
		setItemStack(itemStack);
	}

	@Override
	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		if (source.is(DamageTypeTags.CAN_BREAK_ARMOR_STAND)) {
			if (this.tickCount - this.lastAttack > 30) {
				this.attacks = 0;
			} else if (this.attacks == 2) {
				giveOrDrop(getFirstPassenger());
				discard();
			}

			this.attacks++;
			this.lastAttack = this.tickCount;
			var sign = Math.signum(this.roll);
			if (sign == 0) sign = 1;

			updateRoll(-sign * Math.min(Math.abs(this.roll + 0.2F), 1));
			return true;
		} else if (source.is(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS)) {
			giveOrDrop(getFirstPassenger());
			discard();
			return true;
		} else if (source.is(DamageTypeTags.IGNITES_ARMOR_STANDS)) {
			damageStack((int) (amount * 2));
			return true;
		}

		return false;
	}

	@Override
	public InteractionResult interact(net.minecraft.world.entity.player.Player player, InteractionHand hand) {
		Entity passenger = getFirstPassenger();
		if (passenger != null) {
			if (this.hitbox != null && passenger.getBukkitEntity().getUniqueId().equals(this.hitbox.getUniqueId())) {
				this.hitbox.remove();
				this.hitbox = null;
			} else {
				return InteractionResult.FAIL;
			}
		}

		if (!player.startRiding(this))
			return InteractionResult.FAIL;

		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemoval(Entity.RemovalReason reason) {
		super.onRemoval(reason);
		if (this.hitbox != null) {
			this.hitbox.remove();
			this.hitbox = null;
		}
	}

	@Override
	public void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		this.noItemDamage = input.getBooleanOr(NO_ITEM_DAMAGE_TAG, false);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putBoolean(NO_ITEM_DAMAGE_TAG, this.noItemDamage);
	}

	public boolean hasLanded() {
		return this.hasLanded || this.isRemoved();
	}

}
