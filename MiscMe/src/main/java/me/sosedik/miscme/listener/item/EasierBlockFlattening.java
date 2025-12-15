package me.sosedik.miscme.listener.item;

import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
 * Create paths/farmland below replaceable blocks
 */
@NullMarked
public class EasierBlockFlattening implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPathing(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY) return;
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
		if (tryToFlatten(player, block, EquipmentSlot.HAND) || tryToFlatten(player, block, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean shouldBeBroken(Block block) {
		if (block.getType() == Material.SNOW)
			return block.getBlockData() instanceof Snow snow && snow.getLayers() == snow.getMinimumLayers();
		if (block.isReplaceable())
			return true;
		return Tag.FLOWERS.isTagged(block.getType());
	}

	private boolean tryToFlatten(Player player, Block block, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (player.hasCooldown(item)) return false;

		if (Tag.ITEMS_SHOVELS.isTagged(item.getType()))
			return tryToFlatten(player, block, hand, Material.DIRT_PATH, Sound.ITEM_SHOVEL_FLATTEN);

		if (Tag.ITEMS_HOES.isTagged(item.getType()))
			return tryToFlatten(player, block, hand, Material.FARMLAND, Sound.ITEM_HOE_TILL);

		return false;
	}

	private boolean tryToFlatten(Player player, Block block, EquipmentSlot hand, Material blockType, Sound sound) {
		Material materialPre = block.getType();
		if (!new EntityChangeBlockEvent(player, block, blockType.createBlockData()).callEvent()) return false;

		player.swingHand(hand);
		Block upperBlock = block.getRelative(BlockFace.UP);
		if (!upperBlock.isEmpty()) {
			boolean wasSneaking = player.isSneaking();
			// Workaround to prevent extra behaviors from grass break
			// as sneaking usually stands for skipping a special action
			// Notably, prevents Sweeping enchantment from triggering
			player.setSneaking(true);
			player.breakBlock(upperBlock);
			player.setSneaking(wasSneaking);
		}
		if (materialPre == block.getType()) block.setType(blockType);
		block.emitSound(sound, 1F, 1F);
		player.getInventory().getItem(hand).damage(1, player);

		return true;
	}

}
