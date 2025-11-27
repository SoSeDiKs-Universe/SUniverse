package me.sosedik.trappednewbie.listener.player;

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.event.player.PlaceableBlockHighlightEvent;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Allow placing blocks where it's normally unreachable
 */
@NullMarked
public class ReachAround implements Listener {

	private final Map<UUID, BukkitTask> currentTasks = new HashMap<>();

	@EventHandler
	public void onJoin(PlayerClientLoadedWorldEvent event) {
		// Start reach around task
		Player player = event.getPlayer();
		var task = TrappedNewbie.scheduler().sync(new ReachAroundRunnable(player), 60, 2);
		this.currentTasks.put(player.getUniqueId(), task);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		// Cancel reach around task
		UUID uuid = event.getPlayer().getUniqueId();
		BukkitTask task = this.currentTasks.remove(uuid);
		if (task != null)
			task.cancel();
	}

	@EventHandler
	public void onBlockPlace(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

		Player player = event.getPlayer();
		if (!tryPlacingBlock(player, EquipmentSlot.HAND))
			tryPlacingBlock(player, EquipmentSlot.OFF_HAND);
	}

	private boolean tryPlacingBlock(Player player, EquipmentSlot hand) {
		ReachAroundData reachAroundData = getPlaceableTarget(player, hand);
		if (reachAroundData == null) return false;
		if (!player.placeBlock(hand, reachAroundData.loc(), BlockFace.UP)) return false;

		Block block = reachAroundData.loc().getBlock();
		// Place slabs at top if player is higher
		if (block.getBlockData() instanceof Slab slab && slab.getType() == Slab.Type.BOTTOM
				&& LocationUtil.isBlockHigher(block, (float) MathUtil.getDecimalPartAbs(player.getLocation().getY()))) {
			slab.setType(Slab.Type.TOP);
			block.setBlockData(slab);
		}

		return true;
	}

	public static class ReachAroundRunnable extends BukkitRunnable {

		private static final Color HIGHLIGHT_COLOR = Color.fromARGB(100, 0, 16, 0);

		private final Player player;

		public ReachAroundRunnable(Player player) {
			this.player = player;
		}

		@Override
		public void run() {
			if (!this.player.isOnline()) {
				cancel();
				return;
			}
			if (!tryPlaceBlockPreview(EquipmentSlot.HAND))
				tryPlaceBlockPreview(EquipmentSlot.OFF_HAND);
		}

		private boolean tryPlaceBlockPreview(EquipmentSlot hand) {
			ReachAroundData reachAroundData = getPlaceableTarget(this.player, hand);
			if (reachAroundData == null) return false;

			reachAroundData.loc().getWorld().spawn(reachAroundData.loc(), BlockDisplay.class, bd -> {
				bd.setVisibleByDefault(false);
				bd.setPersistent(false);
				bd.setBlock(reachAroundData.blockData());
				bd.setGlowing(true);
				this.player.showEntity(TrappedNewbie.instance(), bd);
				TrappedNewbie.scheduler().sync(bd::remove, 5L);
			});
			return true;
		}

	}

	private record ReachAroundData(Location loc, BlockData blockData) {}
	private static @Nullable ReachAroundData getPlaceableTarget(Player player, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (ItemStack.isEmpty(item)) return null;
		if (!item.getType().isBlock()) return null;

		var event = new PlaceableBlockHighlightEvent(player, hand, null);
		if (!event.callEvent()) return null;

		Location target = event.getPlaceTarget();
		if (target == null) return null;
		if (!target.getBlock().getRelative(BlockFace.DOWN).isEmpty()) return null;

		BlockData blockData = player.getPlacedState(hand, item, target, BlockFace.UP);
		if (blockData == null) return null;

		return new ReachAroundData(target, blockData);
	}

	/**
	 * Gets the target location for reach around
	 *
	 * @param player player
	 * @return the target location for reach around
	 */
	public static @Nullable Location getPlayerReachAroundTarget(Player player) {
		if (!player.isOnGround()) return null;

		RayTraceResult rayTraceResult = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getEyeLocation().getDirection(), 5);
		if (rayTraceResult != null) return null;

		Location target = getPlayerVerticalReachAround(player);
		if (target != null)
			return checkReplaceable(target);

		target = getPlayerHorizonTalReachAround(player);
		if (target != null)
			return checkReplaceable(target);

		return null;
	}

	private static @Nullable Location checkReplaceable(Location loc) {
		Block block = loc.getBlock();
		return block.isEmpty() || block.isReplaceable() ? loc : null;
	}

	private static @Nullable Location getPlayerVerticalReachAround(Player player) {
		var vec = new Vector(0, 0.5, 0);
		RayTraceResult rayTrace = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getEyeLocation().getDirection().clone().add(vec), 5);
		if (rayTrace == null) return null;
		if (rayTrace.getHitBlock() == null) return null;

		Location playerLoc = player.getLocation();
		Location blockLoc = rayTrace.getHitBlock().getLocation().center(0);
		if (Math.abs(playerLoc.getZ() - blockLoc.getZ()) < 1.35 && playerLoc.getY() > blockLoc.getY() && playerLoc.getY() - blockLoc.getY() < 1.5 && Math.abs(playerLoc.getX() - blockLoc.getX()) < 1.35) {
			Location target = blockLoc.addY(-1);
			if (target.getBlock().isEmpty())
				return target;
		}

		return null;
	}

	private static @Nullable Location getPlayerHorizonTalReachAround(Player player) {
		Location playerLoc = player.getLocation();
		BlockFace facing = player.getFacing();

		Vector direction = player.getEyeLocation().getDirection();
		var vec = new Vector(facing.getModX() / 2D, 0, facing.getModZ() / 2D);
		RayTraceResult rayTrace = player.getWorld().rayTraceBlocks(player.getEyeLocation(), direction.clone().subtract(vec), 4);
		if (rayTrace == null) return null;
		if (rayTrace.getHitBlock() == null) return null;

		Location loc = rayTrace.getHitBlock().getLocation().center(0);
		boolean sameLevel = playerLoc.getY() > playerLoc.getBlockY();
		if (sameLevel ? playerLoc.getBlockY() != loc.getBlockY() : playerLoc.getBlockY() - loc.getBlockY() != 1)
			return null;

		Block target = loc.getBlock().getRelative(player.getFacing());
		double distance = playerLoc.distanceSquared(target.getLocation().center(sameLevel ? 0 : 1));
		if (distance > 85) return null;
		if (distance < 0.3) return null;

		if (target.isEmpty())
			return target.getLocation();

		return null;
	}

}
