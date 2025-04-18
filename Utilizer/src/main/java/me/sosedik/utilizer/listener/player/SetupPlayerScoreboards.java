package me.sosedik.utilizer.listener.player;

import me.sosedik.utilizer.util.GlowingUtil;
import me.sosedik.utilizer.util.ScoreboardUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.jspecify.annotations.NullMarked;

/**
 * Register per-player scoreboards if missing,
 * and create glowing teams
 */
@NullMarked
public class SetupPlayerScoreboards implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Scoreboard scoreboard = ScoreboardUtil.getScoreboard(player);
		registerToScoreboard(scoreboard);
	}

	private void registerToScoreboard(Scoreboard scoreboard) {
		for (NamedTextColor color : NamedTextColor.NAMES.values()) {
			GlowingUtil.getGlowTeam(scoreboard, color);
		}
	}

}
