package me.sosedik.miscme.listener.entity;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Allow placing pumpkins on sheared snowmen
 */
@NullMarked
public class SnowmanPumpkin implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPumpkinSnowman(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof Snowman snowman)) return;

		Player player = event.getPlayer();
		if (tryPlacingPumpkin(player, snowman, EquipmentSlot.HAND) || tryPlacingPumpkin(player, snowman, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryPlacingPumpkin(Player player, Snowman snowman, EquipmentSlot hand) {
		if (!snowman.isDerp()) return false;

		ItemStack item = player.getInventory().getItem(hand);
		if (item.getType() != Material.CARVED_PUMPKIN) return false;

		player.swingHand(hand);
		snowman.setDerp(false);
		if (!player.getGameMode().isInvulnerable())
			item.subtract();

		return true;
	}

}
