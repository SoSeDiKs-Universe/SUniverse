package me.sosedik.utilizer.listener.player;

import me.sosedik.utilizer.util.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;

/**
 * Remove scoreboard entries of offline players
 */
@NullMarked
public class CleanupPlayerScoreboards implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			if (onlinePlayer == player) return;

			Team team = ScoreboardUtil.getPlayerTeam(onlinePlayer);
			team.removePlayer(player);
		});
	}

}
