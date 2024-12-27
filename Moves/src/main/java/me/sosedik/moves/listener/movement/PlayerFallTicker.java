package me.sosedik.moves.listener.movement;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import me.sosedik.moves.api.event.PlayerResetFallingEvent;
import me.sosedik.moves.api.event.PlayerStartFallingEvent;
import me.sosedik.moves.api.event.PlayerStopFallingEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks falling states of players
 */
@NullMarked
public class PlayerFallTicker implements Listener {

	private static final Map<UUID, Float> FALLING_TRACKER = new HashMap<>();
	private static final Map<UUID, Vector> PRE_VELOCITY_TRACKER = new HashMap<>();
	private static final Map<UUID, Vector> VELOCITY_TRACKER = new HashMap<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onTick(ServerTickStartEvent event) {
		Bukkit.getOnlinePlayers().forEach(player -> {
			UUID uuid = player.getUniqueId();

			Float preFallDistance = FALLING_TRACKER.get(uuid);
			boolean lastFallState = preFallDistance != null;
			float fallDistance = player.getFallDistance();
			boolean currentFallState = fallDistance > 0F;

			if (!lastFallState && currentFallState) new PlayerStartFallingEvent(player).callEvent();
			else if (lastFallState && preFallDistance > fallDistance && currentFallState) new PlayerResetFallingEvent(player, preFallDistance).callEvent();
			else if (lastFallState && !currentFallState) new PlayerStopFallingEvent(player, preFallDistance).callEvent();

			if (currentFallState) FALLING_TRACKER.put(uuid, fallDistance);
			else if (lastFallState) FALLING_TRACKER.remove(uuid);

			Vector oldVelocity = VELOCITY_TRACKER.put(uuid, player.getVelocity());
			if (oldVelocity != null)
				PRE_VELOCITY_TRACKER.put(uuid, oldVelocity);
		});
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		FALLING_TRACKER.remove(uuid);
		VELOCITY_TRACKER.remove(uuid);
		PRE_VELOCITY_TRACKER.remove(uuid);
	}

	/**
	 * Gets the stored fall distance from the last fall
	 *
	 * @param player player
	 * @return stored fall distance
	 */
	public static float getStoredFallDistance(Player player) {
		return FALLING_TRACKER.getOrDefault(player.getUniqueId(), player.getFallDistance());
	}

	/**
	 * Gets the stored velocity from the last fall
	 *
	 * @param player player
	 * @return stored velocity
	 */
	public static Vector getStoredVelocity(Player player) {
		return VELOCITY_TRACKER.getOrDefault(player.getUniqueId(), player.getVelocity());
	}

	/**
	 * Gets the stored pre velocity (i.e. velocity of the prior tick)
	 *
	 * @param player player
	 * @return stored pre velocity
	 */
	public static Vector getStoredPreVelocity(Player player) {
		return PRE_VELOCITY_TRACKER.getOrDefault(player.getUniqueId(), player.getVelocity());
	}

}
