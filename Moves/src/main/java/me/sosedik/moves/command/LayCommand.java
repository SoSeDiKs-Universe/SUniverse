package me.sosedik.moves.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.moves.Moves;
import me.sosedik.moves.listener.movement.LayingMechanics;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.jspecify.annotations.NullMarked;

/**
 * Laying with comfort (or not!)
 */
@NullMarked
public class LayCommand {

	@Command("lay")
	private void onCommand(
		CommandSourceStack stack
	) {
		if (!(stack.getExecutor() instanceof Player target)) return;

		Moves.scheduler().sync(() -> lay(target));
	}

	private void lay(Player player) {
		if (!canLay(player)) return;

		player.setSleepingIgnored(true);
		LayingMechanics.lay(player, player.getLocation(), player.getFacing().getOppositeFace());
	}

	private boolean canLay(Player player) {
		if (player.isSleeping()) return false;
		if (!player.isOnGround()) return false;

		Block block = player.getLocation().getBlock();
		return !block.getRelative(BlockFace.DOWN).isEmpty() && !LocationUtil.isTrulySolid(player, block.getRelative(player.getFacing()));
	}

}
