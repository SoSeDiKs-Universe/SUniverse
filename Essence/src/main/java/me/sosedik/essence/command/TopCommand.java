package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.essence.Essence;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Teleporting to the top
 */
@NullMarked
@Permission("essence.command.top")
public class TopCommand {

	@Command("top [player]")
	private void onCommand(
		CommandSourceStack stack,
		@Nullable @Argument(value = "player") Player player
	) {
		Player target;
		if (player == null) {
			if (!(stack.getExecutor() instanceof Player executor)) return;
			target = executor;
		} else {
			target = player;
		}

		Essence.scheduler().sync(() -> target.teleportAsync(target.getLocation().toHighestLocation().addY(1), PlayerTeleportEvent.TeleportCause.COMMAND, TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS));
	}

}
