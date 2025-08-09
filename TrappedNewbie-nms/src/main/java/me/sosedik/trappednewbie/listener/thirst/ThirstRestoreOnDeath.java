package me.sosedik.trappednewbie.listener.thirst;

import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Thirst restores on death
 */
@NullMarked
public class ThirstRestoreOnDeath implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onRespawn(PlayerRespawnEvent event) {
		if (event.getRespawnReason() != PlayerRespawnEvent.RespawnReason.DEATH) return;

		ThirstyPlayer.of(event.getPlayer()).setThirst(ThirstyPlayer.MAX_THIRST);
	}

}
