package me.sosedik.requiem.feature;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.listener.entity.PrepareGhostMobs;
import me.sosedik.requiem.listener.player.LoadSavePlayers;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PossessingPlayer {

	private static final Set<UUID> POSSESSING = new HashSet<>();
	private static final String POSSESSED_PERSISTENT_TAG = "persistent";

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
		entity.modifyPersistentData(nbt -> nbt.getOrCreateCompound(LoadSavePlayers.POSSESSED_TAG).setBoolean(POSSESSED_PERSISTENT_TAG, persistent));

//		EffectManager.addEffect(player, KittenEffects.ATTRITION, -1, 0); // TODO
		PrepareGhostMobs.makeInvisible(player, false);
		player.setInvisible(true);
		player.setInvulnerable(false); // Prevents mobs from targeting the player if true

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
			if (riding != null && riding.getPersistentData(nbt -> nbt.hasTag(POSSESSED_PERSISTENT_TAG))) {
				boolean persistent = riding.modifyAndGetPersistentData(nbt -> {
					boolean value = nbt.getBoolean(POSSESSED_PERSISTENT_TAG);
					nbt.removeKey(POSSESSED_PERSISTENT_TAG);
					return value;
				});
				riding.setPersistent(persistent);
			}
		}

		player.setInvisible(false);
		player.setInvulnerable(false);

//		TemperaturedPlayer.of(player).removeFlag(TempFlag.GHOST_IMMUNE); // TODO

//		PlayerInventory playerInventory = player.getInventory();
//		playerInventory.clear();

		POSSESSING.remove(player.getUniqueId());

		if (quit) return;

		player.leaveVehicle();
//		PrepareGhostMobs.makeVisible(player);

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
	 * Checks whether the entity can be controlled by player
	 *
	 * @param player player
	 * @param entity entity
	 * @return whether the entity can be controlled by player
	 */
	public static boolean isAllowedForCapture(@NotNull Player player, @NotNull Entity entity) {
		if (true) return true;
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

}
