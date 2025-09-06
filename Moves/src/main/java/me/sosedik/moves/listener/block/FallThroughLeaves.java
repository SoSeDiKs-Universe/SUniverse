package me.sosedik.moves.listener.block;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.moves.Moves;
import me.sosedik.moves.api.event.PlayerStartFallingEvent;
import me.sosedik.moves.dataset.MovesTags;
import me.sosedik.moves.listener.movement.CrawlingMechanics;
import me.sosedik.moves.listener.movement.PlayerFallTicker;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Leaves shouldn't be walkable
 */
public class FallThroughLeaves implements Listener {

	private static final Vector DIVISION_OFFSET = new Vector(1.35, 1, 1.35);
	private static final Set<UUID> MOUNTS_ON_DELAY = new HashSet<>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedPosition()) return;

		Location from = event.getFrom();
		Location to = event.getTo();
		if (to.getY() > from.getY()) return;
		if (MathUtil.getDecimalPartAbs(to.getY()) > 0.05) return;

		Player player = event.getPlayer();
		if (player.isInsideVehicle()) return;
		if (player.isFlying() && !player.isSneaking()) return;

		Block block = to.getBlock().getRelative(BlockFace.DOWN);
		if (!MovesTags.FALL_THROUGH_BLOCKS.isTagged(block.getType())) return;

		Block supportingBlock = player.getSupportingBlock();
		if (supportingBlock != null && !MovesTags.FALL_THROUGH_BLOCKS.isTagged(supportingBlock.getType())) return;

		player.teleport(to.clone().addY(-0.1), TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS);
		player.emitSound(Sound.BLOCK_GRASS_STEP, 1F, 1F);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onMoveUp(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedPosition()) return;

		Player player = event.getPlayer();
		if (!player.isFlying()) return;
		if (player.isInsideVehicle()) return;
		if (player.isSneaking()) return;

		Location to = event.getTo();
		double decY = MathUtil.getDecimalPartAbs(to.getY());
		if (decY < 0.2) return;
		if (decY > 0.5) return;
		if (event.getFrom().getY() > to.getY()) return;
		if (!MovesTags.FALL_THROUGH_BLOCKS.isTagged(player.getEyeLocation().addY(0.35).getBlock().getType())) return;

		player.teleport(player.getLocation().addY(0.35), TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS);
		player.emitSound(Sound.BLOCK_GRASS_STEP, 1F, 1F);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockMove(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedPosition()) return;

		Location to = event.getTo();
		Location from = event.getFrom();
		if (to.getX() == from.getX() && to.getZ() == from.getZ()) return;

		Player player = event.getPlayer();
		if (player.isInsideVehicle()) return;
		// Check if not moving backwards
		if (to.toVector().subtract(from.toVector()).normalize().dot(player.getLocation().getDirection()) <= 0) return;

		Vector direction = player.getLocation().getDirection().setY(0).normalize();
		Location loc = player.getLocation().add(direction.divide(new Vector(2.8, 1, 2.8)));
		if (loc.isBlockSame(player.getLocation())) return;

		Block block = loc.getBlock();
		boolean lowerLeaves = MovesTags.FALL_THROUGH_BLOCKS.isTagged(block.getType());
		boolean upperLeaves = MovesTags.FALL_THROUGH_BLOCKS.isTagged(block.getRelative(BlockFace.UP).getType());
		// No blocking leaves
		if (!lowerLeaves && !upperLeaves) return;
		// Upper leaves, but the bottom is solid
		if (upperLeaves && !lowerLeaves && LocationUtil.isTrulySolid(player, block)) return;
		// Bottom leaves, but upper is solid and we are not crawling
		if (lowerLeaves && !upperLeaves && !CrawlingMechanics.isCrawling(player) && LocationUtil.isTrulySolid(player, block.getRelative(BlockFace.UP))) return;

		boolean crawling = player.getPose() == Pose.SWIMMING;
		// If not crawling, no bottom leaves and upper leaves are present, force crawling
		// This is possible when leaves have no collision
		if (!crawling) {
			Block currentBlock = player.getLocation().getBlock();
			// No solid bottom and no leaves down, leaves up
			crawling = !MovesTags.FALL_THROUGH_BLOCKS.isTagged(currentBlock.getType()) && !LocationUtil.isTrulySolid(player, currentBlock) && MovesTags.FALL_THROUGH_BLOCKS.isTagged(currentBlock.getRelative(BlockFace.UP).getType());
			if (crawling && player.isOnGround())
				CrawlingMechanics.crawl(player);
		}
		// If crawling and the block has no collision
		if (crawling && !lowerLeaves && !block.isCollidable()) return;

		player.teleport(loc, TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS);
		player.emitSound(Sound.BLOCK_GRASS_STEP, 1F, 1F);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onJump(PlayerJumpEvent event) {
		Player player = event.getPlayer();
		if (player.getPose() == Pose.SWIMMING) return;

		Location from = event.getFrom();
		Block block = from.getBlock().getRelative(BlockFace.DOWN);

		// Deny jump if jumping from leaves
		if (MovesTags.FALL_THROUGH_BLOCKS.isTagged(block.getType())) {
			player.teleport(from.clone().addY(-0.1), TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS);
			player.emitSound(Sound.BLOCK_GRASS_STEP, 1F, 1F);
			return;
		}

		// Allow jump if jumping with leaves above
		block = player.getEyeLocation().shiftTowards(BlockFace.UP).getBlock();
		if (!MovesTags.FALL_THROUGH_BLOCKS.isTagged(block.getType())) return;

		player.teleport(from.clone().addY(1.15), TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS);
		player.emitSound(Sound.BLOCK_GRASS_STEP, 1F, 1F);
	}

