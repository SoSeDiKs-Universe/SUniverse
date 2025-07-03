package me.sosedik.moves.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.moves.Moves;
import me.sosedik.moves.listener.movement.SittingMechanics;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.jspecify.annotations.NullMarked;

/**
 * Sitting with comfort (or not!)
 */
@NullMarked
public class SitCommand {

	@Command("sit")
	private void onCommand(
		CommandSourceStack stack,
		@Flag(value = "freeze") boolean freeze
	) {
		if (!(stack.getExecutor() instanceof Player target)) return;

		Moves.scheduler().sync(() -> sit(target, !freeze));
	}

	private void sit(Player player, boolean rotating) {
		if (player.isSneaking()) return;
		if (!player.isOnGround()) return;

		Location loc = player.getLocation();
		SittingMechanics.sit(player, loc.addY(-0.01), rotating ? SittingMechanics.SitCase.ROTATING : SittingMechanics.SitCase.STATIC);
	}

}
