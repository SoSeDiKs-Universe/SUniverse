package me.sosedik.trappednewbie.listener.misc;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Dynamically toggle reduced debug info
 */
// MCCheck: 1.21.10, packet changes
@NullMarked
public class DynamicReducedF3DebugInfo implements Listener {

	private static final Set<UUID> DEBUGGERS = new HashSet<>();
	private static final int ENABLE_REDUCED_DEBUG_INFO = 22;
	private static final int DISABLE_REDUCED_DEBUG_INFO = 23;

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (shouldEnableF3(player))
			disableReducedDebugInfo(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onRespawn(PlayerPostRespawnEvent event) {
		Player player = event.getPlayer();
		if (shouldEnableF3(player))
			disableReducedDebugInfo(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (shouldEnableF3(player))
			disableReducedDebugInfo(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onGameModeChange(PlayerGameModeChangeEvent event) {
		Player player = event.getPlayer();
		if (shouldEnableF3(player))
			disableReducedDebugInfo(player);
		else
			enableReducedDebugInfo(player);
	}

	private boolean shouldEnableF3(Player player) {
		return isImmuneToReducedDebugInfo(player) || DynamicInventoryInfoGatherer.getInventoryData(player).shouldDisableReducedDebugInfo();
	}

	/**
	 * Enabled reduced debug info (hidden F3)
	 *
	 * @param player player
	 */
	public static void enableReducedDebugInfo(Player player) {
		if (!DEBUGGERS.add(player.getUniqueId())) return;
		if (isImmuneToReducedDebugInfo(player)) return;

		var packet = new WrapperPlayServerEntityStatus(player.getEntityId(), ENABLE_REDUCED_DEBUG_INFO);
		PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
	}

	private static boolean isImmuneToReducedDebugInfo(Player player) {
		return player.isOp() || player.getGameMode().isInvulnerable();
	}

	/**
	 * Disables reduced debug info (no hidden F3)
	 *
	 * @param player player
	 */
	public static void disableReducedDebugInfo(Player player) {
		if (!DEBUGGERS.remove(player.getUniqueId())) return;

		var packet = new WrapperPlayServerEntityStatus(player.getEntityId(), DISABLE_REDUCED_DEBUG_INFO);
		PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
	}

}
