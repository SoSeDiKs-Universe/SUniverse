package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Advancements for placing a campfire
 */
public class CampfirePlacingAdvancements implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		if (!Tag.CAMPFIRES.isTagged(block.getType())) return;

		grant(event.getPlayer(), block);
	}

	/**
	 * Grants advancements for placing a campfire
	 */
	public static void grant(Player player, Block block) {
		TrappedNewbieAdvancements.CAMPING_OUT.awardAllCriteria(player);
		if (Bukkit.getWorlds().getFirst().equals(block.getWorld()) && Math.abs(block.getX()) <= 10 && Math.abs(block.getZ()) <= 10)
			TrappedNewbieAdvancements.SPAWN_CAMPING.awardAllCriteria(player);
	}

}
