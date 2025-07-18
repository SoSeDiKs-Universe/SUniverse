package me.sosedik.utilizer.util;

import io.papermc.paper.block.fluid.FluidData;
import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.utilizer.Utilizer;
import org.bukkit.Chunk;
import org.bukkit.Fluid;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Stream;

@NullMarked
public class LocationUtil {

	private LocationUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * The surrounding block faces in horizontal pane
	 */
	public static final List<BlockFace> SURROUNDING_BLOCKS = List.of(
		BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH,
		BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST, BlockFace.NORTH
	);

	/**
	 * The surrounding block faces
	 */
	public static final List<BlockFace> SURROUNDING_BLOCKS_XZ = List.of(
		BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH
	);

	/**
	 * The surrounding block faces plus up and down
	 */
	public static final List<BlockFace> SURROUNDING_BLOCKS_UD = List.of(
		BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH,
		BlockFace.UP, BlockFace.DOWN
	);

	private static final Random RANDOM = new Random();
	private static final Set<NamespacedKey> RTP_BLACKLISTED_BIOMES = new HashSet<>();

	static {
		RTP_BLACKLISTED_BIOMES.add(Biome.MUSHROOM_FIELDS.getKey());
		RTP_BLACKLISTED_BIOMES.addAll(BiomeTags.OCEAN.stream().map(Biome::getKey).toList());
	}

