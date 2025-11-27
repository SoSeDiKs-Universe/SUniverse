package me.sosedik.miscme.listener.block;

import me.sosedik.miscme.listener.projectile.BurningProjectileCreatesFire;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Campfires affected by gravity spread fire if lit
 */
@NullMarked
public class FallenCampfireSpreadsFire implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFall(EntityChangeBlockEvent event) {
		if (!Tag.CAMPFIRES.isTagged(event.getTo())) return;
		if (!(event.getEntity() instanceof FallingBlock fallingBlock)) return;
		if (!(event.getBlockData() instanceof Lightable lightable)) return;
		if (!lightable.isLit()) return;

		event.setCancelled(true);
		lightable.setLit(false);

		Block block = event.getBlock();

		BurningProjectileCreatesFire.createFireOrIgnite(block.getRelative(BlockFace.DOWN), BlockFace.UP, fallingBlock, BlockIgniteEvent.IgniteCause.SPREAD);

		List<BlockFace> blockFaces = new ArrayList<>(LocationUtil.SURROUNDING_BLOCKS);
		Collections.shuffle(blockFaces);
		double chance = 1;
		for (BlockFace blockFace : blockFaces) {
			Block relative = block.getRelative(blockFace);
			if (Math.random() > chance) continue;
			if (!BurningProjectileCreatesFire.createFireOrIgnite(relative, blockFace.isCartesian() ? blockFace : BlockFace.UP, fallingBlock, BlockIgniteEvent.IgniteCause.SPREAD)) continue;

			chance -= 0.3;
			if (chance <= 0)
				break;
		}

		block.setBlockData(lightable);
	}

}
