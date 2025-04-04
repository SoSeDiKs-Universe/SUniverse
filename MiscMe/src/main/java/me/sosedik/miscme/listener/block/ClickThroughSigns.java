package me.sosedik.miscme.listener.block;

import com.destroystokyo.paper.MaterialTags;
import me.sosedik.miscme.listener.item.ImmersiveDyes;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Allow opening containers behind signs
 */
public class ClickThroughSigns implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onSignClick(@NotNull PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
		if (!(block.getBlockData() instanceof WallSign wallSign)) return;
		if (!(block.getState(false) instanceof Sign sign)) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		ItemStack handItem = player.getInventory().getItemInMainHand();
		if (handItem.getType() == Material.AIR) {
			if (!sign.isWaxed()) return;
		} else if (MaterialTags.DYES.isTagged(handItem.getType())) {
			DyeColor dyeColor = ImmersiveDyes.getDyeColor(handItem);
			if (dyeColor != sign.getTargetSide(player).getColor()) return;
		} else if (handItem.getType() == Material.GLOW_INK_SAC) {
			if (!sign.getTargetSide(player).isGlowingText()) return;
		} else if (handItem.getType() == Material.HONEYCOMB) {
			if (!sign.isWaxed()) return;
		}

		Block container = event.getClickedBlock().getRelative(wallSign.getFacing().getOppositeFace());
		if (!ClickThroughHanging.openContainer(player, container, false).toBooleanOrElse(true)) return;

		event.setCancelled(true);
	}

}
