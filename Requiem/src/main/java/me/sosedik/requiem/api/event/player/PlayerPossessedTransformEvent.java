package me.sosedik.requiem.api.event.player;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerPossessedTransformEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final LivingEntity transformed;
	private final LivingEntity entity;

	public PlayerPossessedTransformEvent(Player player, LivingEntity transformed, LivingEntity entity) {
		super(player);
		this.transformed = transformed;
		this.entity = entity;
	}

	/**
	 * Gets the transformed entity
	 *
	 * @return the transformed entity
	 */
	public LivingEntity getTransformed() {
		return this.transformed;
	}

	/**
	 * Gets the prior entity
	 *
	 * @return the prior entity
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
