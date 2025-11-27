package me.sosedik.miscme.listener.fixup;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Fix hand swing when igniting cobwebs
 */
@NullMarked
public class IgnitableCobweb implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCobwebIgnite(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
		if (block.getType() != Material.COBWEB) return;

		BlockFace face = event.getBlockFace();
		if (face != BlockFace.UP && face != BlockFace.DOWN) {
			Block relative = block.getRelative(face);
			if (relative.getType() == Material.FIRE) return;
			if (!relative.getRelative(BlockFace.DOWN).isEmpty()) return;
		}

		Player player = event.getPlayer();
		if (tryToIgnite(player, EquipmentSlot.HAND) || tryToIgnite(player, EquipmentSlot.OFF_HAND))
			block.emitSound(Sound.ITEM_FLINTANDSTEEL_USE, 1F, (float) Math.random() * 0.4F + 0.8F);
	}

	private boolean tryToIgnite(Player player, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (item.getType() != Material.FLINT_AND_STEEL && item.getType() != Material.FIRE_CHARGE) return false;

		player.swingHand(hand);
		return true;
	}

}
