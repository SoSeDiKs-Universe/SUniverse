package me.sosedik.trappednewbie.listener.player;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Players spawn as ghosts on first join
 */
@NullMarked
public class StartAsGhost implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (TrappedNewbieAdvancements.FIRST_POSSESSION.isDone(player)) return;

		GhostyPlayer.markGhost(player);
	}

}
