package me.sosedik.requiem.api.event.player;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerTryPossessingEntityEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private final LivingEntity entity;
	private boolean cancelled = false;

	public PlayerTryPossessingEntityEvent(Player who, LivingEntity entity) {
		super(who);
		this.entity = entity;
	}

	/**
	 * Gets the possessed entity
	 *
	 * @return the possessed entity
	 */
	public LivingEntity getEntity() {
		return this.entity;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
