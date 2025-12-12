package me.sosedik.requiem.listener.item;

import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.requiem.listener.player.possessed.PossessingOverMobs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

		if (!PossessingPlayer.canPreserveInventory(player)) {
			PlayerInventory inventory = player.getInventory();
			for (int i = 0; i < inventory.getSize(); i++) {
				ItemStack item = inventory.getItem(i);
				if (ItemStack.isEmpty(item)) continue;
				if (PossessingOverMobs.isPossessedSoulboundItem(item)) continue;

				inventory.setItem(i, null);
			}
		}
		PossessingPlayer.stopPossessing(player);
		GhostyPlayer.markGhost(player);
	}

}
