package me.sosedik.trappednewbie.entity.nms;

import de.tr7zw.nbtapi.NBT;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEntities;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.entity.api.PaperPlane;
import me.sosedik.utilizer.impl.item.modifier.GlowingItemModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.phys.HitResult;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public class PaperPlaneImpl extends ThrowableItemProjectile {

	private static final Item FAKE_DISPLAY_ITEM = BuiltInRegistries.ITEM.getValue(PaperAdventure.asVanilla(TrappedNewbieItems.MATERIAL_AIR.key()));
	private static final Item DEFAULT_PICKUP_ITEM = BuiltInRegistries.ITEM.getValue(PaperAdventure.asVanilla(TrappedNewbieItems.PAPER_PLANE.key()));

	private static final int LOOP_DURATION_TICKS = 40;

	private @Nullable ItemDisplay itemDisplay;
	public ItemStack pickupItemStack;
	private int noVehicleTicks = 0;

	public PaperPlaneImpl(EntityType<? extends PaperPlaneImpl> entityType, Level level) {
		super(entityType, level);
		this.pickupItemStack = new ItemStack(DEFAULT_PICKUP_ITEM);
		setItem(new ItemStack(FAKE_DISPLAY_ITEM)); // Forcefully override to send despite being default
	}

	public PaperPlaneImpl(ServerLevel level, LivingEntity owner, ItemStack item) {
		super(TrappedNewbieEntities.PAPER_PLANE, owner, level, new ItemStack(FAKE_DISPLAY_ITEM));
		this.pickupItemStack = item;
	}

	public PaperPlaneImpl(Level level, double x, double y, double z, ItemStack item) {
		super(TrappedNewbieEntities.PAPER_PLANE, x, y, z, level, new ItemStack(FAKE_DISPLAY_ITEM));
		this.pickupItemStack = item;
	}

	public void updateDisplayItem() {
		if (this.itemDisplay != null)
			updateDisplayItem(this.itemDisplay);
	}

	private void updateDisplayItem(ItemDisplay display) {
		org.bukkit.inventory.ItemStack itemStack = this.pickupItemStack.asBukkitCopy();
		display.setItemStack(itemStack);
		display.setGlowing(NBT.get(itemStack, nbt -> (boolean) nbt.getOrDefault(GlowingItemModifier.GLOW_MODIFIER_KEY, false)));
		display.setVisualFire(NBT.get(itemStack, nbt -> (boolean) nbt.getOrDefault(PaperPlane.BLAZIFIED_TAG, false))); // TODO does not render......
		if (itemStack.hasData(DataComponentTypes.DYED_COLOR)) {
			Color color = Objects.requireNonNull(itemStack.getData(DataComponentTypes.DYED_COLOR)).color();
			display.setGlowColorOverride(color);
		} else {
			display.setGlowColorOverride(null);
		}
	}

	@Override
	public void tick() {
		if (this.itemDisplay == null) {
			this.itemDisplay = getBukkitEntity().getWorld().spawn(getBukkitEntity().getLocation(), ItemDisplay.class, display -> {
				display.setPersistent(false);
				Transformation transformation = display.getTransformation();
				display.setTransformation(new Transformation(new Vector3f(0F, getEyeHeight(), 0F), transformation.getLeftRotation(), transformation.getScale(), transformation.getRightRotation()));
				getBukkitEntity().addPassenger(display);
				updateDisplayItem(display);
			});
		}
		if (this.noVehicleTicks > 0) this.noVehicleTicks--;
		if (!this.itemDisplay.isInsideVehicle()) {
			if (this.noVehicleTicks <= 0) {
				getBukkitEntity().addPassenger(this.itemDisplay);
			} else {
				Location loc = getBukkitEntity().getLocation();
				this.itemDisplay.teleport(loc.setDirection(loc.getDirection().multiply(-1)));
			}
		}
		super.tick();

		this.itemDisplay.setRotation(getYRot(), -getXRot());
	}

	@Override
	public void onRemoval(Entity.RemovalReason reason) {
		if (this.itemDisplay != null) {
			this.itemDisplay.remove();
		}

		super.onRemoval(reason);
	}

	@Override
	protected Item getDefaultItem() {
		return FAKE_DISPLAY_ITEM;
	}

	@Override
	protected void onHit(HitResult result) {
		super.onHit(result);
		if (!isAlive()) return;

		this.discard(org.bukkit.event.entity.EntityRemoveEvent.Cause.HIT);
		getBukkitEntity().getWorld().dropItemNaturally(CraftLocation.toBukkit(result.getLocation()), this.pickupItemStack.asBukkitMirror());
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.put("item", this.pickupItemStack.save(this.registryAccess()));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("item", 10)) {
			this.pickupItemStack = ItemStack.parse(this.registryAccess(), compound.getCompound("item")).orElse(new ItemStack(DEFAULT_PICKUP_ITEM));
		}
	}

	@Override
	protected double getDefaultGravity() {
		boolean aboveSmoke = CampfireBlock.isSmokeyPos(level(), blockPosition());
		if (aboveSmoke) {
			this.noVehicleTicks = 20;
			if (this.itemDisplay != null) this.itemDisplay.leaveVehicle();
		}
		return aboveSmoke ? -0.2 : 0.03;
	}

}