	/**
	 * Randomly teleports the player
	 *
	 * @param player player
	 */
	public static CompletableFuture<Void> runRtp(Player player, World world, int range) {
		if (player.getLocation().getBlockY() < 400) {
			player.teleportAsync(player.getLocation().addY(1600), PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS);
		}
		if (!player.isFlying()) {
			Utilizer.scheduler().sync(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 25 * 20, 10)));
			Utilizer.scheduler().sync(task -> {
				if (!player.isOnline()) return true;
				if (player.isDead()) return true;

				player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 25 * 20, 10));
				return player.isOnGround() || player.isInWater() || player.isFlying();
			}, 20L, 20L);
		}
		var teleported = new CompletableFuture<Void>();
		Utilizer.scheduler().async(() -> findLocation(player, new Location(world, 0, 600, 0), 0, range, teleported));
		return teleported;
	}

	private static void findLocation(Player player, Location loc, int check, int range, CompletableFuture<@Nullable Void> teleported) {
		if (check > 50) {
			teleported.complete(null);
			return;
		}
		loc.setX((RANDOM.nextBoolean() ? 1D : -1D) * RANDOM.nextInt(range));
		loc.setZ((RANDOM.nextBoolean() ? 1D : -1D) * RANDOM.nextInt(range));
		loc.getWorld().getChunkAtAsyncUrgently(loc).thenAccept(chunk -> {
			if (RTP_BLACKLISTED_BIOMES.contains(loc.toHighestLocation().getBlock().getBiome().getKey())) {
				findLocation(player, loc, check + 1, range, teleported);
				return;
			}
			Location preLoc = player.getLocation();
			player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS)
				.thenRun(() -> teleported.complete(null));
			Utilizer.logger().info("Randomly teleporting %s from %s to %s".formatted(
				player.getName(),
				"%s[%s, %s, %s]".formatted(preLoc.getWorld().getName(), preLoc.getX(), preLoc.getY(), preLoc.getZ()),
				"%s[%s, %s, %s]".formatted(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ())
			));
		});
	}

	/**
	 * Checks whether block's collision is a cube
	 *
	 * @param block  block to check
	 * @return true, if block's bounding box is a 1x1x1 cube
	 */
	public static boolean isCube(Block block) {
		BoundingBox bb = block.getBoundingBox();
		return bb.getHeight() == 1 && bb.getWidthX() == 1 && bb.getWidthZ() == 1;
	}

	/**
	 * Checks whether block's collision is higher
	 * than the provided value
	 *
	 * @param block  block to check
	 * @param height max height to fail
	 * @return true, if block's bounding box is higher than the provided value
	 */
	public static boolean isBlockHigher(Block block, float height) {
		for (BoundingBox boundingBox : block.getCollisionShape().getBoundingBoxes()) {
			if (boundingBox.getMaxY() > height)
				return true;
		}
		return false;
	}

	/**
	 * Gets the highest Y point of the block
	 *
	 * @param block block
	 * @return the highest Y point
	 */
	public static double getMaxYPoint(Block block) {
		double maxY = 0;
		for (BoundingBox boundingBox : block.getCollisionShape().getBoundingBoxes()) {
			maxY = Math.max(maxY, boundingBox.getMaxY());
		}
		return maxY;
	}

	/**
	 * Gets the lowest Y point of the block
	 *
	 * @param block block
	 * @return the lowest Y point
	 */
	public static double getMinYPoint(Block block) {
		double minY = 1;
		for (BoundingBox boundingBox : block.getCollisionShape().getBoundingBoxes()) {
			minY = Math.min(minY, boundingBox.getMinY());
		}
		return minY;
	}

	/**
	 * Checks if this block is water or water-covered
	 *
	 * @param block block
	 * @return whether block is watery
	 */
	public static boolean isWatery(Block block) {
		if (block.getType() == Material.WATER_CAULDRON) return true;

		FluidData fluidData = block.getWorld().getFluidData(block.getLocation());
		return fluidData.getFluidType() == Fluid.WATER || fluidData.getFluidType() == Fluid.FLOWING_WATER;
	}

	/**
	 * Checks whether this block is water, lava, or covered in water
	 *
	 * @param block block
	 * @return whether block is fluid
	 */
	public static boolean isFluid(Block block) {
		if (block.getType() == Material.WATER_CAULDRON) return true;
		if (block.getType() == Material.LAVA_CAULDRON) return true;

		FluidData fluidData = block.getWorld().getFluidData(block.getLocation());
		return fluidData.getFluidType() != Fluid.EMPTY;
	}

	/**
	 * Checks whether this block is fire, lit campfire, or lava
	 *
	 * @param block block
	 * @return whether this block is a fire source
	 */
	public static boolean isFireSource(Block block) {
		return Tag.FIRE.isTagged(block.getType())
			|| (block.getType() == Material.LAVA && block.getBlockData() instanceof Levelled levelled && levelled.getLevel() == 0) // Source lava block
			|| (Tag.CAMPFIRES.isTagged(block.getType()) && block.getBlockData() instanceof Campfire campfire && campfire.isLit());
	}

	/**
	 * Checks whether block is "truly solid", so that
	 * the entity can not walk through it
	 *
	 * <p>Note: entity should be facing this block!
	 *
	 * @param entity entity facing the block
	 * @param block  block
	 * @return whether block is "truly solid"
	 */
	public static boolean isTrulySolid(Entity entity, Block block) {
		if (!block.isSolid()) return false;
		Material blockType = block.getType();
		if (Tag.LEAVES.isTagged(blockType)) return false;
		if (block.getBlockData() instanceof TrapDoor trapDoor) {
			if (block.getY() == entity.getLocation().getBlockY() - 1) return !trapDoor.isOpen();
			if (entity.getFacing() != trapDoor.getFacing()) return false;
			return trapDoor.isOpen();
		}
		if (block.getBlockData() instanceof Door door) {
			if (entity.getFacing().getOppositeFace() == door.getFacing()) return false;
			if (entity.getFacing() != door.getFacing()) return door.isOpen();
			return !door.isOpen();
		}
		if (block.getBlockData() instanceof Gate gate) {
			return !gate.isOpen();
		}
		if (Tag.PRESSURE_PLATES.isTagged(blockType)) return false;
		if (Tag.BANNERS.isTagged(blockType)) return false;
		return !Tag.ALL_SIGNS.isTagged(blockType);
		// Truly solid, congrats!
	}

	/**
	 * Checks whether it rains at the provided location
	 *
	 * @param loc location
	 * @return whether it rains at the provided location
	 */
	public static boolean isRainingAt(Location loc) {
		return isRainingAt(loc, false);
	}

	/**
	 * Checks whether it rains at the provided location
	 *
	 * @param loc location
	 * @param leavesObstruct whether leaves are considered as roof
	 * @return whether it rains at the provided location
	 */
	public static boolean isRainingAt(Location loc, boolean leavesObstruct) {
		World world = loc.getWorld();
		if (!world.hasStorm()) return false;

		Block block = loc.getBlock();
		double blockTemperature = block.getTemperature();
		if (blockTemperature > 0.95) return false; // Too hot
		if (blockTemperature < 0.15) return false; // Snowy

		return leavesObstruct
				? world.getHighestBlockAt(loc).getY() < loc.getBlockY()
				: world.getHighestBlockAt(loc, HeightMap.MOTION_BLOCKING_NO_LEAVES).getY() < loc.getBlockY();
	}

	/**
	 * Finds the closest entity
	 *
	 * @param loc loc
	 * @param radius radius
	 * @param filter filter
	 * @return the closest entity, if found
	 */
	public static @Nullable Entity findClosestEntity(Location loc, double radius, Predicate<? super Entity> filter) {
		Collection<Entity> nearbyEntities = loc.getWorld().getNearbyEntities(loc, radius, radius, radius, filter);

		Entity closestEntity = null;
		double closestDistance = Double.MAX_VALUE;
		for (Entity entity : nearbyEntities) {
			double distance = entity.getLocation().distanceSquared(loc);
			if (distance < closestDistance) {
				closestDistance = distance;
				closestEntity = entity;
			}
		}

		return closestEntity;
	}

	/**
	 * Finds loaded tile entities around location
	 *
	 * @param loc location
	 * @param radius radius
	 * @param blockPredicate block predicate
	 * @return found block states
	 */
	public static Stream<BlockState> findTileEntitiesNearby(Location loc, double radius, Predicate<? super Block> blockPredicate) {
		return getLoadedChunksInRadius(loc, radius).stream().map(chunk -> chunk.getTileEntities(blockPredicate, false)).flatMap(Collection::stream);
	}

	/**
	 * Gets loaded chunks around location
	 *
	 * @param center location
	 * @param radius radius
	 * @return chunks around location
	 */
	public static List<Chunk> getLoadedChunksInRadius(Location center, double radius) {
		List<Chunk> chunks = new ArrayList<>();
		World world = center.getWorld();

		int centerX = (int) Math.floor(center.getX()) >> 4;
		int centerZ = (int) Math.floor(center.getZ()) >> 4;

		int radiusInChunks = (int) Math.ceil(radius / 16D);

		for (int x = -radiusInChunks; x <= radiusInChunks; x++) {
			for (int z = -radiusInChunks; z <= radiusInChunks; z++) {
				if (x * x + z * z <= radiusInChunks * radiusInChunks) {
					if (!world.isChunkLoaded(centerX + x, centerZ + z)) continue;

					Chunk chunk = world.getChunkAt(centerX + x, centerZ + z);
					chunks.add(chunk);
				}
			}
		}

		return chunks;
	}

}
