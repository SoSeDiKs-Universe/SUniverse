package me.sosedik.miscme.listener.projectile;

import com.destroystokyo.paper.MaterialTags;
import me.sosedik.utilizer.util.ItemUtil;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Throwable water potions splash torches
 */
// MCCheck: 1.21.10, new torches
@NullMarked
public class WaterPotionSplashesTorches implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onWaterSplash(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof ThrownPotion thrownPotion)) return;
		if (!ItemUtil.isWaterPotion(thrownPotion.getItem())) return;

		var hitBlock = event.getHitBlock();
		if (hitBlock == null) return;

		for (Block block : LocationUtil.getBlocksAround(hitBlock, 1, 1)) {
			if (!isSplashableTorch(block.getType())) continue;

			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation().center(), ItemStack.of(Material.STICK));
			block.emitSound(Sound.BLOCK_FIRE_EXTINGUISH, 1F, 1F);
		}
	}

	private boolean isSplashableTorch(Material type) {
		return MaterialTags.TORCH.isTagged(type)
			|| MaterialTags.SOUL_TORCH.isTagged(type)
			|| MaterialTags.COPPER_TORCH.isTagged(type);
	}

}
