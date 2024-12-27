package me.sosedik.miscme.listener.player;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
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
 * Increased jump height for fences and walls
 */
@NullMarked
public class JumpingOverFences implements Listener {

	private static final Vector JUMP_BOOST = new Vector().setY(0.3);
	private static final Vector CLIMB_BOOST = new Vector().setY(0.5);

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJump(PlayerJumpEvent event) {
		Player player = event.getPlayer();
		Block block = event.getFrom().getBlock();
		if (!LocationUtil.isBlockHigher(block, 1F)) {
			block = block.getRelative(player.getFacing());
			if (!LocationUtil.isBlockHigher(block, 1F)) return;
		}
		if (LocationUtil.isTrulySolid(player, block.getRelative(BlockFace.UP))) return;

		player.setVelocity(player.getVelocity().add(JUMP_BOOST));
	}

	@EventHandler(ignoreCancelled = true)
	public void onClimb(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedPosition()) return;

		Location to = event.getTo();
		Location from = event.getFrom();
		if (to.getBlockY() == from.getBlockY()) return;
		if (to.getY() <= from.getY()) return;
		if (MathUtil.getDecimalPart(to.getY()) > 0.13) return;
		if (MathUtil.getDecimalPart(from.getY()) < 0.9) return;

		Player player = event.getPlayer();
		if (player.getVelocity().getY() > 0) return;

		Block block = to.getBlock();
		if (!block.isEmpty()) return;

		block = block.getRelative(BlockFace.DOWN);
		if (block.isEmpty()) return;
		if (!LocationUtil.isBlockHigher(block, 1F)) return;

		player.setVelocity(player.getVelocity().add(CLIMB_BOOST));
	}

}
