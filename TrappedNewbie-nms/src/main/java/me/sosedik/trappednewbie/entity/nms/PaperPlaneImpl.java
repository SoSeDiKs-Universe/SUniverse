package me.sosedik.trappednewbie.entity.nms;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEntities;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.entity.api.PaperPlane;
import me.sosedik.utilizer.impl.item.modifier.GlowingItemModifier;
import me.sosedik.utilizer.util.LocationUtil;
import net.kyori.adventure.util.TriState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class PaperPlaneImpl extends ThrowableItemProjectile {

	private static final Item FAKE_DISPLAY_ITEM = BuiltInRegistries.ITEM.getValue(PaperAdventure.asVanilla(TrappedNewbieItems.MATERIAL_AIR.key()));
	private static final Item DEFAULT_PICKUP_ITEM = BuiltInRegistries.ITEM.getValue(PaperAdventure.asVanilla(TrappedNewbieItems.PAPER_PLANE.key()));

	private static final int LOOP_DURATION_TICKS = 40;
	private static final double LOOP_INTENSITY = 1D;
	private static final double MAX_INITIAL_SPEED = 0.5;

	private @Nullable ItemDisplay itemDisplay;
	private @Nullable Interaction interaction;
	public ItemStack pickupItemStack;

	// Sinusoidal oscillation parameters for normal flight
	private final double phaseOffsetX = 0.05 * Math.random() * Math.PI * 2;
	private final double phaseOffsetY = Math.random() * Math.PI * 2;
	private final double amplitudeX = 0.05 + Math.random() * 0.1;
	private final double amplitudeY = 0.05 + Math.random() * 0.1;
	private final double frequencyX = 0.1 + Math.random() * 0.1;
	private final double frequencyY = 0.5 + Math.random() * 0.1;

	// Movement data
	private @Nullable Vector baseVelocity = null;
	private Vector currentVelocity = new Vector();
	private float currentRotationYaw;
	private float currentRotationPitch;
	private int upwardTicks = 0;
	private int rainTicks = 0;

	// Barrel role mode data
	private boolean loopActive = false;
	private int loopTicks = 0;
	private Vector loopForward = new Vector(); // track the original horizontal direction
	private double loopSpeed = 1; // maintain speed when starting a loop

	public PaperPlaneImpl(EntityType<? extends PaperPlaneImpl> entityType, Level level) {
		super(entityType, level);
		this.pickupItemStack = new ItemStack(DEFAULT_PICKUP_ITEM);
		setItem(new ItemStack(FAKE_DISPLAY_ITEM)); // Forcefully override to send despite being default
		applyEntityData();
	}

	public PaperPlaneImpl(ServerLevel level, LivingEntity owner, ItemStack item) {
		super(TrappedNewbieEntities.PAPER_PLANE, owner, level, new ItemStack(FAKE_DISPLAY_ITEM));
		this.pickupItemStack = item;
		applyEntityData();
	}

	public PaperPlaneImpl(Level level, double x, double y, double z, ItemStack item) {
		super(TrappedNewbieEntities.PAPER_PLANE, x, y, z, level, new ItemStack(FAKE_DISPLAY_ITEM));
		this.pickupItemStack = item;
		applyEntityData();
	}

	private void applyEntityData() {
		Entity entity = getBukkitEntity();
		entity.setVisibleByDefault(false);
		if (NBT.get(this.pickupItemStack.asBukkitMirror(), nbt -> (boolean) nbt.getOrDefault(PaperPlane.BLAZIFIED_TAG, false))) {
			entity.setImmuneToFire(true);
			entity.setFireTicks(Integer.MAX_VALUE);
		}
	}

	@Override
	public void tick() {
		if (this.baseVelocity == null)
			setupInitialData(getBukkitEntity().getVelocity());

		if (isAlive()) {
			if (this.itemDisplay == null || !this.itemDisplay.isValid()) {
				this.itemDisplay = getBukkitEntity().getWorld().spawn(getBukkitEntity().getLocation(), ItemDisplay.class, display -> {
					display.setPersistent(false);
					Transformation transformation = display.getTransformation();
					display.setTransformation(new Transformation(new Vector3f(0F, getBbHeight() + getEyeHeight(), 0F), transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation()));
					display.setTeleportDuration(1);
					updateDisplayItem(display);
				});
			}
			if (this.interaction == null || !this.interaction.isValid()) {
				this.interaction = getBukkitEntity().getWorld().spawn(getBukkitEntity().getLocation(), Interaction.class, interaction -> {
					interaction.setPersistent(false);
					interaction.setInteractionHeight(getBbHeight());
					interaction.setInteractionWidth(getBbWidth());
					assert this.itemDisplay != null;
					this.itemDisplay.addPassenger(interaction);
				});
			}
		}
		if (this.itemDisplay == null) return;
		if (this.interaction == null) return;

		Interaction.PreviousInteraction lastAttack = interaction.getLastAttack();
		if (lastAttack != null) {
			this.despawn(position(), EntityRemoveEvent.Cause.DROP);
			return;
		}

		if (isOnFire()) {
			this.despawn(position(), EntityRemoveEvent.Cause.DEATH);
			return;
		}

		if (isInWaterOrRain()) {
			rainTicks++;
			if (rainTicks > 30) {
				this.despawn(position(), EntityRemoveEvent.Cause.DEATH);
				return;
			}
		} else {
			rainTicks = 0;
		}

		if (upwardTicks > 0) upwardTicks--;

		// Triggering the barrel roll mode
		if (!loopActive && upwardTicks <= 0 && Math.random() < 0.005) {
			loopActive = true;
			loopTicks = 0;
			// Save the horizontal direction from the current speed
			Vector horizontal = currentVelocity.clone();
			horizontal.setY(0);
			if (horizontal.lengthSquared() == 0)
				horizontal = new Vector(1, 0, 0);
			loopForward = horizontal.normalize();
			loopSpeed = currentVelocity.length();
		}

		Vector targetVelocity;
		boolean aboveCampfire = CampfireBlock.isSmokeyPos(level(), blockPosition());
		if (loopActive) {
			// Barrel role active!
			loopTicks++;
			double rawProgress = (double) loopTicks / LOOP_DURATION_TICKS; // linear progress from 0 to 1
			// The easing function is used to smoothly start and end the loop
			double easedProgress = easeInOutQuad(rawProgress);
			double angle = easedProgress * 2 * Math.PI; // full circle

			// Forming a vector for the loop
			// targetVelocity = loopSpeed * [ cos(angle * loopIntensity) * loopForward + sin(angle * loopIntensity) * UP ]
			targetVelocity = loopForward.clone().multiply(Math.cos(angle * LOOP_INTENSITY));
			targetVelocity.add(new Vector(0, Math.sin(angle * LOOP_INTENSITY), 0));
			targetVelocity.multiply(loopSpeed);

			// If the loop is complete (linear progress >= 1) or we went over a campfire, turn off the barrel roll mode
			if (rawProgress >= 1.0 || aboveCampfire)
				loopActive = false;
		} else {
			// Normal flight: calculating sinusoidal oscillations
			double timeX = tickCount * frequencyX;
			double timeY = tickCount * frequencyY;
			double waveX = Math.sin(timeX + phaseOffsetX) * amplitudeX;
			double waveY = Math.sin(timeY + phaseOffsetY) * amplitudeY;
			Vector wobble = new Vector(waveX, waveY, 0);
			targetVelocity = baseVelocity.clone().add(wobble);

			if (aboveCampfire)
				upwardTicks = 8;

			// Apply gravity: the force of fall increases according to a quadratic dependence
			double gravity = upwardTicks > 0 ? -0.0008 : 0.00004;
			double drop = gravity * tickCount * tickCount;
			targetVelocity.setY(targetVelocity.getY() - drop);
		}

		// Interpolate the current speed to the target speed
		currentVelocity = lerp(currentVelocity, targetVelocity, 0.2);

		// Tick the usual projectile stuff, but revert movement
		Vec3 prePosition = position();
		setDeltaMovement(CraftVector.toVec3(currentVelocity));
		super.tick();
		setDeltaMovement(Vec3.ZERO);
		setPos(prePosition);

		if (!isAlive()) return;

		// Calculate target steering angles based on current speed
		double vx = currentVelocity.getX();
		double vy = currentVelocity.getY();
		double vz = currentVelocity.getZ();
		double horiz = Math.sqrt(vx * vx + vz * vz);
		float targetPitch = (float) (-Math.toDegrees(Math.atan2(vy, horiz)));
		float targetYaw = (float) (Math.toDegrees(Math.atan2(vz, vx)) - 90);

		// Interpolate edges
		currentRotationYaw = lerp(currentRotationYaw, targetYaw, 0.2);
		currentRotationPitch = lerp(currentRotationPitch, targetPitch, 0.2);

		// Update position!
		Location newLocation = getBukkitEntity().getLocation().add(currentVelocity);
		newLocation.setRotation(currentRotationYaw, currentRotationPitch);
		setPos(CraftLocation.toVec3(newLocation));
		assert this.itemDisplay != null;
		LocationUtil.smartTeleport(this.itemDisplay, newLocation, false);
	}

	private void setupInitialData(Vector baseVelocity) {
		if (baseVelocity.length() > MAX_INITIAL_SPEED)
			baseVelocity = baseVelocity.normalize().multiply(MAX_INITIAL_SPEED);

		Vector randomOffset = new Vector(
			(Math.random() - 0.5) * 0.1,
			(Math.random() - 0.5) * 0.1,
			(Math.random() - 0.5) * 0.1
		);

		this.baseVelocity = baseVelocity.add(randomOffset);
		this.currentVelocity = baseVelocity.clone();

		double vx = baseVelocity.getX();
		double vy = baseVelocity.getY();
		double vz = baseVelocity.getZ();
		double horizontalSpeed = Math.sqrt(vx * vx + vz * vz);
		this.currentRotationYaw = (float) (Math.toDegrees(Math.atan2(vz, vx)) - 90);
		this.currentRotationPitch = (float) (-Math.toDegrees(Math.atan2(vy, horizontalSpeed)));
	}

	public void updateDisplayItem() {
		if (this.itemDisplay != null)
			updateDisplayItem(this.itemDisplay);
	}

	private void updateDisplayItem(ItemDisplay display) {
		org.bukkit.inventory.ItemStack itemStack = this.pickupItemStack.asBukkitCopy();
		display.setItemStack(itemStack);
		display.setGlowing(NBT.get(itemStack, nbt -> (boolean) nbt.getOrDefault(GlowingItemModifier.GLOW_MODIFIER_KEY, false)));
		display.setVisualFire(TriState.byBoolean(NBT.get(itemStack, nbt -> (boolean) nbt.getOrDefault(PaperPlane.BLAZIFIED_TAG, false)))); // TODO does not render......
		if (itemStack.hasData(DataComponentTypes.DYED_COLOR)) {
			Color color = Objects.requireNonNull(itemStack.getData(DataComponentTypes.DYED_COLOR)).color();
			display.setGlowColorOverride(color);
		} else {
			display.setGlowColorOverride(null);
		}
	}

	private Vector lerp(Vector start, Vector end, double t) {
		return start.clone().multiply(1 - t).add(end.clone().multiply(t));
	}

	private float lerp(float a, float b, double t) {
		return a + (float) ((b - a) * t);
	}

	private double easeInOutQuad(double t) {
		return t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
	}

	@Override
	public void onRemoval(RemovalReason reason) {
		if (this.interaction != null) this.interaction.remove();
		if (this.itemDisplay != null) this.itemDisplay.remove();

		super.onRemoval(reason);
	}

	@Override
	public Item getDefaultItem() {
		return FAKE_DISPLAY_ITEM;
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!isAlive()) return;

		this.despawn(result.getLocation(), EntityRemoveEvent.Cause.HIT);
	}

	private void despawn(Vec3 loc, EntityRemoveEvent.Cause cause) {
		this.discard(cause);
		boolean blazed = NBT.get(this.pickupItemStack.asBukkitMirror(), nbt -> (boolean) nbt.getOrDefault(PaperPlane.BLAZIFIED_TAG, false));
		boolean waterBlazedDeath = blazed && cause == EntityRemoveEvent.Cause.DEATH && isInWaterOrRain();
		if (!isOnFire() && (!blazed || waterBlazedDeath)) {
			boolean fragile = NBT.get(this.pickupItemStack.asBukkitMirror(), nbt -> (boolean) nbt.getOrDefault(PaperPlane.FRAGILE_TAG, false));
			getBukkitEntity().getWorld().dropItemNaturally(CraftLocation.toBukkit(loc), fragile ? org.bukkit.inventory.ItemStack.of(Material.PAPER) : this.pickupItemStack.asBukkitMirror());
		} else {
			getBukkitEntity().emitSound(Sound.BLOCK_FIRE_EXTINGUISH, 1F, 1F);
			getBukkitEntity().getWorld().spawnParticle(Particle.SMOKE, getBukkitEntity().getLocation(), 3, 0.2, 0.2, 0.2, 0.01);
		}
	}

	@Override
	public void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		this.setItem(input.read("Item", ItemStack.CODEC).orElseGet(() -> new ItemStack(DEFAULT_PICKUP_ITEM)));
	}

	@Override
	protected double getDefaultGravity() {
		return 0D;
	}

}
