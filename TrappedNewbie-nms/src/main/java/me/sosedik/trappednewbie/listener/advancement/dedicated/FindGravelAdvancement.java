package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FindGravelAdvancement implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!event.hasChangedBlock()) return;

		Player player = event.getPlayer();
		if (player.isFlying()) return;

		Block block = event.getTo().getBlock().getRelative(BlockFace.DOWN);
		if (block.getType() != Material.GRAVEL && block.getType() != Material.SUSPICIOUS_GRAVEL) return;

		TrappedNewbieAdvancements.FIND_GRAVEL.awardAllCriteria(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClick(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
		if (block.getType() != Material.GRAVEL && block.getType() != Material.SUSPICIOUS_GRAVEL) return;

		TrappedNewbieAdvancements.FIND_GRAVEL.awardAllCriteria(event.getPlayer());
	}

}
