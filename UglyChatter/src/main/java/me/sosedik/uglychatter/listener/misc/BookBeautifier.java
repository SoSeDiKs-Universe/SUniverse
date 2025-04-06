package me.sosedik.uglychatter.listener.misc;

import io.papermc.paper.datacomponent.item.WritableBookContent;
import io.papermc.paper.datacomponent.item.WrittenBookContent;
import io.papermc.paper.text.Filtered;
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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds fancies support for books
 */
@NullMarked
public class BookBeautifier implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBookEdit(PlayerEditBookEvent event) {
		BookMeta meta = event.getNewBookMeta();
		updateMeta(event.getPlayer(), meta, event.isSigning());
		event.setNewBookMeta(meta);
	}

	/**
	 * Updates book meta's texts with markdown and placeholders
	 *
	 * @param player player viewer
	 * @param meta book meta
	 * @param render whether to render the texts
	 */
	public static void updateMeta(Player player, BookMeta meta, boolean render) {
		MiniMessage miniMessage = Messenger.messenger(player).miniMessage();
		if (meta.hasPages()) {
			for (int i = 1; i <= meta.getPageCount(); i++) { // Pages are 1-indexed
				Component page = meta.page(i);
				Component parsed = render ? render(miniMessage, page, player) : Component.text(asRaw(page));
				meta.page(i, parsed);
			}
		}
	}

	/**
	 * Updates book's texts with markdown and placeholders
	 *
	 * @param player player viewer
	 * @param writtenBookContent book content
	 */
	public static @Nullable WrittenBookContent updateContent(Player player, WrittenBookContent writtenBookContent) {
		List<Filtered<Component>> pages = writtenBookContent.pages();
		if (pages.isEmpty()) return null;

		MiniMessage miniMessage = Messenger.messenger(player).miniMessage();
		List<Component> newPages = new ArrayList<>();
		for (Filtered<Component> page : pages)
			newPages.add(render(miniMessage, page.raw(), player));

		// TODO replace with #toBuilder once available
		return WrittenBookContent.writtenBookContent(writtenBookContent.title(), writtenBookContent.author())
				.generation(writtenBookContent.generation())
				.resolved(writtenBookContent.resolved())
				.addPages(newPages)
				.build();
	}

	/**
	 * Updates book's texts with markdown and placeholders
	 *
	 * @param writableBookContent book content
	 */
	public static @Nullable WritableBookContent updateContent(WritableBookContent writableBookContent) {
		List<Filtered<String>> pages = writableBookContent.pages();
		if (pages.isEmpty()) return null;

		List<String> newPages = new ArrayList<>();
		for (Filtered<String> page : pages)
			newPages.add(asRaw(Component.text(page.raw())));

		// TODO replace with #toBuilder once available
		return WritableBookContent.writeableBookContent()
				.addPages(newPages)
				.build();
	}

	private static String asRaw(Component rawInput) {
		return FancyMessageRenderer.getRawInput(rawInput, FancyRendererTag.SKIP_PLACEHOLDERS, FancyRendererTag.SKIP_MARKDOWN);
	}

	private static Component render(MiniMessage miniMessage, Component rawComponent, Player player, FancyRendererTag... tags) {
		String rawInput = FancyMessageRenderer.getRawInput(rawComponent, tags);
		return FancyMessageRenderer.renderMessage(miniMessage, rawInput, player, player);
	}

}
