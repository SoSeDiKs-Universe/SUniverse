package me.sosedik.trappednewbie.api.task;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

@NullMarked
public record TaskReference(String taskKey, Function<Player, ? extends Task> taskSupplier) {

	public TaskReference(String taskKey, Class<? extends Task> taskClass) {
		this(taskKey, (player) -> getTask(taskClass, taskKey, player));
	}

	public Task constructTask(Player player) {
		return this.taskSupplier.apply(player);
	}

	public static <T extends Task> T getTask(Class<T> taskClass, String taskId, Player player) {
		try {
			return taskClass.getDeclaredConstructor(String.class, Player.class).newInstance(taskId, player);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException("Something went wrong while creating new task", e);
		}
	}

}
