package me.sosedik.requiem.listener.player.ghost;

import me.sosedik.kiterino.event.entity.EntityStartUsingItemEvent;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.task.GoingThroughWallsTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Ghosts can phase through walls
 */
@NullMarked
public class GhostsPhaseThroughWalls implements Listener {
	
	@EventHandler
	public void onUse(EntityStartUsingItemEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!GhostyPlayer.isGhost(player)) return;

		ItemStack item = event.getItem();
		if (item.getType() != RequiemItems.GHOST_MOTIVATOR) return;

		Requiem.scheduler().sync(() -> {
			if (item.isSimilar(player.getActiveItem()))
				new GoingThroughWallsTask(player, item);
		}, 1L);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBow(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!ItemStack.isType(event.getBow(), RequiemItems.GHOST_RELOCATOR)) return;
		if (event.getForce() < 1F) return;
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(true);

		player.teleportAsync(player.getWorld().getSpawnLocation().center(1));
	}

}
