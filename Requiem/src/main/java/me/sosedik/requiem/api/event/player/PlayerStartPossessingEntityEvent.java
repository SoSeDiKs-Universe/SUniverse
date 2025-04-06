package me.sosedik.requiem.api.event.player;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerStartPossessingEntityEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final LivingEntity entity;

	public PlayerStartPossessingEntityEvent(Player who, LivingEntity entity) {
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
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
