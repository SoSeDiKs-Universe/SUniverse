package me.sosedik.trappednewbie.listener.world;

import io.papermc.paper.event.entity.EntityPortalReadyEvent;
import me.sosedik.limboworldgenerator.VoidChunkGenerator;
import me.sosedik.miscme.task.CustomDayCycleTask;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.util.FileUtil;
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
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.File;
import java.util.ArrayList;
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLeave(PlayerQuitEvent event) {
		UUID playerUuid = event.getPlayer().getUniqueId();
		TrappedNewbie.scheduler().sync(() -> unloadIfEmpty(playerUuid), 1L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerLoginEvent event) { // TODO cleanup in case the player didn't join
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;

		UUID playerUuid = event.getPlayer().getUniqueId();
		getPersonalWorld(playerUuid);
		for (World.Environment environment : RESOURCE_ENVIRONMENTS)
			getResourceWorld(playerUuid, environment);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerSpawnLocationEvent event) {
		if (!event.isInitiallyInUnloadedWorld()) return;

		NamespacedKey dimensionId = event.getInitialDimensionId();
		if (dimensionId == null) return;
		if (!TrappedNewbie.NAMESPACE.equals(dimensionId.getNamespace())) return;

		String worldKey = dimensionId.getKey();
		World world = Bukkit.getWorld(worldKey);
		if (world != null) {
			event.setSpawnLocation(event.getInitialLocation().world(world));
			return;
		}

		if (worldKey.startsWith("worlds-resources/")) {
			String[] split = worldKey.split("/");
			UUID uuid;
			try {
				uuid = UUID.fromString(split[split.length - 1]);
			} catch (IllegalArgumentException ignored) {
				return;
			}
			boolean rtp = !new File(Bukkit.getWorldContainer(), worldKey).exists();
			World.Environment environment = MiscUtil.parseOr(split[1], World.Environment.NORMAL);
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

			String[] split = worldKey.split("/");
			UUID uuid;
			try {
				uuid = UUID.fromString(split[split.length - 1]);
			} catch (IllegalArgumentException ignored) {
				return;
			}
			world = getPersonalWorld(uuid);
			event.setSpawnLocation(event.getInitialLocation().world(world));
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPortal(EntityPortalEnterEvent event) {
		World worldFrom = event.getEntity().getWorld();
		if (NamespacedKey.MINECRAFT.equals(worldFrom.key().namespace())) {
			event.setCancelled(true);
			return;
		}

		if (!TrappedNewbie.NAMESPACE.equals(worldFrom.key().namespace())) return;

		String worldKey = worldFrom.key().value();
		if (!worldKey.startsWith("worlds-personal/")) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPortal(EntityPortalReadyEvent event) {
		World worldFrom = event.getEntity().getWorld();
		if (NamespacedKey.MINECRAFT.equals(worldFrom.key().namespace())) {
			event.setTargetWorld(null);
			event.setCancelled(true);
			return;
		}

		if (!TrappedNewbie.NAMESPACE.equals(worldFrom.key().namespace())) return;

		String worldKey = worldFrom.key().value();
		if (!worldKey.startsWith("worlds-resources/")) {
			event.setTargetWorld(null);
			event.setCancelled(true);
			return;
		}

		String[] split = worldKey.split("/");
		UUID playerUuid;
		try {
			playerUuid = UUID.fromString(split[2]);
		} catch (IndexOutOfBoundsException | IllegalArgumentException e) {
			event.setTargetWorld(null);
			event.setCancelled(true);
			return;
		}

		switch (event.getPortalType()) {
			case NETHER -> {
				World.Environment targetEnv = worldFrom.getEnvironment() == World.Environment.NORMAL ? World.Environment.NETHER : World.Environment.NORMAL;
				World worldTo = getResourceWorld(playerUuid, targetEnv, true);
				event.setTargetWorld(worldTo);
			}
			case ENDER -> {
				World.Environment targetEnv = worldFrom.getEnvironment() == World.Environment.NORMAL ? World.Environment.THE_END : World.Environment.NORMAL;
				World worldTo = getResourceWorld(playerUuid, targetEnv, true);
				event.setTargetWorld(worldTo);
			}
			case END_GATEWAY -> {
				World worldTo = getResourceWorld(playerUuid, World.Environment.NORMAL, true);
				event.setTargetWorld(worldTo);
			}
			default -> {
				event.setTargetWorld(null);
				event.setCancelled(true);
			}
		}
	}

	private void unloadIfEmpty(UUID playerUuid) {
		List<World> toUnload = new ArrayList<>();
		if (!unloadIfEmpty(getPersonalWorld(playerUuid, false), toUnload)) return;
		for (World.Environment environment : RESOURCE_ENVIRONMENTS) {
			if (!unloadIfEmpty(getResourceWorld(playerUuid, environment, false), toUnload))
				return;
		}
		toUnload.forEach(world -> Bukkit.unloadWorld(world, true));
	}

	private boolean unloadIfEmpty(@Nullable World world, List<World> toUnload) {
		if (world == null) return true;
		if (!world.getPlayers().isEmpty()) return false;

		toUnload.add(world);
		return true;
	}

	private static void startDayCycleTask(World world) {
		new CustomDayCycleTask(world, () -> {
			if (Bukkit.getServerTickManager().isFrozen()) return 0D;
			if (world.isDayTime()) return DAY_TIME_TICK_INCREASE;

			double incrementTimeBy = NIGHT_TIME_TICK_INCREASE;
			int sleepers = (int) Bukkit.getOnlinePlayers().stream().filter(Player::isDeeplySleeping).filter(Player::bedExists).count();
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
		return getPersonalWorld(playerUuid, true);
	}

	/**
	 * Gets the player's unique personal world
	 *
	 * @param playerUuid player uuid
	 * @return world instance
	 */
	@Contract("_, true -> !null")
	public static @Nullable World getPersonalWorld(UUID playerUuid, boolean load) {
		return getWorld("worlds-personal/", playerUuid,
				(levelName, worldKey) -> requireNonNull(
					new WorldCreator(levelName, worldKey)
						.keepSpawnLoaded(TriState.FALSE)
						.generator(VoidChunkGenerator.GENERATOR)
						.createWorld()
				), load
		);
	}

	/**
	 * Gets the player's unique resource world
	 *
	 * @param playerUuid player uuid
	 * @param environment environment
	 * @return world instance
	 */
	public static World getResourceWorld(UUID playerUuid, World.Environment environment) {
		return getResourceWorld(playerUuid, environment, true);
	}

	/**
	 * Gets the player's unique resource world
	 *
	 * @param playerUuid player uuid
	 * @param environment environment
	 * @param load whether to load/create if not found
	 * @return world instance
	 */
	@Contract("_, _, true -> !null")
	public static @Nullable World getResourceWorld(UUID playerUuid, World.Environment environment, boolean load) {
		if (!RESOURCE_ENVIRONMENTS.contains(environment)) throw new IllegalArgumentException("Invalid resources dimension: %s".formatted(environment.name()));
		return getWorld("worlds-resources/" + environment.name().toLowerCase(Locale.US) + "/", playerUuid,
				(levelName, worldKey) -> {
					File mainWorldFile = Bukkit.getWorlds().getFirst().getWorldFolder();
					File settingsFile = null;
					switch (environment) {
						case NORMAL -> settingsFile = new File(mainWorldFile, "paper-world.yml");
						case NETHER -> settingsFile = new File(mainWorldFile.getParentFile(), mainWorldFile.getName() + "_nether/paper-world.yml");
						case THE_END -> settingsFile = new File(mainWorldFile.getParentFile(), mainWorldFile.getName() + "_the_end/paper-world.yml");
					}
					if (settingsFile != null && settingsFile.exists()) {
						var destinationFile = new File(mainWorldFile.getParentFile(), "worlds-resources" + File.separator + environment.name().toLowerCase(Locale.US) + File.separator + playerUuid + "/paper-world.yml");
						FileUtil.deleteFile(destinationFile);
						FileUtil.copyFile(settingsFile, destinationFile);
					}
					return requireNonNull(
						new WorldCreator(levelName, worldKey)
							.keepSpawnLoaded(TriState.FALSE)
							.environment(environment)
							.createWorld()
					);
				}, load
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

	private static @Nullable World getWorld(String prefix, UUID playerUuid, BiFunction<String, NamespacedKey, World> worldCreator, boolean load) {
		var worldKey = worldKey(prefix, playerUuid);
		World world = Bukkit.getWorld(worldKey);
		if (world == null && load) {
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
