package me.sosedik.utilizer.listener.misc;

import me.sosedik.utilizer.Utilizer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Adds extra suggestions to tab complete in chat
 */
public class ExtraChatTabSuggestions implements Listener {

	private static final Set<String> EXTRA_TAB_SUGGESTIONS = new HashSet<>();

	@EventHandler
	public void onJoin(@NotNull PlayerJoinEvent event) {
		event.getPlayer().addCustomChatCompletions(EXTRA_TAB_SUGGESTIONS);
	}

	/**
	 * Gets the extra chat tab suggestions
	 *
	 * @return the extra chat tab suggestions
	 */
	public static @NotNull Set<String> getTabSuggestions() {
		return EXTRA_TAB_SUGGESTIONS;
	}

	/**
	 * Adds a new chat tab suggestion
	 *
	 * @param suggestion suggestion
	 */
	public static void addTabSuggestion(@NotNull String suggestion) {
		if (!EXTRA_TAB_SUGGESTIONS.add(suggestion))
			Utilizer.logger().warn("Tried to add {} to chat tab completion twice!", suggestion);
	}

}