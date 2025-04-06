package me.sosedik.uglychatter.listener.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jspecify.annotations.NullMarked;

/**
 * Preview writable book's final result after applying formatting
 */
@NullMarked
public class PreviewBookFormatting implements Listener {

	private static final Component DUMMY_AUTHOR = Component.text(Bukkit.getServerName());

	@EventHandler
	public void onBookOpen(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;

		ItemStack item = event.getItem();
		if (item == null) return;
		if (item.getType() != Material.WRITABLE_BOOK) return;
		if (!tryToOpenBook(player, item)) return;

		event.setCancelled(true);
		player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1F, 1F);
	}

	/**
	 * Applies book formatting and opens it for the player
	 *
	 * @param player player
	 * @param item book item
	 * @return whether was able to open the book
	 */
	public static boolean tryToOpenBook(Player player, ItemStack item) {
		if (!(item.getItemMeta() instanceof BookMeta oldMeta)) return false;

		if (item.getType() != Material.WRITTEN_BOOK) {
			item = new ItemStack(Material.WRITTEN_BOOK);
			if (!(item.getItemMeta() instanceof BookMeta meta)) return false;

			item.setItemMeta(meta.toBuilder().title(DUMMY_AUTHOR).author(DUMMY_AUTHOR).pages(oldMeta.pages()).build());
		}

		player.openBook(item);
		return true;
	}

}
