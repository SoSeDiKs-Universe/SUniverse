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
@NullMarked
public class WaterPotionSplashesTorches implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onWaterSplash(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof ThrownPotion thrownPotion)) return;
		if (!ItemUtil.isWaterPotion(thrownPotion.getItem())) return;

		var hitBlock = event.getHitBlock();
		if (hitBlock == null) return;

		for (Block block : LocationUtil.getBlocksAround(hitBlock, 1, 1)) {
			Material type = block.getType();
			if (!MaterialTags.TORCH.isTagged(type) && !MaterialTags.SOUL_TORCH.isTagged(type)) continue;

			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation().center(), ItemStack.of(Material.STICK));
			block.emitSound(Sound.BLOCK_FIRE_EXTINGUISH, 1F, 1F);
		}
	}

}
