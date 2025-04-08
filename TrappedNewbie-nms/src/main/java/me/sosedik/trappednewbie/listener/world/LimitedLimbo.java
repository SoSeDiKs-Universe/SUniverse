package me.sosedik.trappednewbie.listener.world;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Limits interactions in Limbo world
 */
public class LimitedLimbo implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		if (shouldDeny(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBreak(BlockBreakEvent event) {
		if (shouldDeny(event.getPlayer()))
			event.setCancelled(true);
	}

	private boolean shouldDeny(Player player) {
		return !player.isOp() && player.getWorld() == Bukkit.getWorlds().getFirst();
	}

}
