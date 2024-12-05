package me.sosedik.trappednewbie.listener.world;

import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.moves.listener.movement.FreeFall;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Teleport to resource world when falling in limbo world
 */
public class LimboWorldFall implements Listener {

	/**
	 * Vanilla's soft limit is ~30 mil
	 */
	private static final int RPT_RADIUS = 15_000_000;
	private static final World LIMBO_WORLD = Bukkit.getWorlds().getFirst();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFall(@NotNull EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (player.getWorld() != LIMBO_WORLD) return;
		if (event.getDamageSource().getDamageType() != DamageType.OUT_OF_WORLD) return;

		event.setCancelled(true);
		GhostyPlayer.markGhost(player);
		World world = PerPlayerWorlds.getResourceWorld(player.getUniqueId(), World.Environment.NORMAL);
		runRtp(player, world);
	}

	/**
	 * Teleports the player to a random location in the world
	 *
	 * @param player player
	 * @param world world
	 */
	public static void runRtp(@NotNull Player player, @NotNull World world) {
		LocationUtil.runRtp(player, world, RPT_RADIUS)
			.thenRun(() -> {
				Entity vehicle = player.getVehicle();
				if (vehicle == null) {
					FreeFall.startLeaping(player);
				} else {
					vehicle.setFallDistance(0F);
					player.setFallDistance(0F);
					Location loc = player.getLocation().toHighestLocation().above();
					player.teleportAsync(loc, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.Relative.VELOCITY_ROTATION, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS);
				}
			});
	}

}
