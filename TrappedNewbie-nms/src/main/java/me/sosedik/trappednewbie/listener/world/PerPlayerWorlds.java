package me.sosedik.trappednewbie.listener.world;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import io.papermc.paper.connection.PlayerConfigurationConnection;
import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import io.papermc.paper.event.entity.EntityPortalReadyEvent;
import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent;
import io.papermc.paper.math.Position;
import me.sosedik.delightfulfarming.feature.sugar.MealTime;
import me.sosedik.limboworldgenerator.VoidChunkGenerator;
import me.sosedik.miscme.task.CustomDayCycleTask;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.util.FileUtil;
import me.sosedik.utilizer.util.MiscUtil;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRules;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Creates per-player worlds
 */
@NullMarked
public class PerPlayerWorlds implements Listener {

	public static final String PERSONAL_WORLDS_NAMESPACE = "personal";
	public static final String RESOURCE_WORLDS_NAMESPACE_PREFIX = "resources-";

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

		LimboWorldFall.runTeleport(player, player.getWorld(), null, true);
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

		World world = Bukkit.getWorld(dimensionId);
		if (world != null) {
			event.setSpawnLocation(event.getInitialLocation().world(world));
			return;
		}

		if (dimensionId.namespace().startsWith(RESOURCE_WORLDS_NAMESPACE_PREFIX)) {
			UUID worldPlayerUuid;
			try {
				worldPlayerUuid = UUID.fromString(dimensionId.value());
			} catch (IllegalArgumentException ignored) {
				event.setSpawnLocation(Utilizer.limboWorld().getSpawnLocation().center(1));
				return;
			}
			boolean rtp = !new File(getWorldsContainer(), dimensionId.namespace() + File.separator + worldPlayerUuid).exists();
			World.Environment environment = switch (dimensionId.namespace()) {
				case RESOURCE_WORLDS_NAMESPACE_PREFIX + "the_nether" -> World.Environment.NETHER;
				case RESOURCE_WORLDS_NAMESPACE_PREFIX + "the_end" -> World.Environment.THE_END;
				default -> World.Environment.NORMAL;
			};
			CompletableFuture<World> worldGetter = new CompletableFuture<>();
			TrappedNewbie.scheduler().sync(() -> worldGetter.complete(getResourceWorld(worldPlayerUuid, environment)));
			world = worldGetter.get();
			if (!rtp) {
				event.setSpawnLocation(event.getInitialLocation().world(world));
				return;
			}
			event.setSpawnLocation(new Location(world, 0, world.getMaxHeight() + 50, 0));
			UUID playerUuid = event.getConnection().getProfile().getId();
			if (playerUuid != null)
				DELAYED_FALLS.add(playerUuid);
		} else if (PERSONAL_WORLDS_NAMESPACE.equals(dimensionId.namespace())) {
			if (!new File(getWorldsContainer(World.Environment.CUSTOM), dimensionId.value()).exists()) {
				event.setSpawnLocation(Utilizer.limboWorld().getSpawnLocation().center(1));
				return;
			}

			UUID worldPlayerUuid;
			try {
				worldPlayerUuid = UUID.fromString(dimensionId.value());
			} catch (IllegalArgumentException ignored) {
				event.setSpawnLocation(Utilizer.limboWorld().getSpawnLocation().center(1));
				return;
			}
			CompletableFuture<World> worldGetter = new CompletableFuture<>();
			TrappedNewbie.scheduler().sync(() -> worldGetter.complete(getPersonalWorld(worldPlayerUuid)));
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

		if (!PERSONAL_WORLDS_NAMESPACE.equals(worldFrom.key().namespace())) return;

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

		Key worldKey = worldFrom.key();
		if (PERSONAL_WORLDS_NAMESPACE.equals(worldKey.namespace())) {
			event.setTargetWorld(null);
			event.setCancelled(true);
			return;
		}

		if (!worldKey.namespace().startsWith(RESOURCE_WORLDS_NAMESPACE_PREFIX)) return;

		UUID playerUuid;
		try {
			playerUuid = UUID.fromString(worldKey.value());
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
		Key worldKey = world.key();
		if (world == Utilizer.limboWorld() || NamespacedKey.MINECRAFT.equals(worldKey.namespace()))
			return resolveVanillaWorld(environment);

		if (PERSONAL_WORLDS_NAMESPACE.equals(worldKey.namespace()) || worldKey.namespace().startsWith(RESOURCE_WORLDS_NAMESPACE_PREFIX)) {
			try {
				UUID playerUuid = UUID.fromString(worldKey.value());
				return environment == World.Environment.CUSTOM
					? getPersonalWorld(playerUuid)
					: getResourceWorld(playerUuid, environment, true);
			} catch (IndexOutOfBoundsException | IllegalArgumentException ignored) {
				return Utilizer.limboWorld();
			}
		}

		return Utilizer.limboWorld();
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
		var worldKey = new NamespacedKey(PERSONAL_WORLDS_NAMESPACE, playerUuid.toString());
		return getWorld(worldKey, load, true, () -> requireNonNull(
				 WorldCreator.ofKey(worldKey)
					 .generator(VoidChunkGenerator.GENERATOR)
					 .forcedSpawnPosition(Position.fine(0.5, 121, 0.5), 0F, 0F)
					 .createWorld()
			));
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

		String environmentKey = MiscUtil.getDimensionKey(environment);
		var worldKey = new NamespacedKey(RESOURCE_WORLDS_NAMESPACE_PREFIX + environmentKey, playerUuid.toString());
		return getWorld(worldKey, load, environment == World.Environment.NORMAL, () -> {
				File mainWorldFile = Bukkit.getWorlds().getFirst().getWorldFolder();
				File settingsFile = null;
				switch (environment) {
					case NORMAL -> settingsFile = new File(mainWorldFile, "paper-world.yml");
					case NETHER -> settingsFile = new File(mainWorldFile.getParentFile(), "the_nether/paper-world.yml");
					case THE_END -> settingsFile = new File(mainWorldFile.getParentFile(), "the_end/paper-world.yml");
				}
				if (settingsFile.exists()) {
					var destinationFile = new File(getWorldsContainer(), worldKey.namespace() + File.separator + worldKey.value() + "/paper-world.yml");
					FileUtil.deleteFile(destinationFile);
					FileUtil.copyFile(settingsFile, destinationFile);
				}
				return requireNonNull(
					new WorldCreator(worldKey)
						.environment(environment)
						.createWorld()
				);
			});
	}

	/**
	 * Gets the folder where worlds lie
	 *
	 * @return the folder where worlds lie
	 */
	public static File getWorldsContainer() {
		return Bukkit.getWorlds().getFirst().getWorldFolder().getParentFile().getParentFile();
	}

	/**
	 * Gets the folder where worlds lie
	 *
	 * @param environment environment, {@link World.Environment#CUSTOM} for personal worlds
	 * @return the folder where worlds lie
	 */
	public static File getWorldsContainer(World.Environment environment) {
		return switch (environment) {
			case NORMAL, NETHER, THE_END -> new File(getWorldsContainer(), RESOURCE_WORLDS_NAMESPACE_PREFIX + MiscUtil.getDimensionKey(environment));
			case CUSTOM -> new File(getWorldsContainer(), PERSONAL_WORLDS_NAMESPACE);
		};
	}

	/**
	 * Applies plugin's world game rules
	 *
	 * @param world world
	 */
	public static void applyWorldRules(World world) {
		world.setDifficulty(Difficulty.HARD);
		world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false);
		world.setGameRule(GameRules.IMMEDIATE_RESPAWN, false);
		world.setGameRule(GameRules.LIMITED_CRAFTING, false);
		world.setGameRule(GameRules.REDUCED_DEBUG_INFO, true);
	}

