package me.sosedik.trappednewbie.listener.block;

import me.sosedik.trappednewbie.api.event.player.PlayerTargetBlockEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Stripping logs produces barks
 */
@NullMarked
public class LogStrippingGivesBarks implements Listener {

	private static final Map<Material, BarkGiver> BARK_GIVERS = new HashMap<>();
	private static final Map<Material, BarkGiver> BARK_GIVERS_REVERSE = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onStrip(EntityChangeBlockEvent event) {
		Block block = event.getBlock();
		BarkGiver barkGiver = BARK_GIVERS.get(block.getType());
		if (barkGiver == null) return;
		if (barkGiver.to() != event.getTo()) return;

		block.getWorld().dropItemNaturally(block.getLocation().center(), barkGiver.drop());
	}

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		BarkGiver barkGiver = BARK_GIVERS_REVERSE.get(block.getType());
		if (barkGiver == null) return;

		Player player = event.getPlayer();
		if (tryToConvert(player, block, barkGiver, EquipmentSlot.HAND, true)
			|| tryToConvert(player, block, barkGiver, EquipmentSlot.OFF_HAND, true))
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onTarget(PlayerTargetBlockEvent event) {
		Block block = event.getBlock();
		BarkGiver barkGiver = BARK_GIVERS_REVERSE.get(block.getType());
		if (barkGiver == null) return;

		Player player = event.getPlayer();
		event.setCancelled(tryToConvert(player, block, barkGiver, EquipmentSlot.HAND, false) || tryToConvert(player, block, barkGiver, EquipmentSlot.OFF_HAND, false));
	}

	private boolean tryToConvert(Player player, Block block, BarkGiver barkGiver, EquipmentSlot slot, boolean act) {
		ItemStack item = player.getInventory().getItem(slot);
		if (item.getType() != barkGiver.drop().getType()) return false;
		if (item.getAmount() < barkGiver.drop().getAmount()) return false;

		if (act) {
			player.swingHand(slot);
			item.subtract(barkGiver.drop().getAmount());
			block.setType(barkGiver.to());
			block.emitSound(Sound.ENTITY_ITEM_FRAME_PLACE, 1F, 1.5F);
		}

		return true;
	}

	private record BarkGiver(Material to, ItemStack drop) { }

	public static void addBark(Material from, Material to, ItemStack drop) {
		BARK_GIVERS.put(from, new BarkGiver(to, drop));
		BARK_GIVERS_REVERSE.put(to, new BarkGiver(from, drop));
	}

}
