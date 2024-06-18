package me.sosedik.utilizer.util;

import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EntityUtil {

	private EntityUtil() {
		throw new IllegalStateException("Utility class");
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

}
