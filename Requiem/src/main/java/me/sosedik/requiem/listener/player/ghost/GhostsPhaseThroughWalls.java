package me.sosedik.requiem.listener.player.ghost;

import me.sosedik.kiterino.event.entity.EntityStartUsingItemEvent;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.task.GoingThroughWallsTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

}
