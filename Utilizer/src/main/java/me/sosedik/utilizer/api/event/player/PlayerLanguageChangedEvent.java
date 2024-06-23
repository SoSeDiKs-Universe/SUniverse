package me.sosedik.utilizer.api.event.player;

import me.sosedik.utilizer.api.language.LangOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when player changes their server language
 */
public class PlayerLanguageChangedEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final LangOptions oldLanguage;
	private final LangOptions newLanguage;

	public PlayerLanguageChangedEvent(@NotNull Player player, @NotNull LangOptions oldLanguage, @NotNull LangOptions newLanguage) {
		super(player);
		this.oldLanguage = oldLanguage;
		this.newLanguage = newLanguage;
	}

	/**
	 * Returns old player's language
	 *
	 * @return old player's language
	 */
	public @NotNull LangOptions getOldLanguage() {
		return oldLanguage;
	}

	/**
	 * Returns new player's language
	 *
	 * @return new player's language
	 */
	public @NotNull LangOptions getNewLanguage() {
		return newLanguage;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static @NotNull HandlerList getHandlerList() {
		return HANDLERS;
	}

}
