package me.sosedik.trappednewbie.api.task.event;

import me.sosedik.trappednewbie.api.task.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerCompletedTaskEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();

	private final Task task;

	public PlayerCompletedTaskEvent(Player player, Task task) {
		super(player);
		this.task = task;
	}

	/**
	 * Gets the completed task
	 *
	 * @return the completed task
	 */
	public Task getTask() {
		return task;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