	private static void applyVoidWorldRules(World world) {
		world.setFullTime(0);
		world.setGameRule(GameRules.SPAWN_PHANTOMS, false);
		world.setGameRule(GameRules.SPAWN_MOBS, false);
		world.setGameRule(GameRules.SPAWN_MONSTERS, false);
		world.setGameRule(GameRules.SPAWN_PATROLS, false);
		world.setGameRule(GameRules.SPAWN_WANDERING_TRADERS, false);
		world.setGameRule(GameRules.SPAWN_WARDENS, false);
		world.setGameRule(GameRules.SPREAD_VINES, false);
		world.setGameRule(GameRules.MOB_GRIEFING, false);

		Block block = world.getBlockAt(0, 120, 0);
		if (block.isEmpty()) {
			block.setType(Material.BEDROCK);
			world.setSpawnLocation(0, 121, 0);
		}
	}

	private static @Nullable World getWorld(NamespacedKey worldKey, boolean load, boolean tickTime, Supplier<World> worldCreator) {
		World world = Bukkit.getWorld(worldKey);
		if (world == null && load) {
			world = worldCreator.get();
			applyWorldRules(world);
			if (PERSONAL_WORLDS_NAMESPACE.equals(worldKey.namespace()))
				applyVoidWorldRules(world);
			if (tickTime)
				startDayCycleTask(world);
		}
		return world;
	}

}
