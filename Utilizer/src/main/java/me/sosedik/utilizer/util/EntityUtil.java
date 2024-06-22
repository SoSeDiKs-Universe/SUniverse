package me.sosedik.utilizer.util;

import org.bukkit.HeightMap;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EntityUtil {

	private EntityUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final List<Predicate<LivingEntity>> EXTRA_PLAYER_VISIBILITY_RULES = new ArrayList<>();

	/**
	 * Adds a rule to exempt the entity from being considered in darkness
	 *
	 * @param rule rule
	 */
	public static void addDarknessExemptRule(@NotNull Predicate<@NotNull LivingEntity> rule) {
		EXTRA_PLAYER_VISIBILITY_RULES.add(rule);
	}

	/**
	 * Checks whether the entity is in darkness
	 *
	 * @param entity entity
	 * @return whether the entity is in darkness
	 */
	public static boolean isInDarkness(@NotNull LivingEntity entity) {
		if (entity.getEyeLocation().getBlock().getLightLevel() > 3) return false;
		if (isInOpenEnd(entity)) return false;
		if (isHoldingALightSource(entity, EquipmentSlot.HAND)) return false;
		if (isHoldingALightSource(entity, EquipmentSlot.OFF_HAND)) return false;

		for (Predicate<LivingEntity> visibilityRule : EXTRA_PLAYER_VISIBILITY_RULES) {
			if (visibilityRule.test(entity))
				return false;
		}

		return true;
	}

	private static boolean isInOpenEnd(@NotNull LivingEntity entity) {
		if (entity.getWorld().getEnvironment() != World.Environment.THE_END) return false;
		return entity.getLocation().getY() >= entity.getLocation().toHighestLocation(HeightMap.MOTION_BLOCKING_NO_LEAVES).getY();
	}

	/**
	 * Checks whether the entity is holding a light source
	 *
	 * @param entity entity
	 * @param slot slot
	 * @return whether the entity is holding a light source
	 */
	public static boolean isHoldingALightSource(@NotNull LivingEntity entity, @NotNull EquipmentSlot slot) {
		EntityEquipment equipment = entity.getEquipment();
		if (equipment == null) return false;

		ItemStack item = equipment.getItem(slot);
		return ItemUtil.isLightSource(item.getType());
	}

	/**
	 * Clears the player as a target from nearby mobs
	 *
	 * @param player player
	 */
	public static void clearTargets(@NotNull Player player) {
		clearTargets(player, 48);
	}

	/**
	 * Clears the player as a target from nearby mobs
	 *
	 * @param player player
	 */
	public static void clearTargets(@NotNull Player player, int radius) {
		player.getLocation().getNearbyEntitiesByType(Mob.class, radius, mob -> mob.getTarget() == player).forEach(mob -> mob.setTarget(null));
	}

	/**
	 * Tries to find entity's player damager
	 *
	 * @param entity damaged entity
	 * @return player damager
	 */
	public static @Nullable Player getDamager(@NotNull Entity entity) {
		if (entity instanceof LivingEntity livingEntity && livingEntity.getKiller() != null) return livingEntity.getKiller();
		if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamage)) return null;
		if (lastDamage.getDamager() instanceof Player damager) return damager;
		if (lastDamage.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) return shooter;
		return null;
	}

	/**
	 * Checks whether this entity type is immune to fire
	 *
	 * @param type entity type
	 * @return whether this entity type is immune to fire
	 */
	public static boolean isFireImmune(@NotNull EntityType type) {
		return switch (type) {
			case WITHER, ENDER_DRAGON, WITHER_SKULL,
					BLAZE, GHAST, MAGMA_CUBE, STRIDER,
					WITHER_SKELETON, ZOGLIN, ZOMBIFIED_PIGLIN,
					WARDEN -> true;
			default -> false;
		};
	}

}
