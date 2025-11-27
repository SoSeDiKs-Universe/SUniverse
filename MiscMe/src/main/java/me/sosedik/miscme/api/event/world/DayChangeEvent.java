package me.sosedik.miscme.api.event.world;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called when a new day starts in the world
 */
@NullMarked
public class DayChangeEvent extends WorldEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public DayChangeEvent(World world) {
		super(world);
	}

	/**
	 * Gets the current day number
	 *
	 * @return day number
	 */
	public long getCurrentDay() {
		return getWorld().getFullTime() / 24_000;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
