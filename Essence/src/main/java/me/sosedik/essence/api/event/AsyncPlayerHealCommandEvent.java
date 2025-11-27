package me.sosedik.essence.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called when the player uses /heal command
 */
@NullMarked
public class AsyncPlayerHealCommandEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public AsyncPlayerHealCommandEvent(Player who) {
		super(who, true);
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
