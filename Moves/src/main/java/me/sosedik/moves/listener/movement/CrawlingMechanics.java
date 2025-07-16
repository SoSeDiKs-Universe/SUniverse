package me.sosedik.moves.listener.movement;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.sosedik.moves.Moves;
import me.sosedik.moves.listener.entity.ShulkerCrawlerHandler;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Crawling (almost) anywhere
 */
@NullMarked
public class CrawlingMechanics implements Listener {

	private static final BlockData BARRIER_BLOCK_DATA = Material.BARRIER.createBlockData();
	private static final Set<UUID> CRAWLERS = new HashSet<>();
	private static final Set<UUID> CRAWL_COOLDOWNS = new HashSet<>();
	private static final Set<UUID> POTENTIAL_SLIDERS = new HashSet<>();
	private static final Set<UUID> SLIDERS = new HashSet<>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSprintStop(PlayerToggleSprintEvent event) {
		if (event.isSprinting()) return;

		Player player = event.getPlayer();
		if (CRAWL_COOLDOWNS.contains(player.getUniqueId())) return;
		if (!POTENTIAL_SLIDERS.add(player.getUniqueId())) return;

		Moves.scheduler().sync(() -> POTENTIAL_SLIDERS.remove(player.getUniqueId()), 20L);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCrawl(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if (event.isSneaking()) {
			if (!isCrawling(player)) return;

			Block block = player.getLocation().getBlock().getRelative(BlockFace.UP);
			if (shouldAllowStanding(block, player)) {
				SLIDERS.remove(player.getUniqueId());
				standUp(player);
			}
			return;
		}

		if (player.isSwimming()) return;
		if (player.isFlying()) return;
		if (!player.isOnGround()) return;
		if (CRAWL_COOLDOWNS.contains(player.getUniqueId())) return;
		if (player.isInsideVehicle()) return;
		if (SneakCounter.getSneaksCount(player) != 2) return;

		if (!crawl(player))
			return;

		if (POTENTIAL_SLIDERS.contains(player.getUniqueId()))
			slide(player);
	}

	private boolean shouldAllowStanding(Block block, Player player) {
		if (!LocationUtil.isTrulySolid(player, block)) return true;
		return block.getBlockData() instanceof Slab slab && slab.getType() == Slab.Type.TOP;
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCrawlJump(PlayerJumpEvent event) {
		Player player = event.getPlayer();
		if (!isCrawling(player)) return;

		Location currentLoc = player.getLocation();
		if (LocationUtil.isTrulySolid(player, currentLoc.getBlock().getRelative(BlockFace.UP))) return;

		Location locTo = event.getFrom().add(currentLoc.getDirection().setY(0).normalize().multiply(0.4));
		if (currentLoc.isBlockSame(locTo)) {
			if (!LocationUtil.isTrulySolid(player, locTo.getBlock())) return;

			locTo.addY(LocationUtil.getMaxYPoint(locTo.getBlock())).addY(0.3).shiftTowards(player.getFacing(), 0.15);

			Moves.scheduler().sync(() -> player.teleport(locTo), 1L);
			return;
		}

		locTo.addY(1);
		if (!locTo.clone().addY(-0.2).getBlock().isSolid()) return;

		locTo.addY(LocationUtil.getMaxYPoint(locTo.getBlock())).addY(0.2).shiftTowards(player.getFacing(), 0.15);
		if (LocationUtil.isTrulySolid(player, locTo.getBlock())) return;

		Moves.scheduler().sync(() -> player.teleport(locTo), 1L);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onJump(PlayerJumpEvent event) {
		Player player = event.getPlayer();
		if (player.isSwimming()) return;
		if (isCrawling(player)) return;

		boolean sneak = player.isSneaking();
		if (!sneak) {
			// Treat standing on scaffolding as sneak
			Block block = player.getLocation().getBlock();
			sneak = block.getType() != Material.SCAFFOLDING && block.getRelative(BlockFace.DOWN).getType() == Material.SCAFFOLDING;
		}

		boolean slide = false;
		boolean autoJump = false;
		if (!sneak) {
			if (player.isSprinting()) {
				// Trigger slide if there's a solid block above player
				Block block = player.getLocation().getBlock().getRelative(BlockFace.UP, 2);
				if (LocationUtil.isTrulySolid(player, block)) {
					slide = true;
				} else {
					// Also check the block in front, just to be sure
					block = block.getRelative(player.getFacing());
					if (LocationUtil.isTrulySolid(player, block)) {
						slide = true;
					} else {
						// Nothing above the player, possibly auto jump from this distance?
						block = block.getRelative(player.getFacing());
						if (LocationUtil.isTrulySolid(player, block)) {
							slide = true;
							autoJump = true;
						}
					}
				}
			}
			if (!slide) return;
		}

		Vector direction = player.getLocation().getDirection();
		double y = direction.getY();
		if (y < -0.7 || y > 0.85) return;

		double x = direction.getX();
		double z = direction.getZ();
		boolean xc = (x < 0.23 && x > -0.23);
		boolean zc = (z < 0.23 && z > -0.23);
		if (!(xc || zc)) return;

		// Note: adding direction applies also for Y, so we have to add 1 to checks.
		// Non-solid block in front of player; tldr: check if the player will not crawl on air
		Block block = player.getLocation().add(direction).addY(1).getBlock();
		if (!LocationUtil.isTrulySolid(player, block)) {
			if (!autoJump) return;

			block = block.getRelative(player.getFacing());
			if (!LocationUtil.isTrulySolid(player, block))
				return;
		}

		block = block.getRelative(BlockFace.UP);
		// Solid block in front of player; tldr: check if player gets into hole
		if (LocationUtil.isTrulySolid(player, block)) return;

		// Non-solid block above the player & non-solid block above in front of the player; tldr: check for hole in front
		block = block.getRelative(BlockFace.UP);
		if (!LocationUtil.isTrulySolid(player, player.getLocation().addY(2).getBlock()) && !LocationUtil.isTrulySolid(player, block)) return;

		event.setCancelled(true);
		crawl(player);
		Location loc = event.getFrom().clone().add(x, 1, z);
		if (autoJump)
			loc = loc.shiftTowards(player.getFacing()).setDirection(direction);
		loc.center(0).shiftTowards(player.getFacing(), -0.15);

		Location finalLoc = loc;
		boolean finalSlide = slide;
		Moves.scheduler().sync(() -> {
			player.teleport(finalLoc);
			if (finalSlide)
				slide(player);
		}, 1L);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		standUp(event.getPlayer());
	}

	/**
	 * Checks whether the player is crawling
	 *
	 * @param player player
	 * @return whether the player is crawling
	 */
	public static boolean isCrawling(Player player) {
		return CRAWLERS.contains(player.getUniqueId());
	}

	/**
	 * Makes the player crawl
	 *
	 * @param player player
	 * @return whether the player was able to ender the crawling state
	 */
	public static boolean crawl(Player player) {
		if (isCrawling(player)) return false;

		UUID uuid = player.getUniqueId();
		if (!CRAWLERS.add(uuid)) return false;

		player.emitSound(Sound.BLOCK_GRASS_STEP, 1F, 0F);
		player.emitSound(Sound.ITEM_LEAD_BREAK, 1F, 1F);

		player.setSneaking(false);
		player.setPose(Pose.SWIMMING, true);
		player.setNoDamageTicks(Math.max(player.getNoDamageTicks(), 20));

		ShulkerCrawlerHandler.startCrawling(player);

		return true;
	}

	/**
	 * Makes the player to wakeup if currently crawling
	 *
	 * @param player player
	 */
	public static void standUp(Player player) {
		UUID uuid = player.getUniqueId();
		if (!CRAWLERS.remove(uuid)) return;

		player.setPose(player.getPose());

		CRAWL_COOLDOWNS.add(uuid);
		Moves.scheduler().async(() -> CRAWL_COOLDOWNS.remove(uuid), 15L);
	}

	/**
	 * Makes the player to slide in currently looking direction
	 *
	 * @param player player
	 */
	public static void slide(Player player) {
		UUID uuid = player.getUniqueId();
		if (SLIDERS.contains(uuid)) return;
		if (CRAWL_COOLDOWNS.contains(uuid)) return;

		SLIDERS.add(uuid);
		Moves.scheduler().async(() -> SLIDERS.remove(uuid), 20L);

		// It's wacky and inaccurate but still kinda works
		Vector blocksDirection = player.getLocation().getDirection().setY(0).normalize();
		Block block = player.getLocation().getBlock().getRelative(BlockFace.UP);
		int blocks = 9;
		Map<Location, BlockData> fakedBlocks = new HashMap<>(blocks);
		for (int i = 0; i < blocks; i++) {
			block = block.getLocation().toCenterLocation().add(blocksDirection).getBlock();
			if (shouldBeFaked(block))
				fakedBlocks.put(block.getLocation(), BARRIER_BLOCK_DATA);
		}
		player.sendMultiBlockChange(fakedBlocks);

		Vector velocity = blocksDirection.multiply(0.35);
		Moves.scheduler().sync(task -> {
			Block currentBlock = player.getLocation().getBlock();
			if (!currentBlock.getRelative(BlockFace.DOWN).getType().isSolid() // block below player is not solid
					|| LocationUtil.isTrulySolid(player, currentBlock.getRelative(player.getFacing()))) // block in front is solid
				SLIDERS.remove(uuid);

			if (!SLIDERS.contains(uuid)) {
				if (shouldStandUp(player))
					standUp(player);
				fakedBlocks.replaceAll((loc, data) -> loc.getBlock().getBlockData());
				player.sendMultiBlockChange(fakedBlocks);
				Moves.scheduler().sync(() -> {
					if (!player.isSneaking())
						player.setSprinting(true);
				}, 3L);
				return true;
			}

			player.setVelocity(velocity);
			for (LivingEntity nearbyEntity : player.getLocation().getNearbyLivingEntities(0.5)) {
				if (!nearbyEntity.getUniqueId().equals(uuid)) {
					nearbyEntity.damage(1, player);
					nearbyEntity.setVelocity(velocity.clone().setY(0.2));
					if (player.getFireTicks() > 0)
						nearbyEntity.setFireTicks(Math.max(nearbyEntity.getFireTicks(), Math.min(player.getFireTicks(), 40)));
				}
			}

			player.setFireTicks(player.getFireTicks() - 2);
			return false;
		}, 3L, 1L);
	}

	/**
	 * Checks whether player could stand up in current location
	 *
	 * @param player player
	 * @return whether player could stand up in current location
	 */
	public static boolean shouldStandUp(Player player) {
		Block block = player.getLocation().getBlock().getRelative(BlockFace.UP);
		if (!block.getType().isSolid()) return true;
		if (block.getBlockData() instanceof Slab slab) return slab.getType() == Slab.Type.TOP;
		return false;
	}

	private static boolean shouldBeFaked(Block block) {
		if (block.isEmpty()) return true;
		Material type = block.getType();
		return switch (type) {
			case TALL_GRASS, LARGE_FERN, SUNFLOWER, LILAC, ROSE_BUSH, PEONY -> true;
			default -> false;
		};
	}

}
