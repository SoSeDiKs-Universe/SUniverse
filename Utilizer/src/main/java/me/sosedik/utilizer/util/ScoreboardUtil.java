package me.sosedik.utilizer.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

public class ScoreboardUtil {

	private ScoreboardUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Gets the player's unique scoreboard, creating one if missing
	 *
	 * @param player player
	 * @return player's unique scoreboard
	 */
	public static synchronized @NotNull Scoreboard getScoreboard(@NotNull Player player) {
		Scoreboard scoreboard = player.getScoreboard();
		if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
			scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
			player.setScoreboard(scoreboard);
		}
		return scoreboard;
	}

}
