package me.sosedik.miscme.listener.block;

import com.destroystokyo.paper.MaterialTags;
import me.sosedik.miscme.listener.item.ImmersiveDyes;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Allow opening containers behind signs
 */
@NullMarked
public class ClickThroughSigns implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onSignClick(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		BlockData blockData = block.getBlockData();
		if (!(blockData instanceof Directional) && !(blockData instanceof Rotatable)) return;
		if (!(block.getState(false) instanceof Sign sign)) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		ItemStack handItem = player.getInventory().getItemInMainHand();
		if (handItem.isEmpty()) {
			if (!sign.isWaxed()) return;
		} else if (MaterialTags.DYES.isTagged(handItem.getType())) {
			DyeColor dyeColor = ImmersiveDyes.getDyeColor(handItem);
			if (dyeColor != sign.getTargetSide(player).getColor()) return;
		} else if (handItem.getType() == Material.GLOW_INK_SAC) {
			if (!sign.getTargetSide(player).isGlowingText()) return;
		} else if (handItem.getType() == Material.HONEYCOMB) {
			if (!sign.isWaxed()) return;
		}

		BlockFace blockFace
			= blockData instanceof WallSign wallSign
			? wallSign.getFacing().getOppositeFace()
			: event.getBlockFace().getOppositeFace();
		Block container = event.getClickedBlock().getRelative(blockFace);
		if (!ClickThroughHanging.openContainer(player, container, blockFace, false).toBooleanOrElse(true)
			&& player.getOpenInventory().getTopInventory().getType() == InventoryType.CRAFTING) return;

		event.setCancelled(true);
	}

}
