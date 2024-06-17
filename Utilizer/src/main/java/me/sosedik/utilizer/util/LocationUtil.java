package me.sosedik.utilizer.util;

import me.sosedik.utilizer.Utilizer;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
		if (player.getLocation().getBlockY() < 400)
			player.teleportAsync(player.getLocation().addY(1600));
		Utilizer.scheduler().sync(() -> player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 25 * 20, 10)));
		Utilizer.scheduler().sync(task -> {
			if (!player.isOnline()) return true;
			if (player.isDead()) return true;

			player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 25 * 20, 10));
			return player.isOnGround();
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
			if (RTP_BLACKLISTED_BIOMES.contains(loc.getBlock().getBiome().getKey())) {
				findLocation(player, loc, check + 1, range, teleported);
				return;
			}
			Location preLoc = player.getLocation();
			player.teleportAsync(loc.setDirection(player.getLocation().getDirection())).thenRun(() -> teleported.complete(null));
			Utilizer.logger().info("Randomly teleporting %s from %s to %s".formatted(
				player.getName(),
				"%s[%s, %s, %s]".formatted(preLoc.getWorld().getName(), preLoc.getX(), preLoc.getY(), preLoc.getZ()),
				"%s[%s, %s, %s]".formatted(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ())
			));
		});
	}

}
