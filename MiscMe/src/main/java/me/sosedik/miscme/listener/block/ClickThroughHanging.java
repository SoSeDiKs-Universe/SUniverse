package me.sosedik.miscme.listener.block;

import me.sosedik.utilizer.api.storage.block.InventoryBlockDataStorageHolder;
import me.sosedik.utilizer.listener.BlockStorage;
import net.kyori.adventure.util.TriState;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.jspecify.annotations.NullMarked;

/**
 * Allow opening containers behind hanging entities
 */
// MCCheck: 1.21.10: new containers
@NullMarked
public class ClickThroughHanging implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof Hanging hanging)) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		if (hanging instanceof ItemFrame itemFrame && itemFrame.getItem().isEmpty()
			&& !player.getInventory().getItemInMainHand().isEmpty())
			return;

		BlockFace blockFace = hanging.getFacing().getOppositeFace();
		Block container = hanging.getLocation().getBlock().getRelative(blockFace);
		if (!openContainer(player, container, blockFace, false).toBooleanOrElse(true)
			&& player.getOpenInventory().getTopInventory().getType() == InventoryType.CRAFTING) return;

		event.setCancelled(true);
	}

	/**
	 * Opens the block's inventory for the player
	 *
	 * @param player player
	 * @param container block
	 * @param blockFace interacted block face
	 * @param ignoreObstructions whether to open the inventory even if it's blocked (e.g. by a sitting cat on a chest)
	 * @return whether the inventory was opened
	 */
	public static TriState openContainer(Player player, Block container, BlockFace blockFace, boolean ignoreObstructions) {
		if (container.getState(false) instanceof Container storage) {
			if (!canInteractWithContainer(player, container, blockFace)) return TriState.FALSE;

			player.swingMainHand();
			if (!ignoreObstructions && storage instanceof Chest chest && chest.isBlocked()) return TriState.NOT_SET;

			Inventory inventory = storage.getInventory();
			player.openInventory(inventory);
			if (!player.getOpenInventory().getTopInventory().equals(inventory))
				return TriState.FALSE;

			if (container.getType() == Material.CHEST) {
				player.angerNearbyPiglins(true);
				player.incrementStatistic(Statistic.CHEST_OPENED);
			} else if (container.getType() == Material.TRAPPED_CHEST) {
				player.angerNearbyPiglins(true);
				player.incrementStatistic(Statistic.TRAPPED_CHEST_TRIGGERED);
			} else if (container.getType() == Material.BARREL) {
				player.angerNearbyPiglins(true);
				player.incrementStatistic(Statistic.OPEN_BARREL);
			} else if (Tag.SHULKER_BOXES.isTagged(container.getType())) {
				player.angerNearbyPiglins(true);
				player.incrementStatistic(Statistic.SHULKER_BOX_OPENED);
			} else if (container.getType() == Material.FURNACE) {
				player.incrementStatistic(Statistic.FURNACE_INTERACTION);
			} else if (container.getType() == Material.BLAST_FURNACE) {
				player.incrementStatistic(Statistic.INTERACT_WITH_BLAST_FURNACE);
			} else if (container.getType() == Material.SMOKER) {
				player.incrementStatistic(Statistic.INTERACT_WITH_SMOKER);
			} else if (container.getType() == Material.BREWING_STAND) {
				player.incrementStatistic(Statistic.BREWINGSTAND_INTERACTION);
			} else if (container.getType() == Material.DISPENSER) {
				player.incrementStatistic(Statistic.DISPENSER_INSPECTED);
			} else if (container.getType() == Material.DROPPER) {
				player.incrementStatistic(Statistic.DROPPER_INSPECTED);
			} else if (container.getType() == Material.HOPPER) {
				player.incrementStatistic(Statistic.HOPPER_INSPECTED);
			}

			return TriState.TRUE;
		}

		if (container.getType() == Material.ENDER_CHEST) {
			if (!(container.getState(false) instanceof EnderChest enderChest)) return TriState.NOT_SET;
			if (!canInteractWithContainer(player, container, blockFace)) return TriState.FALSE;

			player.swingMainHand();
			if (!ignoreObstructions && enderChest.isBlocked()) return TriState.NOT_SET;
			if (!player.openEnderChest(enderChest)) return TriState.NOT_SET;

			player.incrementStatistic(Statistic.ENDERCHEST_OPENED);
			player.angerNearbyPiglins(true);
			return TriState.TRUE;
		}

		if (container.getType() == Material.CRAFTING_TABLE) {
			if (!canInteractWithContainer(player, container, blockFace)) return TriState.FALSE;

			InventoryView view = MenuType.CRAFTING.builder().location(container.getLocation()).build(player);
			player.openInventory(view);
			if (!player.getOpenInventory().equals(view))
				return TriState.FALSE;

			player.incrementStatistic(Statistic.CRAFTING_TABLE_INTERACTION);
			return TriState.TRUE;
		}

		if (BlockStorage.getByLoc(container) instanceof InventoryBlockDataStorageHolder storage) {
			Inventory inventory = storage.getInventory();
			if (!canInteractWithContainer(player, container, blockFace)) {
				if (storage.isViewing(player)) // In case interact triggered opening
					return TriState.TRUE;
				return TriState.FALSE;
			}

			player.swingMainHand();
			storage.openInventory(player);
			if (!storage.isViewing(player))
				return TriState.FALSE;

			return TriState.TRUE;
		}

		return TriState.FALSE;
	}

	private static boolean canInteractWithContainer(Player player, Block block, BlockFace blockFace) {
		var newEvent = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getInventory().getItemInMainHand(), block, blockFace, EquipmentSlot.HAND);
		newEvent.callEvent();
		return newEvent.useInteractedBlock() != Event.Result.DENY;
	}

}
