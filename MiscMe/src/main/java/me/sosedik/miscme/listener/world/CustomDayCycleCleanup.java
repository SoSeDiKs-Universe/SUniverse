package me.sosedik.miscme.listener.world;

import me.sosedik.miscme.task.CustomDayCycleTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Stops custom day cycle task when the world is unloaded
 */
@NullMarked
public class CustomDayCycleCleanup implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWorldUnload(WorldUnloadEvent event) {
		CustomDayCycleTask.stopDayCycle(event.getWorld());
	}

}
