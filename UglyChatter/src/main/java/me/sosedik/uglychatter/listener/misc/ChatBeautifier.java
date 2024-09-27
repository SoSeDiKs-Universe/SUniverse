package me.sosedik.uglychatter.listener.misc;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.sosedik.uglychatter.api.chat.FancyMessageRenderer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Changes the chat format
 */
public class ChatBeautifier implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onChat(@NotNull AsyncChatEvent event) {
		var renderer = new FancyMessageRenderer();
		event.renderer(renderer);
	}

}
