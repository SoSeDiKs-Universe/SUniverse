package me.sosedik.miscme.listener.misc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Don't reset time on time set command
 */
public class BetterTimeSetCommand implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTimeSet(@NotNull TimeSkipEvent event) {
		if (event.getSkipReason() != TimeSkipEvent.SkipReason.COMMAND) return;

		long skipAmount = event.getSkipAmount();
		if (skipAmount > 0) return;

		long currentTime = event.getWorld().getFullTime();
		if (currentTime <= 24000) return;

		long skipped = currentTime + skipAmount;
		if (skipped > 24000) return;

		skipped -= currentTime % 24000;
		event.setSkipAmount(skipped);
	}

}
