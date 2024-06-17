package me.sosedik.trappednewbie.listener.player;

import me.sosedik.moves.listener.movement.FreeFall;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The very first server join behaviour
 */
public class FirstWorldJoin implements Listener {

	private static final int RPT_RADIUS = 5_000;

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(@NotNull PlayerJoinEvent event) {
		Player player = event.getPlayer();
		GhostyPlayer.markGhost(player);
		World world = PerPlayerWorlds.getResourceWorld(player.getUniqueId(), World.Environment.NORMAL);
		LocationUtil.runRtp(player, world, RPT_RADIUS).thenRun(() -> FreeFall.setLeaping(player, true));
	}

	@EventHandler(ignoreCancelled = true)
	public void onMove(@NotNull PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedBlock()) return;

		Player player = event.getPlayer();
		Location to = event.getTo();
		World playerWorld = PerPlayerWorlds.getPersonalWorld(player.getUniqueId());
		if (to.getWorld() != playerWorld) return;

		Chunk chunk = to.getChunk();
		if (chunk.getX() == 0 && chunk.getZ() == 0) return;

		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onMoveC(@NotNull PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedBlock()) return;

		Player player = event.getPlayer();
		Location to = event.getTo();
		World playerWorld = PerPlayerWorlds.getPersonalWorld(player.getUniqueId());
		if (to.getWorld() != playerWorld) return;

		Chunk chunk = to.getChunk();
		if (chunk.getX() != 0 || chunk.getZ() != 0) return;
		if (to.getBlockX() != 0) return;
		if (to.getBlockZ() != 0) return;
		if (to.getBlockY() != 100) return;

		player.teleportAsync(new Location(Bukkit.getWorlds().getFirst(), 0, 120, 0));
	}

}
