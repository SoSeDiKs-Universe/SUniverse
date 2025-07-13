package me.sosedik.miscme.listener.entity;

import me.sosedik.miscme.api.event.player.PlayerIgniteExplosiveMinecartEvent;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Minecart with TNT can be manually primed
 */
@NullMarked
public class PrimingExplosiveMinecart implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof ExplosiveMinecart minecart)) return;

		Player player = event.getPlayer();
		if (tryToIgnite(player, minecart, EquipmentSlot.HAND)
			|| tryToIgnite(player, minecart, EquipmentSlot.OFF_HAND))
			event.setCancelled(true);
	}

	private boolean tryToIgnite(Player player, ExplosiveMinecart minecart, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (item.getType() == Material.FIRE_CHARGE) {
			item.subtract();
			player.emitSound(Sound.ITEM_FIRECHARGE_USE, 1F, 1F);
			player.incrementStatistic(Statistic.USE_ITEM, Material.FIRE_CHARGE);
		} else if (UtilizerTags.FLINT_AND_STEEL.isTagged(item.getType())) {
			item.damage(1, player);
			player.emitSound(Sound.ITEM_FLINTANDSTEEL_USE, 1F, 1F);
			player.incrementStatistic(Statistic.USE_ITEM, Material.FLINT_AND_STEEL);
		} else {
			return false;
		}
		player.swingHand(hand);
		minecart.ignite();
		new PlayerIgniteExplosiveMinecartEvent(player, minecart).callEvent();
		return true;
	}

}
