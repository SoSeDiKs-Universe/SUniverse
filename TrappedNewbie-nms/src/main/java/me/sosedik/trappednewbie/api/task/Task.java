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

@NullMarked
public abstract class Task {

	private final String taskId;
	private final Player player;
	private @Nullable Listener eventsListener;

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
		if (this.eventsListener != null)
			HandlerList.unregisterAll(this.eventsListener);
		this.eventsListener = listener;
		if (this.eventsListener != null)
			Bukkit.getPluginManager().registerEvents(listener, TrappedNewbie.instance());
	}

	public @Nullable Component @Nullable [] getDisplay() {
		return Messenger.messenger(getPlayer()).getMessages("task." + getTaskId());
	}

	public void onStart() {
		if (canBeSkipped()) {
			finish();
			return;
		}
		if (this instanceof Listener listener)
			startListening(listener);
	}

	public void onFinish() {
		this.player.playSound(this.player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1F, (float) Math.random() * 0.2F + 0.9F);
	}

	public void abort() {
		startListening(null);
	}

	public boolean canBeSkipped() {
		return false;
	}

	protected void finish() {
		abort();
		TrappedNewbie.scheduler().sync(() -> new PlayerCompletedTaskEvent(this.player, this).callEvent());
	}

}
