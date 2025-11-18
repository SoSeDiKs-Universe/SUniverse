package me.sosedik.trappednewbie.listener.item;

import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import me.sosedik.trappednewbie.listener.player.VisualArmorLayer;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Hot items can't be held without gloves
 */
@NullMarked
public class HotItemsRequireGloves implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onItemClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (player.getGameMode().isInvulnerable()) return;

		ItemStack item = event.getCurrentItem();
		if (ItemStack.isEmpty(item)) return;
		if (!ItemUtil.isHot(item)) return;

		VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(player);
		if (!visualArmor.canUseVisualArmor()) return;
		if (visualArmor.hasNonBrokenGloves()) return;
		if (!applyHotEffect(player, item)) return;

		event.setCancelled(true);
		event.setCurrentItem(null);
	}

	@EventHandler(ignoreCancelled = true)
	public void onItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode().isInvulnerable()) return;

		ItemStack item = player.getInventory().getItem(event.getNewSlot());
		if (ItemStack.isEmpty(item)) return;
		if (!ItemUtil.isHot(item)) return;

		VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(player);
		if (!visualArmor.canUseVisualArmor()) return;
		if (visualArmor.hasNonBrokenGloves()) return;
		if (!applyHotEffect(player, item)) return;

		player.getInventory().setItem(event.getNewSlot(), null);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (player.getGameMode().isInvulnerable()) return;

		ItemStack item = event.getItem().getItemStack();
		if (!ItemUtil.isHot(item)) return;

		VisualArmor visualArmor = VisualArmorLayer.getVisualArmor(player);
		if (!visualArmor.canUseVisualArmor()) return;
		if (visualArmor.hasNonBrokenGloves()) return;

		if (!applyHotEffect(player, item)) return;

		event.setCancelled(true);
		event.getItem().remove();
	}

	private boolean applyHotEffect(Player player, ItemStack item) {
		var event = new EntityCombustEvent(player, 3F);
		if (!event.callEvent()) return false;

		int fireTicks = (int) (event.getDuration() * 20);
		player.setFireTicks(Math.max(fireTicks, player.getFireTicks()));

		item = item.clone();
		HudMessenger.of(player).displayMessage(Messenger.messenger(player).getMessage("equipment.gloves.hot_item_hold"));
		player.dropItem(item);
		return true;
	}

}
