package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.api.event.player.PlayerTargetBlockEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.trappednewbie.misc.BlockBreakTask;
import org.bukkit.block.Block;
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
 * Hammer can repair blocks
 */
@NullMarked
public class HammerBlockRepair implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onHammerRepair(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		ItemStack item = player.getInventory().getItemInMainHand();
		if (!TrappedNewbieTags.HAMMERS.isTagged(item.getType())) return;
		if (!BlockBreakTask.clearBlock(block)) return;

		event.setCancelled(true);
		block.emitSound(block.getBlockSoundGroup().getPlaceSound(), 0.5F, 1F);
		if (Math.random() < 0.1)
			item.damage(1, player);
	}

	@EventHandler(ignoreCancelled = true)
	public void onTargetCrackedWithHammer(PlayerTargetBlockEvent event) {
		Player player = event.getPlayer();
		if (player.isSneaking()) return;
		if (!TrappedNewbieTags.HAMMERS.isTagged(player.getInventory().getItemInMainHand().getType())) return;

		Block block = event.getBlock();
		if (!BlockBreakTask.isCracked(block)) return;

		event.setCancelled(true);
	}

}
