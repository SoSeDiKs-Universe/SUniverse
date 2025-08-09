package me.sosedik.trappednewbie.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called upon checking whether comfort effect should be erased
 */
@NullMarked
public class PlayerComfortEraseEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled = false;

	public PlayerComfortEraseEvent(Player who) {
		super(who);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}


}
