package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.dataset.TrappedNewbieRecipes;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.utilizer.util.InventoryUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Bowls can be filled with milk
 */
@NullMarked
public class FillingBowlWithMilk implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof Cow) && !(event.getRightClicked() instanceof Goat)) return;

		Player player = event.getPlayer();
		if (tryToFill(player, EquipmentSlot.HAND) || tryToFill(player, EquipmentSlot.OFF_HAND)) {
			event.setCancelled(true);
			if (event.getRightClicked() instanceof Cow) {
				player.emitSound(Sound.ENTITY_COW_MILK, 1F, 1F);
			} else if (event.getRightClicked() instanceof Goat goat) {
				player.emitSound(goat.isScreaming() ? Sound.ENTITY_GOAT_SCREAMING_MILK : Sound.ENTITY_GOAT_MILK, 1F, 1F);
			}
		}
	}

	private boolean tryToFill(Player player, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		ItemStack result = TrappedNewbieRecipes.getFilled(item, ThirstData.DrinkType.MILK);
		if (result == null) return false;

		player.swingHand(hand);
		item.subtract();
		InventoryUtil.replaceOrAdd(player, hand, result);
		return true;
	}

}
