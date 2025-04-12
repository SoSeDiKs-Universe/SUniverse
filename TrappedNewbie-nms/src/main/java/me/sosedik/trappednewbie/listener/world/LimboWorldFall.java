package me.sosedik.trappednewbie.listener.world;

import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.moves.listener.movement.FreeFall;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieFonts;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.LocationUtil;
import net.kyori.adventure.text.Component;
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
import org.jspecify.annotations.NullMarked;

/**
 * Teleport to resource world when falling in limbo world
 */
@NullMarked
public class LimboWorldFall implements Listener {

	/**
	 * Vanilla's soft limit is ~30 mil
	 */
	private static final int RPT_RADIUS = 15_000_000;

	@EventHandler(priority = EventPriority.LOWEST)
	public void onFall(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (player.getWorld() != TrappedNewbie.limboWorld()) return;
		if (event.getDamageSource().getDamageType() != DamageType.OUT_OF_WORLD) return;

		event.setCancelled(true);
		if (TrappedNewbieAdvancements.BRAVE_NEW_WORLD.hasCriteria(player, "friendship")) {
			TrappedNewbieAdvancements.BRAVE_NEW_WORLD.awardCriteria(player, "fall");
			World world = PerPlayerWorlds.getResourceWorld(player.getUniqueId(), World.Environment.NORMAL);
			runRtp(player, world);
		} else {
			player.teleportAsync(TrappedNewbie.limboWorld().getSpawnLocation());
			player.sendMessage(Mini.combine(Component.space(), TrappedNewbieFonts.WANDERING_TRADER_HEAD.mapping(), Messenger.messenger(player).getMessage("limbo.welcome.ignored")));
		}
	}

	/**
	 * Teleports the player to a random location in the world
	 *
	 * @param player player
	 * @param world world
	 */
	public static void runRtp(Player player, World world) {
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
