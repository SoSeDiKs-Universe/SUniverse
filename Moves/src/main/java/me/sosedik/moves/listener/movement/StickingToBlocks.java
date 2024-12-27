package me.sosedik.moves.listener.movement;

import me.sosedik.moves.Moves;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Sticking to blocks
 */
public class StickingToBlocks implements Listener {

	private static final BlockData BARRIER_BLOCK_DATA = Material.BARRIER.createBlockData();
	private static final Vector ZERO = new Vector();
	private static final Set<UUID> STICKING_PLAYERS = new HashSet<>();

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (!STICKING_PLAYERS.remove(player.getUniqueId())) return;

		player.setGravity(true);
	}

	// Sadly toggle sneak event is not as reliable since
	// then it's required to press Shift at the exact
	// moment, while move event leaves room for error
	@EventHandler
	public void onStick(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedPosition()) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;
		if (player.getPose() != Pose.SNEAKING) return;
		if (player.isFlying()) return;
		if (player.isOnGround()) return;
		if (player.getFallDistance() > 5) return;
		if (isSticking(player)) return;

		Block block = player.getLocation().getBlock();
		if (LocationUtil.isTrulySolid(player, block.getRelative(BlockFace.DOWN))) return;
		if (Tag.CLIMBABLE.isTagged(block.getType())) return;

		block = block.getRelative(player.getFacing());
		if (!block.getType().isSolid()) return;
		if (LocationUtil.isTrulySolid(player, block.getRelative(BlockFace.UP))) return;

		STICKING_PLAYERS.add(player.getUniqueId());
		player.setFallDistance(0F);
		player.setGravity(false);
		player.setVelocity(ZERO);

		Location loc = event.getTo();
		loc.setY(player.getLocation().getBlockY() + 0.1);
		event.setTo(loc);
		Vector dir = player.getFacing().getDirection().setY(0).multiply(.2F);
		Moves.scheduler().sync(() -> player.setVelocity(dir));
	}

	@EventHandler
	public void onStickDenyMove(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedPosition()) return;
		if (event.getFrom().getY() == event.getTo().getY()) return;
		if (!isSticking(event.getPlayer())) return;

		Location loc = event.getTo();
		loc.setY(event.getFrom().getY());
		event.setTo(loc);
	}

	@EventHandler
	public void onStickMoveFall(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedPosition()) return;

		Player player = event.getPlayer();
		if (!isSticking(player)) return;

		Block block = player.getLocation().getBlock().getRelative(player.getFacing());
		if (block.getType().isSolid() && !LocationUtil.isTrulySolid(player, block.getRelative(BlockFace.UP))) return;

		STICKING_PLAYERS.remove(player.getUniqueId());
		player.setGravity(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onUnStickJump(PlayerToggleSneakEvent event) {
		if (event.isSneaking()) return;

		Player player = event.getPlayer();
		if (!STICKING_PLAYERS.remove(player.getUniqueId())) return;

		player.setGravity(true);
		var vector = new Vector(0, EntityUtil.getJumpHeight(1.1), 0);
		player.setVelocity(vector);
	}

	@EventHandler
	public void onWallJump(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedPosition()) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;
		if (!player.isSprinting()) return;

		Block baseBlock = player.getLocation().getBlock();
		Block block = baseBlock.getRelative(BlockFace.DOWN);
		if (block.getType().isSolid()) return;
		if (!baseBlock.getRelative(player.getFacing()).getType().isSolid()) return;

		player.setFallDistance(0F);
		player.sendBlockChange(block.getLocation(), BARRIER_BLOCK_DATA);
		Moves.scheduler().sync(() -> player.sendBlockChange(block.getLocation(), block.getBlockData()), 8L);
	}

	@EventHandler
	public void onCrawl(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;
		if (player.hasGravity()) return;
		if (!isSticking(player)) return;

		Block block = event.getClickedBlock();
		assert block != null;
		if (block.getY() != player.getLocation().getBlockY()) {
			if (block.getType().isInteractable()) return;
			block = block.getRelative(BlockFace.DOWN);
			if (block.getY() != player.getLocation().getBlockY()) return;
		}
		if (!block.getRelative(BlockFace.UP, 2).isSolid()) return;
		if (LocationUtil.isTrulySolid(player, block.getRelative(BlockFace.UP))) return;

		player.setNoDamageTicks(Math.max(player.getNoDamageTicks(), 20));

		Location loc = block.getLocation().center(1);
		loc.setDirection(player.getLocation().getDirection());
		player.swingMainHand();
		player.setGravity(true);
		STICKING_PLAYERS.remove(player.getUniqueId());
		CrawlingMechanics.crawl(player);
		Moves.scheduler().sync(() -> {
			player.teleport(loc);
			player.setVelocity(ZERO);
		}, 1L);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!isSticking(player)) return;

		event.setCancelled(true);
	}

	/**
	 * Checks whether the player is sticking to the block
	 *
	 * @param player player
	 * @return whether the player is sticking to the block
	 */
	public static boolean isSticking(Player player) {
		return STICKING_PLAYERS.contains(player.getUniqueId());
	}

}
