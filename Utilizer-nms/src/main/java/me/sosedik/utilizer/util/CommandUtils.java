package me.sosedik.utilizer.util;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Utility class for common command patterns and operations
 */
@NullMarked
public final class CommandUtils {

	private CommandUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Gets the target player from command arguments, defaulting to command executor if no target specified.
	 * Returns null if no valid target can be determined (e.g., console execution without target).
	 *
	 * @param stack the command source stack
	 * @param targetArg the target player argument (nullable)
	 * @return the target player, or null if invalid
	 */
	public static @Nullable Player getTargetPlayer(CommandSourceStack stack, @Nullable Player targetArg) {
		if (targetArg != null)
			return targetArg;

		CommandSender executor = stack.getExecutor();
		if (executor instanceof Player player)
			return player;

		return null;
	}

	/**
	 * Checks if the command was executed on another player (not self).
	 *
	 * @param stack the command source stack
	 * @param target the target player
	 * @return true if executed on another player
	 */
	public static boolean isTargetingOther(CommandSourceStack stack, Player target) {
		CommandSender executor = stack.getExecutor();
		return executor instanceof Player player && player != target;
	}

}
