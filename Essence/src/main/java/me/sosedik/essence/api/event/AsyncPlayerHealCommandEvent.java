package me.sosedik.essence.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the player uses /heal command
 */
public class AsyncPlayerHealCommandEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public AsyncPlayerHealCommandEvent(@NotNull Player who) {
		super(who, true);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static @NotNull HandlerList getHandlerList() {
		return HANDLERS;
	}

}
