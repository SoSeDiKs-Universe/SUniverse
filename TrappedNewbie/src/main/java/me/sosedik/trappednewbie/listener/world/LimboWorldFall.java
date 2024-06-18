package me.sosedik.trappednewbie.listener.world;

import me.sosedik.moves.listener.movement.FreeFall;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
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

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFall(@NotNull EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (player.getWorld() != LIMBO_WORLD) return;
		if (event.getDamageSource().getDamageType() != DamageType.OUT_OF_WORLD) return;

		event.setCancelled(true);
		GhostyPlayer.markGhost(player);
		World world = PerPlayerWorlds.getResourceWorld(player.getUniqueId(), World.Environment.NORMAL);
		LocationUtil.runRtp(player, world, RPT_RADIUS)
			.thenRun(() -> FreeFall.setLeaping(player, true));
	}

}
