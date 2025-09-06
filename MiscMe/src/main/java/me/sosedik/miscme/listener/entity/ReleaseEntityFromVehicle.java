package me.sosedik.miscme.listener.entity;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Shift-right click entity in vehicle to release it
 */
@NullMarked
public class ReleaseEntityFromVehicle implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof LivingEntity entity)) return;
		if (entity instanceof Player) return;
		if (!entity.isInsideVehicle()) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;
		if (!player.getInventory().getItemInMainHand().isEmpty()) return;

		event.setCancelled(true);
		entity.leaveVehicle();
		entity.emitSound(Sound.BLOCK_ANVIL_FALL, 1F, 0.1F);
	}

}
