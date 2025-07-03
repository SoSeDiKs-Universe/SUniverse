package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Advancement for path creation
 */
@NullMarked
public class PathwaysAdvancement implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPathing(EntityChangeBlockEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getTo() != Material.DIRT_PATH) return;

		TrappedNewbieAdvancements.PATHWAYS.awardAllCriteria(player);
	}

}
