package me.sosedik.trappednewbie.api.task;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.task.event.PlayerCompletedTaskEvent;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

@NullMarked
public abstract class Task {

	private final String taskId;
	private final Player player;
	private @Nullable Listener listener;

	protected Task(String taskId, Player player) {
		this.taskId = taskId;
		this.player = player;
	}

	public String getTaskId() {
		return taskId;
	}

	public Player getPlayer() {
		return player;
	}

	protected void startListening(@Nullable Listener listener) {
		if (this.listener != null)
			HandlerList.unregisterAll(this.listener);
		this.listener = listener;
		if (this.listener != null)
			Bukkit.getPluginManager().registerEvents(listener, TrappedNewbie.instance());
	}

	public @Nullable Component[] getDisplay() {
		return Messenger.messenger(getPlayer()).getMessages("task." + getTaskId());
	}

	public void onStart() {
		if (this instanceof Listener listener)
			startListening(listener);
	}

	public void onFinish() {
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
	}

	public void abort() {
		startListening(null);
	}

	public boolean canBeSkipped() {
		return false;
	}

	protected void finish() {
		abort();
		TrappedNewbie.scheduler().sync(() -> new PlayerCompletedTaskEvent(player, this).callEvent());
	}

	public static <T extends Task> T getTask(Class<T> taskClass, String taskId, Player player) {
		try {
			return taskClass.getDeclaredConstructor(String.class, Player.class).newInstance(taskId, player);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException("Something went wrong while creating new task", e);
		}
	}

}
