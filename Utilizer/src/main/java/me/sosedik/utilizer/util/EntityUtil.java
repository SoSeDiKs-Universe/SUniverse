package me.sosedik.utilizer.util;

import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Turtle;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@NullMarked
public class EntityUtil {

	private EntityUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * How far the player's hand can reach
	 */
	public static final int PLAYER_REACH = 5;
	public static final int DARKNESS_LIGHT_LEVEL = 3;

	/**
	 * Covers entities that should be excluded from interactions
	 */
	public static Predicate<LivingEntity> IGNORE_INTERACTION = (entity) -> entity instanceof ArmorStand armorStand && armorStand.isMarker();

	private static final List<Predicate<LivingEntity>> EXTRA_PLAYER_VISIBILITY_RULES = new ArrayList<>();

	/**
	 * Tries to get items tag
	 *
	 * @param key key
	 * @return items tag
	 */
	public static Tag<EntityType> entityTag(NamespacedKey key) {
		return Objects.requireNonNull(Bukkit.getTag(Tag.REGISTRY_ENTITY_TYPES, key, EntityType.class), () -> "Couldn't find entity type tag " + key);
	}

	/**
	 * Adds a rule to exempt the entity from being considered in darkness
	 *
	 * @param rule rule
	 */
	public static void addDarknessExemptRule(Predicate<LivingEntity> rule) {
		EXTRA_PLAYER_VISIBILITY_RULES.add(rule);
	}

	/**
	 * Checks whether the entity is in darkness
	 *
	 * @param entity entity
	 * @return whether the entity is in darkness
	 */
	public static boolean isInDarkness(LivingEntity entity) {
		if (entity.getEyeLocation().getBlock().getLightLevel() > DARKNESS_LIGHT_LEVEL) return false;
		if (isInOpenEnd(entity)) return false;
		if (isHoldingALightSource(entity, EquipmentSlot.HAND)) return false;
		if (isHoldingALightSource(entity, EquipmentSlot.OFF_HAND)) return false;

		for (Predicate<LivingEntity> visibilityRule : EXTRA_PLAYER_VISIBILITY_RULES) {
			if (visibilityRule.test(entity))
				return false;
		}

		return true;
	}

	private static boolean isInOpenEnd(LivingEntity entity) {
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
	public static boolean isHoldingALightSource(LivingEntity entity, EquipmentSlot slot) {
		EntityEquipment equipment = entity.getEquipment();
		if (equipment == null) return false;

		ItemStack item = equipment.getItem(slot);
		return ItemUtil.isLightSource(item);
	}

	/**
	 * Checks whether the entity can see (i.e. is not in darkness or has night vision)
	 *
	 * @param entity entity
	 * @return whether the entity can see
	 */
	public static boolean canSee(LivingEntity entity) {
		return !isInDarkness(entity) || entity.hasPotionEffect(PotionEffectType.NIGHT_VISION);
	}

	/**
	 * Clears the player as a target from nearby mobs
	 *
	 * @param player player
	 */
	public static void clearTargets(Player player) {
		clearTargets(player, 48);
	}

	/**
	 * Clears the player as a target from nearby mobs
	 *
	 * @param player player
	 */
	public static void clearTargets(Player player, int radius) {
		player.getLocation().getNearbyEntitiesByType(Mob.class, radius, mob -> mob.getTarget() == player).forEach(mob -> mob.setTarget(null));
	}

	/**
	 * Tries to find entity's last damager
	 *
	 * @param entity damaged entity
	 * @return player damager
	 */
	public static @Nullable Entity getCausingDamager(Entity entity) {
		if (entity instanceof LivingEntity livingEntity && livingEntity.getKiller() != null) return livingEntity.getKiller();
		if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamage)) return null;
		if (lastDamage.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) return shooter;
		return lastDamage.getDamager();
	}

	/**
	 * Tries to find entity's last direct damager
	 *
	 * @param entity damaged entity
	 * @return player damager
	 */
	public static @Nullable Entity getDirectDamager(Entity entity) {
		if (entity instanceof LivingEntity livingEntity && livingEntity.getKiller() != null) return livingEntity.getKiller();
		if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamage)) return null;
		return lastDamage.getDamager();
	}

	/**
	 * Checks whether this entity type is immune to fire
	 *
	 * @param type entity type
	 * @return whether this entity type is immune to fire
	 */
	public static boolean isFireImmune(EntityType type) {
		return switch (type) {
			case WITHER, ENDER_DRAGON, WITHER_SKULL,
					BLAZE, GHAST, MAGMA_CUBE, STRIDER,
					WITHER_SKELETON, ZOGLIN, ZOMBIFIED_PIGLIN,
					WARDEN -> true;
			default -> false;
		};
	}

	/**
	 * Checks whether entity was naturally spawned
	 * (including spawner and spawner egg)
	 *
	 * @param entity entity
	 * @return whether entity was naturally spawned
	 */
	public static boolean isNaturallySpawned(Entity entity) {
		CreatureSpawnEvent.SpawnReason spawnReason = entity.getEntitySpawnReason();
		return spawnReason == CreatureSpawnEvent.SpawnReason.NATURAL
			|| spawnReason == CreatureSpawnEvent.SpawnReason.DEFAULT
			|| spawnReason == CreatureSpawnEvent.SpawnReason.SPAWNER
			|| spawnReason == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG;
	}

	/**
	 * Gets the required jump height.
	 * Adapted only for a default height player.
	 *
	 * @param height height
	 * @return jump height
	 */
	public static double getJumpHeight(double height) {
		return Math.sqrt(2 * height * 0.08);
	}

	/**
	 * Checks whether the entity is a water mob
	 *
	 * @param entity entity
	 * @return whether the entity is a water mob
	 */
	public static boolean isWaterMob(@Nullable LivingEntity entity) {
		return entity instanceof WaterMob || entity instanceof Turtle || entity instanceof Drowned;
	}

	/**
	 * Checks whether the entity is in the air
	 *
	 * @param entity entity
	 * @return whether the entity is in the air
	 */
	public static boolean isInAirLazy(LivingEntity entity) {
		if (entity.isOnGround()) return false;
		if (entity.isClimbing()) return false;
		if (entity.isGliding()) return false;
		if (entity.isRiptiding()) return false;
		if (entity.isSwimming()) return false;
		if (entity.isInsideVehicle()) return false;
		return entity.getMovementAffectingBlock().isEmpty();
	}

	/**
	 * Checks whether the damage is a fire damage
	 *
	 * @param cause cause
	 * @return whether the damage is a fire damage
	 */
	public static boolean isFireDamageCause(EntityDamageEvent.DamageCause cause) {
		return cause == EntityDamageEvent.DamageCause.FIRE
				|| cause == EntityDamageEvent.DamageCause.FIRE_TICK
				|| cause == EntityDamageEvent.DamageCause.LAVA
				|| cause == EntityDamageEvent.DamageCause.HOT_FLOOR;
	}

	/**
	 * Sets mob's target after calling target event
	 *
	 * @param mob mob
	 * @param target target
	 */
	public static void setTarget(Mob mob, LivingEntity target) {
		EntityTargetEvent event = new EntityTargetLivingEntityEvent(mob, target, EntityTargetEvent.TargetReason.CUSTOM);
		if (event.callEvent() && event.getTarget() instanceof LivingEntity living)
			mob.setTarget(living);
	}

}
