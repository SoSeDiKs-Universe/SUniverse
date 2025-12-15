package me.sosedik.trappednewbie.listener.block;

import me.sosedik.requiem.impl.block.TombstoneBlockStorage;
import me.sosedik.trappednewbie.api.event.player.PlayerTargetBlockEvent;
import me.sosedik.trappednewbie.api.event.player.PlayerToolCheck;
import me.sosedik.trappednewbie.misc.BlockBreakTask;
import me.sosedik.utilizer.listener.BlockStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Compat between custom block breaking and player tombstones
 */
@NullMarked
public class BreakableTombstones implements Listener {

	public BreakableTombstones() {
		BlockBreakTask.addBreakingRule((task, seconds) -> {
			if (!(BlockStorage.getByLoc(task.getBlock()) instanceof TombstoneBlockStorage storage)) return null;
			if (!storage.isPlayerTombstone()) return null;

			return seconds == 0F ? 2F : seconds;
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTarget(PlayerTargetBlockEvent event) {
		if (!(BlockStorage.getByLoc(event.getBlock()) instanceof TombstoneBlockStorage storage)) return;
		if (!storage.isPlayerTombstone()) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onToolCheck(PlayerToolCheck event) {
		if (!(BlockStorage.getByLoc(event.getBlock()) instanceof TombstoneBlockStorage storage)) return;
		if (!storage.isPlayerTombstone()) return;

		event.setAllowed(true);
	}

}
