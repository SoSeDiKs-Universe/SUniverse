package me.sosedik.miscme.listener.item;

import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Create paths below replaceable blocks
 */
@NullMarked
public class EasierShovelPathCreation implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPathing(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
		if (!UtilizerTags.FLATTENABLES.isTagged(block.getType())) {
			block = block.getRelative(BlockFace.DOWN);
			if (!UtilizerTags.FLATTENABLES.isTagged(block.getType()))
				return;
		}
		if (!shouldBeBroken(block.getRelative(BlockFace.UP))) return;

		Player player = event.getPlayer();
		if (createPath(player, block, EquipmentSlot.HAND) || createPath(player, block, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean shouldBeBroken(Block block) {
		if (block.getType() == Material.SNOW) return false;
		if (block.isReplaceable()) return true;
		return Tag.FLOWERS.isTagged(block.getType());
	}

	private boolean createPath(Player player, Block block, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (!Tag.ITEMS_SHOVELS.isTagged(item.getType())) return false;
		if (player.hasCooldown(item)) return false;

		Material materialPre = block.getType();
		if (!new EntityChangeBlockEvent(player, block, Material.DIRT_PATH.createBlockData()).callEvent()) return false;

		player.swingHand(hand);
		if (materialPre == block.getType()) block.setType(Material.DIRT_PATH);
		block.emitSound(Sound.ITEM_SHOVEL_FLATTEN, 1F, 1F);
		item.damage(1, player);

		return true;
	}

}
