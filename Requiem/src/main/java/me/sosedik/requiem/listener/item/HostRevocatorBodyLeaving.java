package me.sosedik.requiem.listener.item;

import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Stopping possessment with host revocator
 */
public class HostRevocatorBodyLeaving implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onUse(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!ItemStack.isType(event.getBow(), RequiemItems.HOST_REVOCATOR)) return;
		if (event.getForce() < 1F) return;

		event.setCancelled(true);

		PossessingPlayer.stopPossessing(player);
		player.setLevel(0);
		player.setExp(0);
		player.getInventory().clear();
		GhostyPlayer.markGhost(player);
	}

}
