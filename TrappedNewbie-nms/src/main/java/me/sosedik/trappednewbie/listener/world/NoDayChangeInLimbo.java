package me.sosedik.trappednewbie.listener.world;

import me.sosedik.miscme.api.event.world.DayChangeEvent;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.Utilizer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Limbo world always has day 0
 */
@NullMarked
public class NoDayChangeInLimbo implements Listener {

	@EventHandler
	public void onDayChange(DayChangeEvent event) {
		World world = event.getWorld();
		if (Utilizer.limboWorld() == world)
			world.setFullTime(0L);
	}

}
