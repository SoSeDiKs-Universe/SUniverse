package me.sosedik.requiem.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerStartGhostingEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public PlayerStartGhostingEvent(Player who) {
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
