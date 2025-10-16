package me.sosedik.miscme.listener.item;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Random;

/**
 * RMB click with a torch removes cobwebs
 */
@NullMarked
public class TorchesBurnCobwebs implements Listener {

	private static final Random RANDOM = new Random();

	@EventHandler(ignoreCancelled = true)
	public void onClick(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
		if (block.getType() != Material.COBWEB) return;

		Player player = event.getPlayer();
		if (tryToBurn(player, block, EquipmentSlot.HAND) || tryToBurn(player, block, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryToBurn(Player player, Block block, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (!isTorch(item.getType())) return false;

		player.swingHand(hand);
		block.getWorld().spawnParticle(Particle.FLAME, block.getLocation().toCenterLocation(), 7 + RANDOM.nextInt(40), RANDOM.nextDouble() * 0.5D, RANDOM.nextDouble() * 0.5D, RANDOM.nextDouble() * 0.5D, 0.005);
		block.setType(Material.AIR);
		return true;
	}

	private boolean isTorch(Material type) {
		return type == Material.TORCH || type == Material.SOUL_TORCH || type == Material.COPPER_TORCH;
	}

}
