package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
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
import org.jspecify.annotations.Nullable;

/**
 * Sadly, rocks can't be trident and block at the same time
 */
public class PlaceableRocks implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		Player player = event.getPlayer();
		BlockFace blockFace = event.getBlockFace();
		if (tryToPlaceRock(player, block, blockFace, EquipmentSlot.HAND)
			|| tryToPlaceRock(player, block, blockFace, EquipmentSlot.OFF_HAND)) {
			event.setCancelled(true);
			player.completeUsingActiveItem();
		}
	}

	private boolean tryToPlaceRock(Player player, Block block, BlockFace blockFace, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (!TrappedNewbieTags.ROCKS.isTagged(item.getType())) return false;

		Material pebble = getPebble(item.getType());
		if (pebble == null) return false;

		if (!player.placeBlock(ItemStack.of(pebble), block.getLocation(), blockFace)) return false;

		player.swingHand(hand);
		if (!player.getGameMode().isInvulnerable())
			item.subtract();

		return true;
	}

	private @Nullable Material getPebble(Material rock) {
		return Material.getMaterial(rock.name().replace("ROCK", "PEBBLE"));
	}

}
