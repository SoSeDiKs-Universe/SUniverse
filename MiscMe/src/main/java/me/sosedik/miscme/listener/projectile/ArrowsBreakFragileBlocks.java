package me.sosedik.miscme.listener.projectile;

import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

/**
 * Arrows break glass
 */
@NullMarked
public class ArrowsBreakFragileBlocks implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onProjectileHit(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof AbstractArrow arrow)) return; // Also covers Tridents

		Block block = event.getHitBlock();
		if (block == null) return;
		if (!UtilizerTags.FRAGILE_BLOCKS.isTagged(block.getType())) return;

		Vector velocity = arrow.getVelocity();
		if (velocity.lengthSquared() < 0.4) return;

		event.setCancelled(true);
		block.breakNaturally(arrow.getItemStack(), true, true);

		Vector newVelocity = velocity.multiply(0.85);
		// Fixup arrow thinking it's stuck in a block
		MiscMe.scheduler().sync(() -> {
			arrow.startFalling();
			arrow.setVelocity(newVelocity);
		}, 1L);
	}

}
