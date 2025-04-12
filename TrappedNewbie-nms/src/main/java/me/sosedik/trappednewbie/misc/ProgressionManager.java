package me.sosedik.trappednewbie.misc;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.api.task.BossBarTask;
import me.sosedik.trappednewbie.api.task.Task;
import me.sosedik.trappednewbie.api.task.TaskReference;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTasks;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@NullMarked
public class ProgressionManager {

	private final Map<String, Task> currentTasks = new HashMap<>();
	private final BossBarTask bossBarTask;
	private final Player player;

	public ProgressionManager(Player player) {
		this.player = player;
		this.bossBarTask = new BossBarTask(player);
	}

	public BossBarTask bossBar() {
		return bossBarTask;
	}

	public void checkProgressionTasks() {
		for (TaskReference taskReference : TrappedNewbieTasks.TUTORIAL_TREE) {
			Task task = taskReference.constructTask(player);
			if (task.canBeSkipped()) continue;

			task.onStart();
			bossBarTask.setProgressionTask(task);
			return;
		}
		bossBarTask.setProgressionTask(null);
	}

	public boolean hasTask(String id) {
		return currentTasks.containsKey(id);
	}

	public void addTask(Task task) {
		addTask(task.getTaskId(), task);
	}

	public void addTask(String id, Task task) {
		if (currentTasks.put(id, task) == null)
			task.onStart();
	}

	public @Nullable Task removeTask(String id) {
		Task task = currentTasks.remove(id);
		if (task != null)
			task.abort();
		return task;
	}

	public Collection<Task> tasks() {
		return currentTasks.values();
	}

	public void complete(Task task) {
		boolean progression = bossBar().getProgressionTask() == task;
		if (progression) {
			TrappedNewbie.scheduler().async(this::checkProgressionTasks, 10L);
		} else {
			removeTask(task.getTaskId());
		}
		task.onFinish();
	}

	public void startTasks() {
		for (Task task : tasks()) {
			task.onStart();
		}
	}

	public void abort() {
		for (Task task : tasks()) {
			task.abort();
		}
		Task task = bossBarTask.getProgressionTask();
		if (task != null)
			task.abort();
	}

}
