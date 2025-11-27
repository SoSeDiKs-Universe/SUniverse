package me.sosedik.trappednewbie.listener.item;

import me.sosedik.kiterino.event.entity.EntityItemConsumeEvent;
import me.sosedik.kiterino.event.entity.ItemConsumeEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Visuals for eyece cream candy
 */
@NullMarked
public class EyeceCreamCandyVisual implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onUse(PlayerItemConsumeEvent event) {
		tryToUse(event);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onUse(EntityItemConsumeEvent event) {
		tryToUse(event);
	}

	public void tryToUse(ItemConsumeEvent event) {
		ItemStack item = event.getItem();
		if (item.getType() != TrappedNewbieItems.EYECE_CREAM_CANDY) return;
		if (!(event.getEntity() instanceof Player player)) return;

		player.showElderGuardian();
	}

}
