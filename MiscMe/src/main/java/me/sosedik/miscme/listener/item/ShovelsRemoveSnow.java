package me.sosedik.miscme.listener.item;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Snow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Shovels can compress snow
 */
@NullMarked
public class ShovelsRemoveSnow implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		ItemStack item = event.getItem();
		if (item == null) return;
		if (!Tag.ITEMS_SHOVELS.isTagged(item.getType())) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		if (block.getType() == Material.SNOW && block.getBlockData() instanceof Snow snow) {
			event.setCancelled(true);
			event.getPlayer().swingMainHand();
			block.emitSound(Sound.BLOCK_SNOW_BREAK, 1F, 1F);
			block.getWorld().dropItemNaturally(block.getLocation().center(), new ItemStack(Material.SNOWBALL));
			if (snow.getLayers() == snow.getMinimumLayers()) {
				block.setType(Material.AIR);
			} else {
				snow.setLayers(snow.getLayers() - 1);
				block.setBlockData(snow);
			}
		} else if (block.getType() == Material.SNOW_BLOCK) {
			event.setCancelled(true);
			event.getPlayer().swingMainHand();
			block.setType(Material.SNOW);
			block.emitSound(Sound.BLOCK_SNOW_BREAK, 1F, 1F);
			block.getWorld().dropItemNaturally(block.getLocation().center(), new ItemStack(Material.SNOWBALL));
			if (block.getBlockData() instanceof Snow snow) {
				snow.setLayers(snow.getMaximumLayers() - 1);
				block.setBlockData(snow);
			}
		}
	}

}
