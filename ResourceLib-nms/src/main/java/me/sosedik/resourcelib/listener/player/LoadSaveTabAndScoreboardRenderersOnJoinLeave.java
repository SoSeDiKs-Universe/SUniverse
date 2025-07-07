package me.sosedik.resourcelib.listener.player;

import me.sosedik.resourcelib.feature.ScoreboardRenderer;
import me.sosedik.resourcelib.feature.TabRenderer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Loads and saves tab and scoreboard renderers
 */
@NullMarked
public class LoadSaveTabAndScoreboardRenderersOnJoinLeave implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		TabRenderer.of(player).run();
		ScoreboardRenderer.of(player).run();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		TabRenderer.removePlayer(player);
		ScoreboardRenderer.removePlayer(player);
	}

}
