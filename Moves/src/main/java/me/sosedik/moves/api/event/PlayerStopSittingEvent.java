package me.sosedik.moves.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called when the player stops sitting
 */
@NullMarked
public class PlayerStopSittingEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public PlayerStopSittingEvent(Player who) {
		super(who);
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
