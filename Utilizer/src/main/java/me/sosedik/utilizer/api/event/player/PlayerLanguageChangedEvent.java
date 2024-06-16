package me.sosedik.utilizer.api.event.player;

import me.sosedik.utilizer.api.language.LangKey;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when player changes their server language
 */
public class PlayerLanguageChangedEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final LangKey oldLangKey;
	private final LangKey newLangKey;

	public PlayerLanguageChangedEvent(@NotNull Player player, @NotNull LangKey oldLangKey, @NotNull LangKey newLangKey) {
		super(player);
		this.oldLangKey = oldLangKey;
		this.newLangKey = newLangKey;
	}

	/**
	 * Returns old player's language
	 *
	 * @return old player's language
	 */
	public @NotNull LangKey getOldLangKey() {
		return oldLangKey;
	}

	/**
	 * Returns new player's language
	 *
	 * @return new player's language
	 */
	public @NotNull LangKey getNewLangKey() {
		return newLangKey;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static @NotNull HandlerList getHandlerList() {
		return HANDLERS;
	}

}
