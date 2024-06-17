package me.sosedik.requiem.feature;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MobPossessing {

	/**
	 * Checks whether the entity can be controlled by player
	 *
	 * @param player player
	 * @param entity entity
	 * @return whether the entity can be controlled by player
	 */
	public static boolean isAllowedForCapture(@NotNull Player player, @NotNull Entity entity) {
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
		if (entity instanceof Animals) return true;
		if (entity instanceof Fish) return true;
		EntityType entityType = entity.getType();
		return switch (entityType) {
			case SQUID, GLOW_SQUID, BAT -> true;
			default -> false;
		};
	}

}