//	@EventHandler(ignoreCancelled = true) // TODO: does not teleport on the client for some reason since 1.20.4
	public void onMountMove(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedPosition()) return;

		Player player = event.getPlayer();
		Entity entity = player.getVehicle();
		if (entity == null) return;
		if (MOUNTS_ON_DELAY.contains(entity.getUniqueId())) return;

		Location to = event.getTo();
		if (MathUtil.getDecimalPartAbs(to.getY()) < 0.1 && MovesTags.FALL_THROUGH_BLOCKS.isTagged(entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType())) {
			if (entity.teleport(entity.getLocation().addY(-0.2), TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS))
				entity.emitSound(Sound.BLOCK_GRASS_STEP, 1F, 1F);
			return;
		}

		Location from = event.getFrom();
		if (to.getX() == from.getX() && to.getZ() == from.getZ()) return;

		Vector direction = player.getLocation().getDirection().setY(0).normalize();
		Location loc = to.clone().add(direction.divide(DIVISION_OFFSET));
		if (to.isBlockSame(loc)) return;

		Block block = loc.getBlock();
		boolean lowerLeaves = MovesTags.FALL_THROUGH_BLOCKS.isTagged(block.getType());
		boolean upperLeaves = MovesTags.FALL_THROUGH_BLOCKS.isTagged(block.getRelative(BlockFace.UP).getType());
		if (!lowerLeaves && !upperLeaves) return;
		if (upperLeaves && !lowerLeaves && LocationUtil.isTrulySolid(player, block)) return;
		if (lowerLeaves && !upperLeaves && LocationUtil.isTrulySolid(player, block.getRelative(BlockFace.UP))) return;

		MOUNTS_ON_DELAY.add(entity.getUniqueId());
		Moves.scheduler().async(() -> MOUNTS_ON_DELAY.remove(entity.getUniqueId()), 3L);
		if (entity.teleport(loc, TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS))
			entity.emitSound(Sound.BLOCK_GRASS_STEP, 1F, 1F);
	}

//	@EventHandler(ignoreCancelled = true) // TODO: does not teleport on the client for some reason since 1.20.4
	public void onMountJump(HorseJumpEvent event) {
		Entity entity = event.getEntity();
		List<Entity> passengers = entity.getPassengers();
		if (passengers.isEmpty()) return;

		Block block = entity.getLocation().getBlock().getRelative(BlockFace.UP, 2);
		if (!MovesTags.FALL_THROUGH_BLOCKS.isTagged(block.getType())) return;

		Entity passenger = passengers.getFirst();
		Vector direction = passenger.getLocation().getDirection().setY(0).normalize();
		Location loc = entity.getLocation().add(direction.divide(DIVISION_OFFSET).setY(1));
		block = loc.getBlock();
		if (LocationUtil.isTrulySolid(passenger, block)) return;
		if (LocationUtil.isTrulySolid(passenger, block.getRelative(BlockFace.UP))) return;

		Moves.scheduler().sync(() -> {
			if (entity.teleport(loc.center(0.1), TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS))
				entity.emitSound(Sound.BLOCK_GRASS_STEP, 1F, 1F);
		}, 1L);
	}

	@EventHandler
	public void onFall(PlayerStartFallingEvent event) {
		Player player = event.getPlayer();
		Block block = player.getLocation().shiftTowards(BlockFace.UP, 2).getBlock();
		if (!MovesTags.FALL_THROUGH_BLOCKS.isTagged(block.getType())) return;

		Vector velocity = PlayerFallTicker.getStoredPreVelocity(player);
		if (velocity.getY() < 0.6) return;

		player.teleportAsync(player.getLocation().addY(1.5)).thenRun(() -> {
			player.setVelocity(velocity);
			Moves.scheduler().sync(() -> player.setVelocity(velocity), 1L);
		});
	}

}
