package me.sosedik.uglychatter.listener.misc;

import me.sosedik.uglychatter.api.chat.FancyMessageRenderer;
import me.sosedik.uglychatter.api.chat.FancyRendererTag;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Adds fancies support for books
 */
public class BookBeautifier implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBookEdit(@NotNull PlayerEditBookEvent event) {
		BookMeta meta = event.getNewBookMeta();
		if (event.isSigning())
			updateMeta(event.getPlayer(), meta, true);
		else
			updateMeta(event.getPlayer(), meta, false, FancyRendererTag.SKIP_PLACEHOLDERS, FancyRendererTag.SKIP_MARKDOWN);
		event.setNewBookMeta(meta);
	}

	/**
	 * Updates book meta's texts with markdown and placeholders
	 *
	 * @param player player viewer
	 * @param meta book meta
	 * @param render whether to render the texts
	 * @param tags tags to use upon stripping
	 */
	public static void updateMeta(@NotNull Player player, @NotNull BookMeta meta, boolean render, @NotNull FancyRendererTag... tags) {
		MiniMessage miniMessage = Messenger.messenger(player).miniMessage();
		if (meta.hasPages()) {
			for (var i = 1; i <= meta.getPageCount(); i++) { // Pages are 1-indexed
				Component page = meta.page(i);
				String rawPage = FancyMessageRenderer.getRawInput(page, tags);
				page = render ? FancyMessageRenderer.renderMessage(miniMessage, rawPage, player, player) : Component.text(rawPage);
				meta.page(i, page);
			}
		}

		if (meta.hasTitle()) {
			Component title = meta.title();
			if (title != null) {
				String rawTitle = FancyMessageRenderer.getRawInput(title, tags);
				title = render ? FancyMessageRenderer.renderMessage(miniMessage, rawTitle, player, player) : Component.text(rawTitle);
				meta.title(title);
			}
		}
	}

}
