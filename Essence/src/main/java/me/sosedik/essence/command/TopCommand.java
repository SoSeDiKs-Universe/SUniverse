package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import me.sosedik.utilizer.util.CommandUtils;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.entity.Player;
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
		Player target = CommandUtils.getTargetPlayer(stack, player);
		if (target == null) return;

		Essence.scheduler().sync(() -> LocationUtil.smartTeleport(target, target.getLocation().toHighestLocation().addY(1), false));
	}

}
