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

import java.util.Objects;
import java.util.UUID;

/**
 * Creates per-player worlds
 */
public class PerPlayerWorlds implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(@NotNull PlayerQuitEvent event) {
		String worldName = event.getPlayer().getUniqueId().toString();
		Bukkit.unloadWorld(worldName, true); // TODO what if someone else is in this world?
	}

	/**
	 * Gets the player's unique world
	 *
	 * @param playerUuid player uuid
	 * @return world instance
	 */
	public static @NotNull World getWorld(@NotNull UUID playerUuid) {
		var worldKey = worldKey(playerUuid);
		World world = Bukkit.getWorld(worldKey);
		if (world == null)
			world = createWorld("worlds/" + playerUuid, worldKey);
		return world;
	}

	private static @NotNull World createWorld(@NotNull String worldName, @NotNull NamespacedKey worldKey) {
		return Objects.requireNonNull(
				new WorldCreator(worldName, worldKey)
					.keepSpawnLoaded(TriState.FALSE)
					.generator(EmptyWorldGenerator.GENERATOR)
					.createWorld()
		);
	}

	private static @NotNull NamespacedKey worldKey(@NotNull UUID uuid) {
		return new NamespacedKey(TrappedNewbie.instance(), uuid.toString());
	}

}
