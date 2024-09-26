package me.sosedik.requiem.feature;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.api.event.player.PlayerStartPossessingEntityEvent;
import me.sosedik.requiem.api.event.player.PlayerStopPossessingEntityEvent;
import me.sosedik.requiem.task.DynamicScaleTask;
import me.sosedik.requiem.task.PoseMimikingTask;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PossessingPlayer {

	private static final Set<UUID> POSSESSING = new HashSet<>();
	private static final String POSSESSED_TAG = "possessed";
	private static final String POSSESSED_PERSISTENT_TAG = "persistent";
	private static final String POSSESSED_ENTITY_DATA = "entity_data";

	/**
	 * Checks whether the player is possessing a mob
	 *
	 * @param player player
	 * @return whether the player is possessing a mob
	 */
	public static boolean isPossessing(@NotNull Player player) {
		boolean possessing = isPossessingSoft(player);
		if (possessing && getPossessed(player) == null) {
			possessing = false;
			stopPossessing(player);
		}
		return possessing;
	}

	/**
	 * Checks whether the player is possessing a mob without validation checks
	 *
	 * @param player player
	 * @return whether the player is possessing a mob
	 */
	public static boolean isPossessingSoft(@NotNull Player player) {
		return POSSESSING.contains(player.getUniqueId());
	}

	/**
	 * Makes the player to start possessing the mob
	 *
	 * @param player player
	 * @param entity possessed entity
	 */
	public static void startPossessing(@NotNull Player player, @NotNull LivingEntity entity) {
		if (isPossessing(player) && getPossessed(player) == entity) return;
		if (!entity.setRider(player)) return;

		POSSESSING.add(player.getUniqueId());

//		KittenAdvancements.FIRST_POSSESSION.awardAllCriteria(player); // TODO

		GhostyPlayer.clearGhost(player);
//		TemperaturedPlayer.of(player).addFlag(TempFlag.GHOST_IMMUNE); // TODO

		boolean persistent = entity.isPersistent();
		entity.setPersistent(true);

		NBT.modifyPersistentData(entity, nbt -> {
			nbt = nbt.getOrCreateCompound(POSSESSED_TAG);
			nbt.setBoolean(POSSESSED_PERSISTENT_TAG, persistent);
		});

//		EffectManager.addEffect(player, KittenEffects.ATTRITION, -1, 0); // TODO
		player.setInvisible(true);
		player.setInvulnerable(false); // Prevents mobs from targeting the player if true
		player.setRemainingAir(entity.getRemainingAir());

		new PlayerStartPossessingEntityEvent(player, entity).callEvent();

		new DynamicScaleTask(player, entity);
		new PoseMimikingTask(player, entity);

		Requiem.logger().info("Making " + player.getName() + " possess " + entity.getType().getKey());
	}

	/**
	 * Makes player to stop possessing a mob
	 *
	 * @param player player
	 */
	public static void stopPossessing(@NotNull Player player) {
		stopPossessing(player, getPossessed(player), false);
	}

	/**
	 * Makes player to stop possessing a mob
	 *
	 * @param player player
	 * @param riding possessed entity
	 */
	public static void stopPossessing(@NotNull Player player, @Nullable LivingEntity riding, boolean quit) {
		if (!isPossessingSoft(player)) return;

		if (quit) {
			if (riding != null) riding.remove();
		} else {
			if (riding != null && NBT.getPersistentData(riding, nbt -> nbt.hasTag(POSSESSED_TAG))) {
				NBT.modifyPersistentData(riding, nbt -> {
					nbt = nbt.getCompound(POSSESSED_TAG);
					if (nbt == null) return;

					boolean persistent = nbt.getBoolean(POSSESSED_PERSISTENT_TAG);
					nbt.removeKey(POSSESSED_PERSISTENT_TAG);
					riding.setPersistent(persistent);
				});
			}
		}

		player.setInvisible(false);

//		TemperaturedPlayer.of(player).removeFlag(TempFlag.GHOST_IMMUNE); // TODO

//		PlayerInventory playerInventory = player.getInventory();
//		playerInventory.clear();

		POSSESSING.remove(player.getUniqueId());

		if (riding != null) new PlayerStopPossessingEntityEvent(player, riding).callEvent();

		if (quit) return;

		player.leaveVehicle();

		if (riding != null) Requiem.logger().info("Making " + player.getName() + " no longer possess " + riding.getType().name());
		else Requiem.logger().info("Making " + player.getName() + " no longer possess an entity");
	}

	/**
	 * Gets the entity the player's currently possessing
	 *
	 * @param player player
	 * @return the possessed entity
	 */
	public static @Nullable LivingEntity getPossessed(@NotNull Player player) {
		if (!(player.getVehicle() instanceof LivingEntity riding)) return null;
		if (player != riding.getRider()) return null;
		return riding;
	}

	/**
	 * Migrates stats (including equipment) from player to the entity
	 *
	 * @param player player
	 * @param entity entity
	 */
	public static void migrateStatsToEntity(@NotNull Player player, @NotNull LivingEntity entity) {
		migrateInvToEntity(player, entity);

		entity.setPersistent(false);
		entity.setArrowsInBody(player.getArrowsInBody());
		entity.setFireTicks(player.getFireTicks());
		entity.setRemainingAir(player.getRemainingAir());
		if (entity instanceof Mob mob)
			mob.setLeftHanded(player.getMainHand() == MainHand.LEFT);
		if (entity instanceof Ageable ageable)
			ageable.setAdult();
	}

	private static void migrateInvToEntity(@NotNull Player player, @NotNull LivingEntity entity) {
		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		PlayerInventory playerInventory = player.getInventory();
		entityEquipment.clear();
		entityEquipment.setItemInMainHand(playerInventory.getItemInMainHand());
		entityEquipment.setItemInOffHand(playerInventory.getItemInOffHand());
		entityEquipment.setHelmet(playerInventory.getHelmet());
		entityEquipment.setChestplate(playerInventory.getChestplate());
		entityEquipment.setLeggings(playerInventory.getLeggings());
		entityEquipment.setBoots(playerInventory.getBoots());
	}

	/**
	 * Migrates stats (including equipment) from entity to the player
	 *
	 * @param player player
	 * @param entity entity
	 */
	public static void migrateStatsToPlayer(@NotNull Player player, @NotNull LivingEntity entity) {
		migrateInvFromEntity(player, entity);
//		addExtraControlItems(player); // TODO should also be soulbound and not droppable

		if (entity instanceof Bat)
			player.addPotionEffect(infinitePotionEffect(PotionEffectType.NIGHT_VISION));
		if (EntityUtil.isFireImmune(entity.getType()))
			player.addPotionEffect(infinitePotionEffect(PotionEffectType.FIRE_RESISTANCE));
	}

	private static void migrateInvFromEntity(@NotNull Player player, @NotNull LivingEntity entity) {
		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		PlayerInventory playerInventory = player.getInventory();
		playerInventory.clear();
		playerInventory.setItemInMainHand(entityEquipment.getItemInMainHand());
		playerInventory.setItemInOffHand(entityEquipment.getItemInOffHand());
		playerInventory.setHelmet(entityEquipment.getHelmet());
		playerInventory.setChestplate(entityEquipment.getChestplate());
		playerInventory.setLeggings(entityEquipment.getLeggings());
		playerInventory.setBoots(entityEquipment.getBoots());
	}

	private static @NotNull PotionEffect infinitePotionEffect(@NotNull PotionEffectType type) {
		return new PotionEffect(type, PotionEffect.INFINITE_DURATION, 0, false, false, false);
	}

	/**
	 * Checks whether the entity can be controlled by player
	 *
	 * @param player player
	 * @param entity entity
	 * @return whether the entity can be controlled by player
	 */
	public static boolean isAllowedForCapture(@NotNull Player player, @NotNull Entity entity) {
		// TODO llamas are not controllable for whatever reason
		if (true) return true; // TODO no.
		if (entity instanceof AbstractHorse) return false; // TODO "Horses" dismount player :L ; there's also dolphins and probably others
		if (entity instanceof Animals) return true;
		EntityType entityType = entity.getType();
		return switch (entityType) {
			case ZOMBIE, HUSK, ZOMBIE_VILLAGER, SKELETON, STRAY, DROWNED, WITHER_SKELETON,
					SPIDER, CAVE_SPIDER, ZOMBIE_HORSE, SKELETON_HORSE, ZOMBIFIED_PIGLIN, ZOGLIN,
					IRON_GOLEM, SNOW_GOLEM, SHULKER,
					SQUID, GLOW_SQUID, TROPICAL_FISH, PUFFERFISH, COD, SALMON,
					BAT -> true;
			default -> false;
		};
	}

	/**
	 * Checks whether the entity has soul
	 *
	 * @param entity entity
	 * @return whether the entity has soul
	 */
	public static boolean hasSoul(@NotNull LivingEntity entity) {
		if (true) return true;
		if (entity instanceof Animals) return true;
		if (entity instanceof Fish) return true;
		EntityType entityType = entity.getType();
		return switch (entityType) {
			case SQUID, GLOW_SQUID, BAT -> true;
			default -> false;
		};
	}

	/**
	 * Loads player's possessed data
	 *
	 * @param player player
	 * @param data data
	 * @return whether the player is now a possessor
	 */
	public static boolean loadPossessingData(@NotNull Player player, @NotNull ReadWriteNBT data) {
		if (!data.hasTag(POSSESSED_TAG)) return false;

		data = data.getCompound(POSSESSED_TAG);
		if (data == null) return false;
		if (!data.hasTag(POSSESSED_ENTITY_DATA)) return false;

		byte[] entityData = data.getByteArray(POSSESSED_ENTITY_DATA);
		LivingEntity entity = (LivingEntity) Bukkit.getUnsafe().deserializeEntity(entityData, player.getWorld(), true);

		entity.spawnAt(player.getLocation());
		startPossessing(player, entity);
//		addExtraControlItems(player); // ToDo: restore inventory? // TODO

		return true;
	}

	/**
	 * Saves player's possessed data
	 *
	 * @param player player
	 * @param data data to save into
	 * @param quit whether this saving is due to player quitting
	 */
	public static void savePossessedData(@NotNull Player player, @NotNull ReadWriteNBT data, boolean quit) {
		if (!isPossessing(player)) return;

		LivingEntity entity = getPossessed(player);
		if (entity == null) return;

		if (quit)
			player.leaveVehicle();

		byte[] entityData = Bukkit.getUnsafe().serializeEntity(entity);
		Location entityLoc = entity.getLocation();

		if (quit) {
			stopPossessing(player, entity, true);
		}

		data = data.getOrCreateCompound(POSSESSED_TAG);
		data.setByteArray(POSSESSED_ENTITY_DATA, entityData);
	}

	/**
	 * Saves all data, should be called on server shutdown
	 */
	public static void saveAllData() {
		for (UUID uuid : POSSESSING) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null) continue;

			savePossessedData(player, PlayerDataStorage.getData(uuid), true);
		}
		POSSESSING.clear();
	}

}
