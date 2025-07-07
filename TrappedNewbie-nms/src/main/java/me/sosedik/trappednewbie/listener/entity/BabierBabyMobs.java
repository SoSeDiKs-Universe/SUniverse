package me.sosedik.trappednewbie.listener.entity;

import com.destroystokyo.paper.entity.ai.PaperGoal;
import com.destroystokyo.paper.entity.ai.VanillaGoal;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.kiterino.event.entity.EntityStartUsingItemEvent;
import me.sosedik.miscme.api.event.entity.EntityTurnBabyEvent;
import me.sosedik.miscme.listener.entity.MoreBabyMobs;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.impl.entity.ai.NonBowRangedAttackGoal;
import me.sosedik.trappednewbie.impl.entity.ai.TrumpetAttackGoal;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftAbstractSkeleton;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.AbstractSkeleton;
import org.bukkit.entity.Bogged;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Stray;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Adds some unique traits to some baby mobs
 */
public class BabierBabyMobs implements Listener {

	private static final String STRAY_PROJECTILE_MARKER = "stray_projectile";
	private static final String BOGGED_PROJECTILE_MARKER = "bogged_projectile";

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTurn(EntityTurnBabyEvent event) {
		applyTraits(event.getEntity());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onStart(EntityStartUsingItemEvent event) {
		if (!(event.getEntity() instanceof AbstractSkeleton skeleton)) return;
		if (!(skeleton instanceof Bogged || skeleton instanceof Stray)) return;
		if (!MoreBabyMobs.isNonVanillaBaby(skeleton)) return;
		if (event.getItem().getType() == Material.BOW) return;

		event.setCancelled(true);

		LivingEntity target = skeleton.getTarget();
		if (target == null) return;
		if (skeleton.getArrowCooldown() > 0) {
			skeleton.setArrowCooldown(skeleton.getArrowCooldown() - 1);
			return;
		}

		skeleton.setArrowCooldown(30);
		skeleton.swingMainHand();
		skeleton.emitSound(Sound.ENTITY_SNOW_GOLEM_SHOOT, 1F, 0.4F / ((float) Math.random() * 0.4F + 0.8F));

		if (skeleton instanceof Bogged) {
			skeleton.launchProjectile(Snowball.class, null, snowball -> {
				snowball.setItem(new ItemStack(Math.random() > 0.5 ? Material.RED_MUSHROOM : Material.BROWN_MUSHROOM));
				NBT.modifyPersistentData(snowball, (Consumer<ReadWriteNBT>) nbt -> nbt.setBoolean(BOGGED_PROJECTILE_MARKER, true));
			});
		} else {
			skeleton.launchProjectile(Snowball.class, null,
				snowball -> NBT.modifyPersistentData(snowball, (Consumer<ReadWriteNBT>) nbt -> nbt.setBoolean(STRAY_PROJECTILE_MARKER, true)));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onHit(ProjectileHitEvent event) {
		if (!(event.getHitEntity() instanceof LivingEntity target)) return;
		if (!(event.getEntity() instanceof Snowball snowball)) return;

		Double damage = NBT.getPersistentData(snowball, nbt -> {
			if (nbt.getOrDefault(STRAY_PROJECTILE_MARKER, false)) return 0.5;
			if (nbt.getOrDefault(BOGGED_PROJECTILE_MARKER, false)) return 2D;
			return null;
		});
		if (damage == null) return;

		LivingEntity shooter = snowball.getShooter() instanceof LivingEntity livingEntity ? livingEntity : null;
		DamageSource.Builder builder = DamageSource.builder(DamageType.THROWN).withDirectEntity(snowball).withDamageLocation(snowball.getLocation());
		if (shooter != null) builder = builder.withCausingEntity(shooter);
		target.damage(damage, builder.build());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof WitherSkeleton skeleton)) return;
		if (!MoreBabyMobs.isNonVanillaBaby(skeleton)) return;

		EquipmentSlot skullHand = getItemHand(skeleton, Material.WITHER_SKELETON_SKULL);
		if (skullHand == null) return;

		Player player = event.getPlayer();
		EquipmentSlot roseHand = getItemHand(player, Material.WITHER_ROSE);
		if (roseHand == null) return;

		player.swingHand(roseHand);
		ItemStack roseItem = player.getInventory().getItem(roseHand);

		skeleton.swingMainHand();
		skeleton.getWorld().dropItemNaturally(skeleton.getLocation(), skeleton.getEquipment().getItem(skullHand));
		skeleton.getEquipment().setItem(skullHand, roseItem.asOne());

		roseItem.subtract();

		updateWitherSkeletonGoals(skeleton);
	}
	
	private @Nullable EquipmentSlot getItemHand(LivingEntity entity, Material type) {
		if (entity.getEquipment() == null) return null;
		if (entity.getEquipment().getItemInMainHand().getType() == type) return EquipmentSlot.HAND;
		if (entity.getEquipment().getItemInOffHand().getType() == type) return EquipmentSlot.OFF_HAND;
		return null;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof AbstractSkeleton skeleton)) return;
		if (!MoreBabyMobs.isNonVanillaBaby(skeleton)) return;

