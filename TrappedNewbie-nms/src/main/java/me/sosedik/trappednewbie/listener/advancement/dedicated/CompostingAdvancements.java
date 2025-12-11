package me.sosedik.trappednewbie.listener.advancement.dedicated;

import io.papermc.paper.event.entity.EntityCompostItemEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Advancements for composting
 */
@NullMarked
public class CompostingAdvancements implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCompost(EntityCompostItemEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		ItemStack item = event.getItem();
		if (item.getType() == Material.TALL_GRASS)
			TrappedNewbieAdvancements.COMPOST_A_STACK_OF_TALL_GRASS.awardNextCriterion(player);
		else if (item.getType() == Material.LARGE_FERN)
			TrappedNewbieAdvancements.COMPOST_A_STACK_OF_LARGE_FERNS.awardNextCriterion(player);
	}

}
