package me.sosedik.trappednewbie.listener.world;

import me.sosedik.limboworldgenerator.VoidChunkGenerator;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.util.LocationUtil;
import me.sosedik.utilizer.util.MiscUtil;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.File;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Creates per-player worlds
 */
public class PerPlayerWorlds implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(@NotNull PlayerChangedWorldEvent event) {
		World world = event.getFrom();
		if (!world.getKey().getKey().endsWith(event.getPlayer().getUniqueId().toString())) return;
		if (!world.getPlayers().isEmpty()) return;

		Bukkit.unloadWorld(world, true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(@NotNull PlayerSpawnLocationEvent event) {
		if (!event.isInitiallyInUnloadedWorld()) return;

		NamespacedKey dimensionId = event.getInitialDimensionId();
		if (dimensionId == null) return;
		if (!dimensionId.getNamespace().equals(TrappedNewbie.instance().getName().toLowerCase(Locale.ENGLISH))) return;

		UUID uuid = event.getPlayer().getUniqueId();
		String worldKey = dimensionId.getKey();
		if (!dimensionId.getKey().endsWith(uuid.toString())) return;
		if (!new File(worldKey).exists()) return;

		World world = Bukkit.getWorld(worldKey);
		if (world != null) {
			event.setSpawnLocation(event.getInitialLocation().world(world));
			return;
		}

		if (worldKey.startsWith("worlds-resources/")) {
			World.Environment environment = MiscUtil.parseOr(worldKey.split("/")[1], World.Environment.NORMAL);
			world = getResourceWorld(uuid, environment);
			event.setSpawnLocation(new Location(world, 0, 1600, 0));
			World finalWorld = world;
			TrappedNewbie.scheduler().sync(() -> LimboWorldFall.runRtp(event.getPlayer(), finalWorld), 1L);
		} else if (worldKey.startsWith("worlds-personal/")) {
			world = getPersonalWorld(uuid);
			event.setSpawnLocation(event.getInitialLocation().world(world));
		}
	}

	/**
	 * Gets the player's unique personal world
	 *
	 * @param playerUuid player uuid
	 * @return world instance
	 */
	public static @NotNull World getPersonalWorld(@NotNull UUID playerUuid) {
		return getWorld("worlds-personal/", playerUuid, (levelName, worldKey) -> Objects.requireNonNull(
			new WorldCreator(levelName, worldKey)
				.keepSpawnLoaded(TriState.FALSE)
				.generator(VoidChunkGenerator.GENERATOR)
				.createWorld()
		));
	}

	/**
	 * Gets the player's unique resource world
	 *
	 * @param playerUuid player uuid
	 * @return world instance
	 */
	public static @NotNull World getResourceWorld(@NotNull UUID playerUuid, @NotNull World.Environment environment) {
		return getWorld("worlds-resources/" + environment.name().toLowerCase(Locale.ENGLISH) + "/", playerUuid, (levelName, worldKey) -> Objects.requireNonNull(
			new WorldCreator(levelName, worldKey)
				.keepSpawnLoaded(TriState.FALSE)
				.environment(environment)
				.createWorld()
		));
	}

	/**
	 * Applies plugin's world game rules
	 *
	 * @param world world
	 */
	public static void applyWorldRules(@NotNull World world) {
		world.setDifficulty(Difficulty.HARD);
		world.setGameRule(GameRule.NATURAL_REGENERATION, false);
		world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
		world.setGameRule(GameRule.DO_LIMITED_CRAFTING, true);
		world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
	}

	private static @NotNull World getWorld(@NotNull String prefix, @NotNull UUID playerUuid, @NotNull BiFunction<@NotNull String, @NotNull NamespacedKey, @NotNull World> worldCreator) {
		var worldKey = worldKey(prefix, playerUuid);
		World world = Bukkit.getWorld(worldKey);
		if (world == null) {
			world = worldCreator.apply(prefix + playerUuid, worldKey);
			applyWorldRules(world);
		}
		return world;
	}

	private static @NotNull NamespacedKey worldKey(@NotNull String prefix, @NotNull UUID uuid) {
		return new NamespacedKey(TrappedNewbie.instance(), prefix + uuid);
	}

}