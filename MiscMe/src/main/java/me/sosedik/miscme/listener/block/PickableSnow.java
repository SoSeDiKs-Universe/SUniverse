package me.sosedik.miscme.listener.block;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Snow can be picked into snowball
 */
@NullMarked
public class PickableSnow implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onSnowClick(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getItem() != null) return;

		Player player = event.getPlayer();
		if (player.getInventory().getItemInOffHand().getType() == Material.SNOWBALL) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		if (block.getType() == Material.SNOW && block.getBlockData() instanceof Snow snow) {
			event.setCancelled(true);
			player.getInventory().setItemInMainHand(ItemStack.of(Material.SNOWBALL));
			block.emitSound(Sound.BLOCK_SNOW_HIT, 1F, 1F);
			if (snow.getLayers() == snow.getMinimumLayers()) {
				block.setType(Material.AIR);
			} else {
				snow.setLayers(snow.getLayers() - 1);
				block.setBlockData(snow);
			}
		} else if (block.getType() == Material.SNOW_BLOCK) {
			event.setCancelled(true);
			player.getInventory().setItemInMainHand(ItemStack.of(Material.SNOWBALL));
			block.setType(Material.SNOW);
			block.emitSound(Sound.BLOCK_SNOW_HIT, 1F, 1F);
			if (block.getBlockData() instanceof Snow snow) {
				snow.setLayers(snow.getMaximumLayers() - 1);
				block.setBlockData(snow);
			}
		}
	}

}
