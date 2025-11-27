package me.sosedik.moves.listener.movement;

import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

/**
 * You can jump from water into the block above
 */
@NullMarked
public class HigherWaterJump implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedBlock()) return;

		Player player = event.getPlayer();
		if (player.isFlying()) return;

		Location from = event.getFrom();
		Location to = event.getTo();
		Block blockFrom = from.getBlock();
		Block blockTo = to.getBlock();
		if (blockFrom.getX() != blockTo.getX()) return;
		if (blockFrom.getZ() != blockTo.getZ()) return;
		if (blockFrom.getY() + 1 != blockTo.getY()) return;
		if (!LocationUtil.isWatery(blockFrom)) return;
		if (blockTo.isCollidable()) return;

		Block forwardBlock = blockTo.getRelative(player.getFacing());
		if (!LocationUtil.isTrulySolid(player, forwardBlock)) return;
		forwardBlock = forwardBlock.getRelative(BlockFace.UP);
		if (LocationUtil.isTrulySolid(player, forwardBlock)) return;
		forwardBlock = forwardBlock.getRelative(BlockFace.UP);
		if (LocationUtil.isTrulySolid(player, forwardBlock)) return;

		Vector velocity = player.getVelocity();
		if (velocity.getY() > 0.35) return;

		player.setVelocity(velocity.setY(0.35));
	}

}
