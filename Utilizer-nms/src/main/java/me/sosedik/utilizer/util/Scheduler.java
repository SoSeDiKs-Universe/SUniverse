package me.sosedik.utilizer.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jspecify.annotations.NullMarked;

import java.util.function.Predicate;

/**
 * Helper class for scheduling tasks
 *
 * @param plugin owning plugin
 */
@NullMarked
public record Scheduler(Plugin plugin) {

	/**
	 * Schedules a once off task to occur as soon as possible.
	 * <p>
	 * This task will be executed by the main server thread.
	 *
	 * @param runnable Task to be executed
	 * @return Task id number (-1 if scheduling failed)
	 */
	public int sync(Runnable runnable) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, runnable);
	}

	/**
	 * Schedules a once off task to occur after a delay.
	 * <p>
	 * This task will be executed by the main server thread.
	 *
	 * @param runnable Task to be executed
	 * @param delay Delay in server ticks before executing task
	 * @return Task id number (-1 if scheduling failed)
	 */
	public int sync(Runnable runnable, long delay) {
		return Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, runnable, delay);
	}

	/**
	 * Schedules task to repeatedly run until cancelled, starting after the
	 * specified number of server ticks.
	 *
	 * @param delay the ticks to wait before running the task
	 * @param period the ticks to wait between runs
	 * @return a BukkitTask that contains the id number
	 */
	public BukkitTask sync(Predicate<BukkitRunnable> runnable, long delay, long period) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (runnable.test(this))
					cancel();
			}
		}.runTaskTimer(this.plugin, delay, period);
	}

	/**
	 * Schedules task to repeatedly run until cancelled, starting after the
	 * specified number of server ticks.
	 *
	 * @param delay the ticks to wait before running the task
	 * @param period the ticks to wait between runs
	 * @return a BukkitTask that contains the id number
	 */
	public BukkitTask sync(Runnable runnable, long delay, long period) {
		return Bukkit.getScheduler().runTaskTimer(this.plugin, runnable, delay, period);
	}

	/**
	 * Schedules task to repeatedly run until cancelled, starting after the
	 * specified number of server ticks.
	 *
	 * @param delay the ticks to wait before running the task
	 * @param period the ticks to wait between runs
	 * @return a BukkitTask that contains the id number
	 */
	public BukkitTask sync(BukkitRunnable runnable, long delay, long period) {
		return runnable.runTaskTimer(this.plugin, delay, period);
	}

	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit.</b> <b>Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Returns a task that will run asynchronously.
	 *
	 * @param runnable the task to be run
	 * @return a BukkitTask that contains the id number
	 */
	public BukkitTask async(Runnable runnable) {
		return Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
	}

	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Returns a task that will run asynchronously after the specified number
	 * of server ticks.
	 *
	 * @param runnable the task to be run
	 * @param delay the ticks to wait before running the task
	 * @return a BukkitTask that contains the id number
	 */
	public BukkitTask async(Runnable runnable, long delay) {
		return Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, runnable, delay);
	}

	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Schedules task to repeatedly run asynchronously until cancelled,
	 * starting after the specified number of server ticks.
	 *
	 * @param delay the ticks to wait before running the task for the first
	 *     time
	 * @param period the ticks to wait between runs
	 * @return a BukkitTask that contains the id number
	 */
	public BukkitTask async(Predicate<BukkitRunnable> runnable, long delay, long period) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (runnable.test(this))
					cancel();
			}
		}.runTaskTimerAsynchronously(this.plugin, delay, period);
	}

	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Schedules task to repeatedly run asynchronously until cancelled,
	 * starting after the specified number of server ticks.
	 *
	 * @param delay the ticks to wait before running the task for the first
	 *     time
	 * @param period the ticks to wait between runs
	 * @return a BukkitTask that contains the id number
	 */
	public BukkitTask async(Runnable runnable, long delay, long period) {
		return Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, runnable, delay, period);
	}

	/**
	 * <b>Asynchronous tasks should never access any API in Bukkit. Great care
	 * should be taken to assure the thread-safety of asynchronous tasks.</b>
	 * <p>
	 * Schedules task to repeatedly run asynchronously until cancelled,
	 * starting after the specified number of server ticks.
	 *
	 * @param delay the ticks to wait before running the task for the first
	 *     time
	 * @param period the ticks to wait between runs
	 * @return a BukkitTask that contains the id number
	 */
	public BukkitTask async(BukkitRunnable runnable, long delay, long period) {
		return runnable.runTaskTimerAsynchronously(this.plugin, delay, period);
	}

	/**
	 * Removes task from scheduler.
	 *
	 * @param taskId id number of task to be removed
	 */
	public void cancelTask(int taskId) {
		Bukkit.getScheduler().cancelTask(taskId);
	}

}
