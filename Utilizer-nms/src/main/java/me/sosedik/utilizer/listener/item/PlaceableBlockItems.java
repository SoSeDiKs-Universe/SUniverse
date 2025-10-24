package me.sosedik.utilizer.listener.item;

import org.bukkit.Material;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Placing items as blocks
 */
public class PlaceableBlockItems implements Listener {

	private static final Map<Material, Material> MAPPINGS = new HashMap<>();

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		Player player = event.getPlayer();
		BlockFace blockFace = event.getBlockFace();
		if (tryToPlaceItem(player, block, blockFace, EquipmentSlot.HAND)
				|| tryToPlaceItem(player, block, blockFace, EquipmentSlot.OFF_HAND)) {
			event.setCancelled(true);
			player.clearActiveItem();
		}
	}

	private boolean tryToPlaceItem(Player player, Block block, BlockFace blockFace, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		Material blockType = MAPPINGS.get(item.getType());
		if (blockType == null) return false;

		if (!player.placeBlock(ItemStack.of(blockType), block.getLocation(), blockFace)) return false;

		player.swingHand(hand);
		if (!player.getGameMode().isInvulnerable())
			item.subtract();

		return true;
	}

	/**
	 * Adds a new mapping
	 *
	 * @param item item variant
	 * @param block block variant
	 */
	public static void addMapping(Material item, Material block) {
		MAPPINGS.put(item, block);
	}

}
