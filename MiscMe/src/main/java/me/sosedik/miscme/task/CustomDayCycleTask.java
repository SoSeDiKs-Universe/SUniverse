package me.sosedik.miscme.task;

import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.event.world.DayChangeEvent;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Custom day cycling
 */
public class CustomDayCycleTask extends BukkitRunnable {

	private static final Map<UUID, CustomDayCycleTask> TASKS = new HashMap<>();

	private final World world;
	private final Supplier<Double> timeIncrementRule;
	private double fakeTime = 0;

	public CustomDayCycleTask(@NotNull World world, @NotNull Supplier<@NotNull Double> timeIncrementRule) {
		this.world = world;
		this.timeIncrementRule = timeIncrementRule;

		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);

		TASKS.put(world.getUID(), this);
		MiscMe.scheduler().sync(this, 0L, 1L);
	}

	@Override
	public void run() {
		double incrementTimeBy = timeIncrementRule.get();

		fakeTime += incrementTimeBy;
		long adding = (long) fakeTime;
		if (adding == 0) return;

		fakeTime -= adding;

		long preDay = world.getFullTime() / 24_000;
		world.setFullTime(world.getFullTime() + adding);
		long afterDay = world.getFullTime() / 24_000;
		if (preDay != afterDay) new DayChangeEvent(world).callEvent();
	}

	/**
	 * Stops the custom day cycle task
	 *
	 * @param world world
	 */
	public static void stopDayCycle(@NotNull World world) {
		CustomDayCycleTask task = TASKS.remove(world.getUID());
		if (task != null)
			task.cancel();
	}

}
