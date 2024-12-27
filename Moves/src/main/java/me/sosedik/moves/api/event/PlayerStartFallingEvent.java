package me.sosedik.moves.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called when the player starts falling
 */
@NullMarked
public class PlayerStartFallingEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public PlayerStartFallingEvent(Player who) {
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