		event.setDroppedExp((int) (event.getDroppedExp() * 2.5));
	}

	private void applyTraits(LivingEntity entity) {
		if (entity instanceof Bogged skeleton) {
			var nms = ((CraftAbstractSkeleton) skeleton).getHandle();
			Material main = Math.random() > 0.5 ? Material.RED_MUSHROOM : Material.BROWN_MUSHROOM;
			Material off = main == Material.RED_MUSHROOM ? Material.BROWN_MUSHROOM : Material.RED_MUSHROOM;
			skeleton.getEquipment().setItemInMainHand(new ItemStack(main));
			skeleton.getEquipment().setItemInOffHand(new ItemStack(off));
			skeleton.getEquipment().setItemInMainHandDropChance(0.2F);
			skeleton.getEquipment().setItemInOffHandDropChance(0.2F);
			Bukkit.getMobGoals().removeGoal(skeleton, VanillaGoal.RANGED_BOW_ATTACK);
			Bukkit.getMobGoals().addGoal(skeleton, 1, new PaperGoal<>(new NonBowRangedAttackGoal<>(nms, 1, 30, 15F)));
		} else if (entity instanceof Stray skeleton) {
			var nms = ((CraftAbstractSkeleton) skeleton).getHandle();
			skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.SNOWBALL));
			skeleton.getEquipment().setItemInMainHandDropChance(0.2F);
			Bukkit.getMobGoals().removeGoal(skeleton, VanillaGoal.RANGED_BOW_ATTACK);
			Bukkit.getMobGoals().addGoal(skeleton, 1, new PaperGoal<>(new NonBowRangedAttackGoal<>(nms, 1, 30, 15F)));
		} else if (entity instanceof WitherSkeleton skeleton) {
			if (getItemHand(skeleton, Material.WITHER_ROSE) == null) {
				var nms = ((CraftAbstractSkeleton) skeleton).getHandle();
				skeleton.getEquipment().setItemInMainHand(new ItemStack(Material.WITHER_SKELETON_SKULL));
				skeleton.getEquipment().setItemInMainHandDropChance(1F);
				Bukkit.getMobGoals().removeGoal(skeleton, VanillaGoal.NEAREST_ATTACKABLE);
				Bukkit.getMobGoals().removeGoal(skeleton, VanillaGoal.HURT_BY);
				Bukkit.getMobGoals().addGoal(skeleton, 3, new PaperGoal<>(new AvoidEntityGoal<>(nms, net.minecraft.world.entity.player.Player.class, 6F, 1, 1.2)));
			} else {
				updateWitherSkeletonGoals(skeleton);
			}
		} else if (entity instanceof Skeleton skeleton) {
			var nms = ((CraftAbstractSkeleton) skeleton).getHandle();
			skeleton.getEquipment().setItemInMainHand(new ItemStack(TrappedNewbieItems.TRUMPET));
			skeleton.getEquipment().setItemInMainHandDropChance(0.085F);
			Bukkit.getMobGoals().removeGoal(skeleton, VanillaGoal.RANGED_BOW_ATTACK);
			Bukkit.getMobGoals().removeGoal(skeleton, VanillaGoal.SKELETON_MELEE);
			Bukkit.getMobGoals().addGoal(skeleton, 1, new PaperGoal<>(new TrumpetAttackGoal<>(nms, 1, 40, 6F)));
		}
	}

	private void updateWitherSkeletonGoals(WitherSkeleton skeleton) {
		var nms = ((CraftAbstractSkeleton) skeleton).getHandle();
		Bukkit.getMobGoals().removeGoal(skeleton, VanillaGoal.AVOID_ENTITY);
		Bukkit.getMobGoals().addGoal(skeleton, 1, new PaperGoal<>(new HurtByTargetGoal(nms)));
		Bukkit.getMobGoals().addGoal(skeleton, 3, new PaperGoal<>(new AvoidEntityGoal<>(nms, net.minecraft.world.entity.animal.wolf.Wolf.class, 6F, 1, 1.2)));
		Bukkit.getMobGoals().addGoal(skeleton, 3, new PaperGoal<>(new NearestAttackableTargetGoal<>(nms, net.minecraft.world.entity.animal.Turtle.class, 10, true, false, net.minecraft.world.entity.animal.Turtle.BABY_ON_LAND_SELECTOR)));
	}

}
