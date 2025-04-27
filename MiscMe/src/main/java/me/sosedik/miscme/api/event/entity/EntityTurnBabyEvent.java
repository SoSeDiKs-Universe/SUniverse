package me.sosedik.miscme.api.event.entity;

import me.sosedik.miscme.listener.entity.MoreBabyMobs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called when non-ageable mob becomes a baby
 */
@NullMarked
public class EntityTurnBabyEvent extends EntityEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private boolean cancelled;

	public EntityTurnBabyEvent(LivingEntity entity) {
		super(entity);
	}

	@Override
	public LivingEntity getEntity() {
		return (LivingEntity) super.getEntity();
	}

	/**
	 * Checks whether the entity was a baby already
	 *
	 * @return  whether the entity was a baby already
	 */
	public boolean wasBaby() {
		return MoreBabyMobs.isNonVanillaBaby(getEntity());
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
