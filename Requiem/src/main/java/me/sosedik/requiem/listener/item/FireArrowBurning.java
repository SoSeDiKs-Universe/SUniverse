package me.sosedik.requiem.listener.item;

import me.sosedik.requiem.dataset.RequiemItems;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Fire arrows are burning
 */
@NullMarked
public class FireArrowBurning implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onShoot(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof AbstractArrow arrow)) return;
		if (arrow instanceof Trident) return;

		ItemStack arrowItem = arrow.getItemStack();
		if (arrowItem.getType() != RequiemItems.FIRE_ARROW) return;

		arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
		arrow.setFireTicks(Integer.MAX_VALUE);
	}

}
