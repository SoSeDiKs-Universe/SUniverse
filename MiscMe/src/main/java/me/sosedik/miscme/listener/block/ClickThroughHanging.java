package me.sosedik.miscme.listener.block;

import net.kyori.adventure.util.TriState;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

/**
 * Allow opening containers behind hanging entities
 */
@NullMarked
public class ClickThroughHanging implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof Hanging hanging)) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		if (hanging instanceof ItemFrame itemFrame && itemFrame.getItem().getType() == Material.AIR
				&& player.getInventory().getItemInMainHand().getType() != Material.AIR)
			return;

		Block container = hanging.getLocation().getBlock().getRelative(hanging.getFacing().getOppositeFace());
		if (!openContainer(player, container, false).toBooleanOrElse(true)) return;

		event.setCancelled(true);
	}

	/**
	 * Opens the block's inventory for the player
	 *
	 * @param player player
	 * @param container block
	 * @param ignoreObstructions whether to open the inventory even if it's blocked (e.g. by a sitting cat on a chest)
	 * @return whether the inventory was opened
	 */
	public static TriState openContainer(Player player, Block container, boolean ignoreObstructions) {
		if (container.getState(false) instanceof Container storage) {
			player.swingMainHand();
			if (!ignoreObstructions && storage instanceof Chest chest && chest.isBlocked()) return TriState.NOT_SET;

			player.openInventory(storage.getInventory());
			if (container.getType() == Material.CHEST || container.getType() == Material.TRAPPED_CHEST) {
				player.incrementStatistic(Statistic.CHEST_OPENED);
			} else if (container.getType() == Material.BARREL) {
				player.incrementStatistic(Statistic.OPEN_BARREL);
			} else if (Tag.SHULKER_BOXES.isTagged(container.getType())) {
				player.incrementStatistic(Statistic.SHULKER_BOX_OPENED);
			}

			player.angerNearbyPiglins(true);
			return TriState.TRUE;
		} else if (container.getType() == Material.ENDER_CHEST) {
			if (!(container.getState(false) instanceof EnderChest enderChest)) return TriState.NOT_SET;

			player.swingMainHand();
			if (!ignoreObstructions && enderChest.isBlocked()) return TriState.NOT_SET;
			if (!player.openEnderChest(enderChest)) return TriState.NOT_SET;

			player.incrementStatistic(Statistic.ENDERCHEST_OPENED);
			player.angerNearbyPiglins(true);
			return TriState.TRUE;
		}

		return TriState.FALSE;
	}

}
