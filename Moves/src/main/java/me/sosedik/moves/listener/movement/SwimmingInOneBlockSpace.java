package me.sosedik.moves.listener.movement;

import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.moves.Moves;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Start swimming in one block space
 */
// MCCheck: 1.21.10, workaround to swimming state metadata
@NullMarked
public class SwimmingInOneBlockSpace implements Listener {

	private static final int SWIMMING_METADATA_INDEX = 0;

	@EventHandler(ignoreCancelled = true)
	public void onSprintToggle(PlayerToggleSprintEvent event) {
		if (event.isSprinting()) return;

		Player player = event.getPlayer();
		if (player.getPose() != Pose.STANDING) return;

		Block block = player.getLocation().getBlock();
		if (!LocationUtil.isWatery(block)) return;
		if (!player.getEyeLocation().getBlock().getType().isAir()) return;
		if (isInPuddle(block)) return;

		Location loc = player.getLocation();
		player.teleport(loc.y(loc.getBlockY() + 0.65), TeleportFlag.Relative.values());
		Moves.scheduler().sync(() -> {
			player.setSprinting(true); // Swimming is not possible without sprinting

			// noinspection deprecation
			player.setSwimming(true);

			// Dumb workaround for swimming state desync
			Moves.scheduler().sync(() -> player.resendMetadata(SWIMMING_METADATA_INDEX), 5L);
		}, 2L);
	}

	private boolean isInPuddle(Block block) {
		return block.getType() == Material.WATER
				&& !LocationUtil.isWatery(block.getRelative(BlockFace.DOWN))
				&& block.getBlockData() instanceof Levelled levelled
				&& levelled.getLevel() == 7;
	}

}
