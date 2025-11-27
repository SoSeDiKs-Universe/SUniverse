package me.sosedik.moves.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called when the player stops falling
 */
@NullMarked
public class PlayerStopFallingEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final float fallDistance;

	public PlayerStopFallingEvent(Player who, float fallDistance) {
		super(who);
		this.fallDistance = fallDistance;
	}

	/**
	 * Gets the fall distance before landing
	 *
	 * @return fall distance
	 */
	public float getFallDistance() {
		return this.fallDistance;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
