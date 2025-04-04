package me.sosedik.miscme.listener.projectile;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Fire;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Burning arrows create fire upon landing
 */
@NullMarked
// MCCheck 1.21.4, new burnable blocks
public class BurningArrowCreatesFire implements Listener {

	private static final List<Material> burnableBlocks = List.of(Material.SHORT_GRASS, Material.TALL_GRASS, Material.FERN, Material.LARGE_FERN);

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFireArrowLand(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof AbstractArrow)) return;
		if (event.getEntity().getFireTicks() <= 0) return;

		var hitBlock = event.getHitBlock();
		var hitBlockFace = event.getHitBlockFace();
		if (hitBlock == null || hitBlockFace == null) return;

		createFire(hitBlock, hitBlockFace);
	}

	/**
	 * Creates fire at location
	 *
	 * @param hitBlock hit block
	 * @param hitBlockFace git block face
	 */
	public static void createFire(Block hitBlock, BlockFace hitBlockFace) {
		if (!hitBlock.getType().isBurnable() && !hitBlock.getRelative(hitBlockFace).getRelative(BlockFace.DOWN).getType().isSolid())
			return;

		hitBlock = hitBlock.getRelative(hitBlockFace);
		if (!(hitBlock.getType().isEmpty() || Tag.CORAL_PLANTS.isTagged(hitBlock.getType()) || burnableBlocks.contains(hitBlock.getType()))) return;

		hitBlock.setType(Material.FIRE);
		if (hitBlockFace == BlockFace.UP) return;
		if (!(hitBlock.getBlockData() instanceof Fire fire)) return;

		for (BlockFace face : fire.getAllowedFaces()) {
			if (hitBlock.getRelative(face).getType().isBurnable())
				fire.setFace(face, true);
		}
		hitBlock.setBlockData(fire);
	}

}
