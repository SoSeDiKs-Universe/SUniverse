package me.sosedik.trappednewbie.listener.world;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.feature.worldgen.EmptyWorldGenerator;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Creates per-player worlds
 */
public class PerPlayerWorlds implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(@NotNull PlayerQuitEvent event) {
		String worldName = event.getPlayer().getUniqueId().toString();
		Bukkit.unloadWorld("worlds/" + worldName, true); // TODO what if someone else is in this world?
	}

	/**
	 * Gets the player's unique personal world
	 *
	 * @param playerUuid player uuid
	 * @return world instance
	 */
	public static @NotNull World getPersonalWorld(@NotNull UUID playerUuid) {
		return getWorld("worlds/", playerUuid, (levelName, worldKey) -> Objects.requireNonNull(
			new WorldCreator(levelName, worldKey)
				.keepSpawnLoaded(TriState.FALSE)
				.generator(EmptyWorldGenerator.GENERATOR)
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
		return getWorld("resource-worlds/" + environment.name().toLowerCase(Locale.ENGLISH) + "/", playerUuid, (levelName, worldKey) -> Objects.requireNonNull(
			new WorldCreator(levelName, worldKey)
				.keepSpawnLoaded(TriState.FALSE)
				.environment(environment)
				.createWorld()
		));
	}

	private static @NotNull World getWorld(@NotNull String prefix, @NotNull UUID playerUuid, @NotNull BiFunction<@NotNull String, @NotNull NamespacedKey, @NotNull World> worldCreator) {
		var worldKey = worldKey(prefix, playerUuid);
		World world = Bukkit.getWorld(worldKey);
		if (world == null)
			world = worldCreator.apply(prefix + playerUuid, worldKey);
		return world;
	}

	private static @NotNull NamespacedKey worldKey(@NotNull String prefix, @NotNull UUID uuid) {
		return new NamespacedKey(TrappedNewbie.instance(), prefix + uuid);
	}

}
