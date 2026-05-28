package me.sosedik.trappednewbie.listener.effect;

import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.trappednewbie.impl.blockstorage.FlowerPotBlockStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.listener.item.NotDroppableItems;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jspecify.annotations.NullMarked;

/**
 * Attrition limits the player
 */
@NullMarked
public class AttritionLimitations implements Listener {

	static {
		NotDroppableItems.addRule(new NotDroppableItems.NotDroppableRule(
			(entity, _) -> {
				if (!(entity instanceof Player player)) return false;
				if (!PossessingPlayer.isPossessing(player)) return false;
				if (PossessingPlayer.canDropItems(player)) return false;

				HudMessenger.of(player).displayMessage(Messenger.messenger(player).getMessage("attrition.too_low"));
				return true;
			})
			.exclude(AttritionLimitations::isAllowedInventory)
			.withAllowedCrafts()
			.withAllowedPlace()
		);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onOpen(InventoryOpenEvent event) {
		if (!(event.getPlayer() instanceof Player player)) return;
		if (!PossessingPlayer.isPossessing(player)) return;
		if (PossessingPlayer.canOpenInventories(player)) return;

		InventoryHolder holder = event.getInventory().getHolder();
		if (holder != null && isAllowedInventory(holder)) return;

		event.setCancelled(true);
		HudMessenger.of(player).displayMessage(Messenger.messenger(player).getMessage("attrition.too_low"));
	}

	private static boolean isAllowedInventory(InventoryHolder holder) {
		return holder instanceof FlowerPotBlockStorage // It has GUI, but it's meant to be of primitive kind and there's no better way yet
			|| holder instanceof AbstractHorse; // Can't open player's own inventory otherwise
	}

}
