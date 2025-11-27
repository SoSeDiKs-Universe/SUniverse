package me.sosedik.miscme.listener.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Swap half of the current item while sneaking
 */
@NullMarked
public class SwapHalfStack implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSwap(PlayerSwapHandItemsEvent event) {
		if (!event.getPlayer().isSneaking()) return;

		ItemStack mainHandItem = event.getMainHandItem();
		ItemStack offHandItem = event.getOffHandItem();

		// From main hand to empty off-hand
		if (mainHandItem.isEmpty() && !offHandItem.isEmpty()) {
			int amount = offHandItem.getAmount();
			if (amount <= 1) return;
			boolean extraOne = amount % 2 == 1;
			amount /= 2;
			event.setMainHandItem(offHandItem.asQuantity(extraOne ? amount + 1 : amount));
			event.setOffHandItem(offHandItem.asQuantity(amount));
			return;
		}

		// From off-hand to empty main hand
		if (offHandItem.isEmpty() && !mainHandItem.isEmpty()) {
			int amount = mainHandItem.getAmount();
			if (amount <= 1) return;
			boolean extraOne = amount % 2 == 1;
			amount /= 2;
			event.setOffHandItem(mainHandItem.asQuantity(extraOne ? amount + 1 : amount));
			event.setMainHandItem(mainHandItem.asQuantity(amount));
			return;
		}

		if (mainHandItem.isEmpty()) return;
		if (offHandItem.isEmpty()) return;

		// Swapping while holding similar items in both hands
		if (mainHandItem.isSimilar(offHandItem)) {
			int mainAmount = mainHandItem.getAmount();
			int maxAmount = mainHandItem.getMaxStackSize();
			if (mainAmount >= maxAmount) return;

			int offHandAmount = offHandItem.getAmount();
			int newAmount = offHandAmount + mainAmount;
			if (newAmount > maxAmount) {
				offHandAmount = newAmount - maxAmount;
				event.setMainHandItem(mainHandItem.asQuantity(maxAmount));
				event.setOffHandItem(mainHandItem.asQuantity(offHandAmount));
			} else {
				event.setMainHandItem(mainHandItem.asQuantity(newAmount));
				event.setOffHandItem(ItemStack.empty());
			}
		}
	}

}
