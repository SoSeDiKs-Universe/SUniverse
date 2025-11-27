package me.sosedik.requiem.listener.item;

import me.sosedik.requiem.dataset.RequiemItems;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Host revocator can be used as a trash can
 */
@NullMarked
public class HostRevocatorItemDestroying implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (event.getClickedInventory() == null) return;
		if (event.getClick() != ClickType.RIGHT) return;

		ItemStack currentItem = event.getCurrentItem();
		if (!ItemStack.isType(currentItem, RequiemItems.HOST_REVOCATOR)) return;

		ItemStack cursor = event.getCursor();
		if (cursor.isEmpty()) return;

		event.setCancelled(true);
		player.setItemOnCursor(null);
		player.playSound(player, Sound.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.AMBIENT, 1F, 1.2F);
	}

}
