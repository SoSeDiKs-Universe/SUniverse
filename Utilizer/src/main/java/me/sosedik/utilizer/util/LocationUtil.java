package me.sosedik.utilizer.util;

import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.utilizer.Utilizer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LocationUtil {

	private LocationUtil() {
		throw new IllegalStateException("Utility class");
	}

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
	public static @NotNull CompletableFuture<Void> runRtp(@NotNull Player player, @NotNull World world, int range) {
		if (player.getLocation().getBlockY() < 400) {
			player.teleportAsync(player.getLocation().addY(1600), PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.Relative.YAW, TeleportFlag.Relative.PITCH, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS);
		}
		Utilizer.scheduler().sync(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 25 * 20, 10)));
		Utilizer.scheduler().sync(task -> {
			if (!player.isOnline()) return true;
			if (player.isDead()) return true;

			player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 25 * 20, 10));
			return player.isOnGround() || player.isInWater();
		}, 20L, 20L);
		var teleported = new CompletableFuture<Void>();
		Utilizer.scheduler().async(() -> findLocation(player, new Location(world, 0, 600, 0), 0, range, teleported));
		return teleported;
	}

	private static void findLocation(@NotNull Player player, @NotNull Location loc, int check, int range, @NotNull CompletableFuture<Void> teleported) {
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
			player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.Relative.YAW, TeleportFlag.Relative.PITCH, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS)
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
	 * @return true, if block's bounding box is cube (1x1x1)
	 */
	public static boolean isCube(@NotNull Block block) {
		BoundingBox bb = block.getBoundingBox();
		return bb.getHeight() == 1 && bb.getWidthX() == 1 && bb.getWidthZ() == 1;
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
	public static boolean isTrulySolid(@NotNull Entity entity, @NotNull Block block) {
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
		return !Tag.SIGNS.isTagged(blockType);
		// Truly solid, congrats!
	}

}
