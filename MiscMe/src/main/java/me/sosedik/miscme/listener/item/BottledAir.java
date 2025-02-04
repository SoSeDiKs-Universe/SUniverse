package me.sosedik.miscme.listener.item;

import io.papermc.paper.event.player.PlayerFillBottleEvent;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Air can be obtained from empty bottles.
 * <br>Bottle won't be filled with water (with a chance to consume/break the bottle)
 * if the player is holding a hot item in the second hand.
 */
public class BottledAir implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onFill(@NotNull PlayerFillBottleEvent event) {
		ItemStack bottle = event.getBottle();
		if (bottle.getType() != Material.GLASS_BOTTLE) return;
		if (!ItemUtil.isWaterBottle(event.getResultItem())) return;

		Player player = event.getPlayer();
		if (!player.isInWaterOrBubbleColumn()) return;

		int maxAir = player.getMaximumAir() - 1;
		int currentAir = player.getRemainingAir();
		if (currentAir >= maxAir) return;

		player.setRemainingAir(Math.min(maxAir, currentAir + 150));
		player.setCooldown(bottle, 15);

		ItemStack secondItem = player.getInventory().getItemInOffHand();
		if (secondItem.getType() == Material.GLASS_BOTTLE)
			secondItem = player.getInventory().getItemInMainHand();

		if (!ItemUtil.isHot(secondItem)) return;

		player.emitSound(Sound.BLOCK_FIRE_EXTINGUISH, 1F, 1.2F);
		if (Math.random() < 0.5) {
			player.emitSound(Sound.BLOCK_GLASS_BREAK, 1F, 1.2F);
			event.setResultItem(ItemStack.empty());
			return;
		}

		event.setResultItem(bottle.asOne());
	}

}
