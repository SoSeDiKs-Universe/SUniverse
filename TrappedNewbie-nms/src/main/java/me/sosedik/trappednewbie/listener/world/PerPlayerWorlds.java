package me.sosedik.trappednewbie.listener.world;

import me.sosedik.limboworldgenerator.VoidChunkGenerator;
import me.sosedik.miscme.task.CustomDayCycleTask;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.util.MiscUtil;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;

/**
 * Creates per-player worlds
 */
@NullMarked
public class PerPlayerWorlds implements Listener {

	private static final double DAY_TIME_TICK_INCREASE = 1;
	private static final double NIGHT_TIME_TICK_INCREASE = DAY_TIME_TICK_INCREASE;
	
	private static final List<World.Environment> RESOURCE_ENVIRONMENTS = List.of(
		World.Environment.NORMAL, World.Environment.NETHER, World.Environment.THE_END
	);

//	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLeave(PlayerChangedWorldEvent event) {
		World world = event.getFrom();
		if (!world.getKey().getKey().endsWith(event.getPlayer().getUniqueId().toString())) return;
		if (!world.getPlayers().isEmpty()) return;

		Bukkit.unloadWorld(world, true); // TODO "breaks" /world command
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		unloadIfNeeded(getPersonalWorld(player.getUniqueId()));
		for (World.Environment environment : RESOURCE_ENVIRONMENTS)
			unloadIfNeeded(getResourceWorld(player.getUniqueId(), environment));
	}

	private void unloadIfNeeded(World world) {
		if (!world.getPlayers().isEmpty()) return;

		Bukkit.unloadWorld(world, true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreJoin(AsyncPlayerPreLoginEvent event) {
		TrappedNewbie.scheduler().sync(() -> {
			getPersonalWorld(event.getUniqueId());
			for (World.Environment environment : RESOURCE_ENVIRONMENTS)
				getResourceWorld(event.getUniqueId(), environment);
		});
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerSpawnLocationEvent event) {
		if (!event.isInitiallyInUnloadedWorld()) return;

		NamespacedKey dimensionId = event.getInitialDimensionId();
		if (dimensionId == null) return;
		if (!TrappedNewbie.NAMESPACE.equals(dimensionId.getNamespace())) return;

		UUID uuid = event.getPlayer().getUniqueId();
		String worldKey = dimensionId.getKey();
		if (!dimensionId.getKey().endsWith(uuid.toString())) return;

		World world = Bukkit.getWorld(worldKey);
		if (world != null) {
			event.setSpawnLocation(event.getInitialLocation().world(world));
			return;
		}

		if (worldKey.startsWith("worlds-resources/")) {
			boolean rtp = !new File(Bukkit.getWorldContainer(), worldKey).exists();
			World.Environment environment = MiscUtil.parseOr(worldKey.split("/")[1], World.Environment.NORMAL);
			world = getResourceWorld(uuid, environment);
			if (!rtp) {
				event.setSpawnLocation(event.getInitialLocation().world(world));
				return;
			}
			event.setSpawnLocation(new Location(world, 0, 1600, 0));
			World finalWorld = world;
			TrappedNewbie.scheduler().sync(() -> LimboWorldFall.runRtp(event.getPlayer(), finalWorld), 1L);
		} else if (worldKey.startsWith("worlds-personal/")) {
			if (!new File(Bukkit.getWorldContainer(), worldKey).exists()) return;

			world = getPersonalWorld(uuid);
			event.setSpawnLocation(event.getInitialLocation().world(world));
		}
	}

	private static void startDayCycleTask(World world) {
		new CustomDayCycleTask(world, () -> {
			if (Bukkit.getServerTickManager().isFrozen()) return 0D;
			if (world.isDayTime()) return DAY_TIME_TICK_INCREASE;

			double incrementTimeBy = NIGHT_TIME_TICK_INCREASE;
			int sleepers = (int) Bukkit.getOnlinePlayers().stream().filter(Player::isSleeping).count();
			if (sleepers > 0) {
				int players = (int) Bukkit.getOnlinePlayers().stream().filter(PerPlayerWorlds::isSleepCounted).count();
				if (players > 0)
					incrementTimeBy += 6D * sleepers / players;
			}
			return incrementTimeBy;
		});
	}

	private static boolean isSleepCounted(Player player) {
		return !player.isSleepingIgnored()
				&& !player.getGameMode().isInvulnerable();
	}

	/**
	 * Gets the player's unique personal world
	 *
	 * @param playerUuid player uuid
	 * @return world instance
	 */
	public static World getPersonalWorld(UUID playerUuid) {
		return getWorld("worlds-personal/", playerUuid,
				(levelName, worldKey) -> requireNonNull(
					new WorldCreator(levelName, worldKey)
						.keepSpawnLoaded(TriState.FALSE)
						.generator(VoidChunkGenerator.GENERATOR)
						.createWorld()
				)
			);
	}

	/**
	 * Gets the player's unique resource world
	 *
	 * @param playerUuid player uuid
	 * @return world instance
	 */
	public static World getResourceWorld(UUID playerUuid, World.Environment environment) {
		if (!RESOURCE_ENVIRONMENTS.contains(environment)) throw new IllegalArgumentException("Invalid resources dimension: %s".formatted(environment.name()));
		return getWorld("worlds-resources/" + environment.name().toLowerCase(Locale.US) + "/", playerUuid,
				(levelName, worldKey) -> requireNonNull(
					new WorldCreator(levelName, worldKey)
						.keepSpawnLoaded(TriState.FALSE)
						.environment(environment)
						.createWorld()
				)
			);
	}

	/**
	 * Applies plugin's world game rules
	 *
	 * @param world world
	 */
	public static void applyWorldRules(World world) {
		world.setDifficulty(Difficulty.HARD);
		world.setGameRule(GameRule.NATURAL_REGENERATION, false);
		world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		world.setGameRule(GameRule.DO_LIMITED_CRAFTING, true);
		world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
		world.setGameRule(GameRule.SPAWN_CHUNK_RADIUS, 0);
	}

	private static World getWorld(String prefix, UUID playerUuid, BiFunction<String, NamespacedKey, World> worldCreator) {
		var worldKey = worldKey(prefix, playerUuid);
		World world = Bukkit.getWorld(worldKey);
		if (world == null) {
			world = worldCreator.apply(prefix + playerUuid, worldKey);
			applyWorldRules(world);
			startDayCycleTask(world);
		}
		return world;
	}

	private static NamespacedKey worldKey(String prefix, UUID uuid) {
		return TrappedNewbie.trappedNewbieKey(prefix + uuid);
	}

}
