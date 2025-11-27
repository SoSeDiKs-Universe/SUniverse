package me.sosedik.trappednewbie.listener.world;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import io.papermc.paper.event.entity.EntityPortalReadyEvent;
import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent;
import me.sosedik.delightfulfarming.feature.sugar.MealTime;
import me.sosedik.limboworldgenerator.VoidChunkGenerator;
import me.sosedik.miscme.task.CustomDayCycleTask;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.util.FileUtil;
import me.sosedik.utilizer.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.ServerTickManager;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;

import static java.util.Objects.requireNonNull;

/**
 * Creates per-player worlds
 */
@NullMarked
public class PerPlayerWorlds implements Listener {

	private static final double DAY_TIME_TICK_INCREASE = 0.375; // 40 minutes
	private static final double NIGHT_TIME_TICK_INCREASE = 0.25; // 20 minutes
	
	private static final List<World.Environment> RESOURCE_ENVIRONMENTS = List.of(
		World.Environment.NORMAL, World.Environment.NETHER, World.Environment.THE_END
	);

	private static final Set<UUID> DELAYED_FALLS = new HashSet<>();

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!DELAYED_FALLS.remove(player.getUniqueId())) return;

		LimboWorldFall.runTeleport(player, player.getWorld(), true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLeave(PlayerConnectionCloseEvent event) {
		UUID playerUuid = event.getPlayerUniqueId();
		DELAYED_FALLS.remove(playerUuid);
		TrappedNewbie.scheduler().sync(() -> unloadIfEmpty(playerUuid), 1L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerConnectionValidateLoginEvent event) {
		if (!event.isAllowed()) return;
		if (!(event.getConnection() instanceof PlayerConfigurationConnection connection)) return;

		UUID playerUuid = connection.getProfile().getId();
		if (playerUuid == null) return;

		getPersonalWorld(playerUuid);
		for (World.Environment environment : RESOURCE_ENVIRONMENTS)
			getResourceWorld(playerUuid, environment);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(AsyncPlayerSpawnLocationEvent event) throws ExecutionException, InterruptedException {
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
				event.setSpawnLocation(Utilizer.limboWorld().getSpawnLocation().center(1));
				return;
			}
			boolean rtp = !new File(Bukkit.getWorldContainer(), worldKey).exists();
			World.Environment environment = MiscUtil.parseOr(split[1], World.Environment.NORMAL);
			CompletableFuture<World> worldGetter = new CompletableFuture<>();
			TrappedNewbie.scheduler().sync(() -> worldGetter.complete(getResourceWorld(uuid, environment)));
			world = worldGetter.get();
			if (!rtp) {
				event.setSpawnLocation(event.getInitialLocation().world(world));
				return;
			}
			event.setSpawnLocation(new Location(world, 0, world.getMaxHeight() + 50, 0));
			UUID playerUuid = event.getConnection().getProfile().getId();
			if (playerUuid != null)
				DELAYED_FALLS.add(playerUuid);
		} else if (worldKey.startsWith("worlds-personal/")) {
			if (!new File(Bukkit.getWorldContainer(), worldKey).exists()) {
				event.setSpawnLocation(Utilizer.limboWorld().getSpawnLocation().center(1));
				return;
			}

			String[] split = worldKey.split("/");
			UUID uuid;
			try {
				uuid = UUID.fromString(split[split.length - 1]);
			} catch (IllegalArgumentException ignored) {
				event.setSpawnLocation(Utilizer.limboWorld().getSpawnLocation().center(1));
				return;
			}
			CompletableFuture<World> worldGetter = new CompletableFuture<>();
			TrappedNewbie.scheduler().sync(() -> worldGetter.complete(getPersonalWorld(uuid)));
			world = worldGetter.get();
			event.setSpawnLocation(event.getInitialLocation().world(world));
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPortal(EntityPortalEnterEvent event) {
		World worldFrom = event.getEntity().getWorld();
		if (worldFrom == Utilizer.limboWorld()) {
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
		if (worldFrom == Utilizer.limboWorld()) {
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
			playerUuid = UUID.fromString(split[split.length - 1]);
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

	/**
	 * Starts a day cycle task
	 *
	 * @param world world
	 */
	public static void startDayCycleTask(World world) {
		new CustomDayCycleTask(world, () -> {
			ServerTickManager serverTickManager = Bukkit.getServerTickManager();
			if (serverTickManager.isFrozen()) return 0D;

			boolean nightTime = MealTime.NIGHT_SNACK.is(world.getTime());
			double incrementTimeBy = nightTime ? NIGHT_TIME_TICK_INCREASE : DAY_TIME_TICK_INCREASE;

			int sleepers = (int) Bukkit.getOnlinePlayers().stream().filter(Player::isDeeplySleeping).filter(Player::bedExists).count();
			if (sleepers > 0) {
				int players = (int) Bukkit.getOnlinePlayers().stream().filter(PerPlayerWorlds::isSleepCounted).count();
				if (players > 0)
					incrementTimeBy += 6D * sleepers / players;
			}

			float tickRate = serverTickManager.getTickRate();
			if (tickRate != 20F) incrementTimeBy *= tickRate / 20F;

			return incrementTimeBy;
		});
	}

	private static boolean isSleepCounted(Player player) {
		return !player.isSleepingIgnored()
			&& !player.getGameMode().isInvulnerable();
	}

	/**
	 * Resolves player's world for the environment
	 *
	 * @param player player
	 * @param environment environment
	 * @return world
	 */
	public static World resolveWorld(Player player, World.Environment environment) {
		World world = player.getWorld();
		if (world == Utilizer.limboWorld() || NamespacedKey.MINECRAFT.equals(world.key().namespace()))
			return resolveVanillaWorld(environment);

		if (!TrappedNewbie.NAMESPACE.equals(world.key().namespace()))
			return Utilizer.limboWorld();

		String worldKey = world.key().value();
		if (!worldKey.startsWith("worlds-personal/") && !worldKey.startsWith("worlds-resources/"))
			return Utilizer.limboWorld();

		String[] split = worldKey.split("/");
		UUID playerUuid;
		try {
			playerUuid = UUID.fromString(split[split.length - 1]);
		} catch (IndexOutOfBoundsException | IllegalArgumentException ignored) {
			return Utilizer.limboWorld();
		}

		return environment == World.Environment.CUSTOM
			? getPersonalWorld(playerUuid)
			: getResourceWorld(playerUuid, environment, true);
	}

	/**
	 * Resolves vanilla world
	 *
	 * @param environment environment
	 * @return vanilla world
	 */
	public static World resolveVanillaWorld(World.Environment environment) {
		World world = switch (environment) {
			case NORMAL -> Bukkit.getWorlds().getFirst();
			case NETHER -> Bukkit.getWorld(NamespacedKey.minecraft("the_nether"));
			case THE_END -> Bukkit.getWorld(NamespacedKey.minecraft("the_end"));
			default -> Utilizer.limboWorld();
		};
		return world == null ? Utilizer.limboWorld() : world;
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
					 WorldCreator.ofNameAndKey(levelName, worldKey)
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
		world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
		world.setGameRule(GameRule.DO_LIMITED_CRAFTING, false);
		world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
	}

	private static void applyVoidWorldRules(World world) {
		world.setFullTime(0);
		world.setGameRule(GameRule.DO_INSOMNIA, false);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
		world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
		world.setGameRule(GameRule.DO_WARDEN_SPAWNING, false);
		world.setGameRule(GameRule.DO_VINES_SPREAD, false);
		world.setGameRule(GameRule.MOB_GRIEFING, false);

		Block block = world.getBlockAt(0, 120, 0);
		if (block.isEmpty()) {
			block.setType(Material.BEDROCK);
			world.setSpawnLocation(0, 121, 0);
		}
	}

	private static @Nullable World getWorld(String prefix, UUID playerUuid, BiFunction<String, NamespacedKey, World> worldCreator, boolean load) {
		var worldKey = worldKey(prefix, playerUuid);
		World world = Bukkit.getWorld(worldKey);
		if (world == null && load) {
			world = worldCreator.apply(prefix + playerUuid, worldKey);
			applyWorldRules(world);
			if (world.key().value().startsWith("worlds-personal/"))
				applyVoidWorldRules(world);
			startDayCycleTask(world);
		}
		return world;
	}

	private static NamespacedKey worldKey(String prefix, UUID uuid) {
		return TrappedNewbie.trappedNewbieKey(prefix + uuid);
	}

}
