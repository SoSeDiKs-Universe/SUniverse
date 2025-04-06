package me.sosedik.miscme.listener.item;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import me.sosedik.uglychatter.listener.item.PreviewBookFormatting;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Books inside item frames can be read with RMB
 */
@NullMarked
public class ReadableBooksInFrames implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onFrameBookOpen(PlayerItemFrameChangeEvent event) {
		if (event.getAction() != PlayerItemFrameChangeEvent.ItemFrameChangeAction.ROTATE) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		ItemStack item = event.getItemFrame().getItem();
		if (!PreviewBookFormatting.tryToOpenBook(player, item)) return;

		event.setCancelled(true);
		player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1F, 1F);
	}

}
