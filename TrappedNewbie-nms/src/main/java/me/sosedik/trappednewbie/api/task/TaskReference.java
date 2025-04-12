package me.sosedik.trappednewbie.api.task;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record TaskReference(String taskKey, Class<? extends Task> taskClass) {

	public Task constructTask(Player player) {
		return Task.getTask(taskClass(), taskKey(), player);
	}

}
