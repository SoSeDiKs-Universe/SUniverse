package me.sosedik.miscme.listener.block;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Breaking replaceable blocks when placing blocks in their place
 */
@NullMarked
public class BreakReplaceables implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlace(BlockCanBuildEvent event) {
		if (!event.isBuildable()) return;

		Player player = event.getPlayer();
		if (player == null) return;

		Block block = event.getBlock();
		if (!block.getBlockData().isReplaceable()) return;

		Material type = block.getType();
		if (type.isAir()) return;
		if (type == Material.WATER || type == Material.LAVA) return;
		if (type == event.getBlockData().getMaterial()) return;

		player.breakBlock(block);
	}

}
