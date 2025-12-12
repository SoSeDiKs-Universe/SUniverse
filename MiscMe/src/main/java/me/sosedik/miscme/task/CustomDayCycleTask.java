package me.sosedik.miscme.task;

import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.api.event.world.DayChangeEvent;
import org.bukkit.GameRules;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Custom day cycling
 */
@NullMarked
public class CustomDayCycleTask extends BukkitRunnable {

	private static final Map<UUID, CustomDayCycleTask> TASKS = new HashMap<>();

	private final World world;
	private final Supplier<Double> timeIncrementRule;
	private double fakeTime = 0;

	public CustomDayCycleTask(World world, Supplier<Double> timeIncrementRule) {
		this.world = world;
		this.timeIncrementRule = timeIncrementRule;

		world.setGameRule(GameRules.ADVANCE_TIME, false);

		TASKS.put(world.getUID(), this);
		MiscMe.scheduler().sync(this, 0L, 1L);
	}

	@Override
	public void run() {
		double incrementTimeBy = this.timeIncrementRule.get();

		this.fakeTime += incrementTimeBy;
		long adding = (long) this.fakeTime;
		if (adding == 0) return;

		this.fakeTime -= adding;

		long preDay = this.world.getFullTime() / 24_000;
		this.world.setFullTime(this.world.getFullTime() + adding);
		long afterDay = this.world.getFullTime() / 24_000;
		if (preDay != afterDay) new DayChangeEvent(this.world).callEvent();
	}

	/**
	 * Stops the custom day cycle task
	 *
	 * @param world world
	 */
	public static void stopDayCycle(World world) {
		CustomDayCycleTask task = TASKS.remove(world.getUID());
		if (task != null)
			task.cancel();
	}

}
