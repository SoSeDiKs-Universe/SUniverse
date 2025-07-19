package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Tag;
import org.bukkit.World;
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
		World world = block.getWorld();
		if (Math.abs(block.getX()) == 0 && Math.abs(block.getZ()) == 0) {
			TrappedNewbieAdvancements.SPAWN_CAMPING.awardAllCriteria(player);
			return;
		}
		if (Math.abs(block.getX()) >= 29_999_980 && Math.abs(block.getZ()) >= 29_999_980) {
			TrappedNewbieAdvancements.CORNER_CAMPING.awardAllCriteria(player);
			return;
		}
		if (world.getEnvironment() == World.Environment.THE_END) {
			if (block.getX() == 1_894_255 && block.getY() == 14 && block.getZ() == -804_312)
				TrappedNewbieAdvancements.NOT_SPAWN_CAMPING.awardAllCriteria(player);
		}
	}

}
