package me.sosedik.miscme.event.world;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a new day starts in the world
 */
public class DayChangeEvent extends WorldEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	public DayChangeEvent(@NotNull World world) {
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
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static @NotNull HandlerList getHandlerList() {
		return HANDLERS;
	}

}
