package me.sosedik.trappednewbie.listener.block;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Farmland turns into dirt if the crop is broken without a hoe
 */
@NullMarked
public class ProperCropBreaking implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		if (!Tag.CROPS.isTagged(block.getType())) return;

		block = block.getRelative(BlockFace.DOWN);
		if (block.getType() != Material.FARMLAND) return;

		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();

		if (Tag.ITEMS_HOES.isTagged(item.getType())) return;
		if (isCropBreakableByHand(block)) return;

		block.setType(Material.DIRT);
	}

	private static boolean isCropBreakableByHand(Block block) {
		return block.getBlockData() instanceof Ageable ageable && ageable.getAge() == 0;
	}

}
