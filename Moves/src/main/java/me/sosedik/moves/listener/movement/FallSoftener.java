package me.sosedik.moves.listener.movement;

import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Less damage from soft blocks
 */
public class FallSoftener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFall(EntityDamageEvent event) {
		if (event.getDamageSource().getDamageType() != DamageType.FALL) return;

		Block block = event.getEntity().getSupportingBlock();
		if (block == null) return;

		Material blockType = block.getType();
		if (blockType == Material.HAY_BLOCK) {
			if (event.getEntity().getFallDistance() > 8) {
				event.setDamage(event.getFinalDamage() / 2.5);
			} else {
				event.setDamage(0);
			}
		} else if (blockType == Material.SNOW_BLOCK
			|| blockType == Material.POWDER_SNOW
			|| blockType == Material.SNOW
			|| Tag.WOOL.isTagged(blockType)
			|| Tag.BEDS.isTagged(blockType)
			|| Tag.LEAVES.isTagged(blockType)
		) {
			event.setDamage(event.getFinalDamage() / 2);
		} else if (UtilizerTags.FRAGILE_BLOCKS.isTagged(blockType)) {
			event.setDamage(event.getFinalDamage() / 1.25);
			if (event.getEntity().getFallDistance() > 12 || (Math.random() > 0.4 && event.getFinalDamage() >= 2)) {
				block.breakNaturally(true, true);
			}
		}

		if (event.getFinalDamage() <= 0)
			event.setCancelled(true);
	}

}
