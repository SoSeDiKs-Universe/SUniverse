package me.sosedik.moves.listener.movement;

import me.sosedik.moves.Moves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Counts number on player's sneaks
 */
public class SneakCounter implements Listener {

	private static final Map<UUID, Integer> SNEAKS = new HashMap<>();
	private static final Map<UUID, Long> LAST_SNEAK_TIMES = new HashMap<>();

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSneak(@NotNull PlayerToggleSneakEvent event) {
		if (event.isSneaking())
			increaseSneaksCount(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(@NotNull PlayerQuitEvent event) {
		LAST_SNEAK_TIMES.remove(event.getPlayer().getUniqueId());
	}

	private static void increaseSneaksCount(@NotNull Player player) {
		UUID uuid = player.getUniqueId();
		int count = getSneaksCount(player) + 1;
		SNEAKS.put(uuid, Integer.valueOf(count));
		LAST_SNEAK_TIMES.put(uuid, System.currentTimeMillis());
		Moves.scheduler().async(() -> {
			if (getSneaksCount(player) == count)
				SNEAKS.remove(uuid);
		}, 18L);
	}

	/**
	 * Gets number of times that the player has recently quickly sneaked
	 *
	 * @param player player
	 * @return number of sneaks
	 */
	public static int getSneaksCount(@NotNull Player player) {
		return SNEAKS.getOrDefault(player.getUniqueId(), 0);
	}

	/**
	 * Gets the time passed (in ticks) since last sneak
	 *
	 * @param player player
	 * @return the time passed (in ticks) since last sneak
	 */
	public static int getTimeSinceLastSneak(@NotNull Player player) {
		Long lastSneakTIme = LAST_SNEAK_TIMES.get(player.getUniqueId());
		return lastSneakTIme == null
				? Integer.MAX_VALUE
				: (int) ((System.currentTimeMillis() - lastSneakTIme) / 50D);
	}

}
