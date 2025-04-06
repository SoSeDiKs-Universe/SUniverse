package me.sosedik.utilizer.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ScoreboardUtil {

	private ScoreboardUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final String PLAYER_COMMAND_TAG = "command";

	/**
	 * Gets the player's unique scoreboard, creating one if missing
	 *
	 * @param player player
	 * @return player's unique scoreboard
	 */
	public static synchronized Scoreboard getScoreboard(Player player) {
		Scoreboard scoreboard = player.getScoreboard();
		if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
			scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
			player.setScoreboard(scoreboard);
		}
		return scoreboard;
	}

	/**
	 * Gets the player's unique scoreboard team, creating one if missing
	 *
	 * @param player player
	 * @return player's unique scoreboard team
	 */
	public static synchronized Team getPlayerTeam(Player player) {
		Scoreboard scoreboard = getScoreboard(player);
		Team team = scoreboard.getPlayerTeam(player);
		if (team == null) {
			team = scoreboard.registerNewTeam(PLAYER_COMMAND_TAG);
			team.addPlayer(player);
		}
		return team;
	}

}
