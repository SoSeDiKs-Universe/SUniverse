package me.sosedik.requiem.listener.entity;

import me.sosedik.utilizer.util.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;

/**
 * Implements ghost mobs
 */
@NullMarked
public class PrepareGhostMobs implements Listener {

	/**
	 * Scoreboard team id used for "ghosts" mechanics
	 */
	private static final String GHOST_TEAM_ID = "Ghost";

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		Team team = ScoreboardUtil.getPlayerTeam(player);
		team.setCanSeeFriendlyInvisibles(true);
		getGhostTeam(Bukkit.getScoreboardManager().getMainScoreboard()).getEntries().forEach(team::addEntry);
	}

	/**
	 * Gets (or registers) ghost team from scoreboard
	 *
	 * @param scoreboard scoreboard
	 * @return ghost team
	 */
	private static Team getGhostTeam(Scoreboard scoreboard) {
		Team team = scoreboard.getTeam(GHOST_TEAM_ID);
		if (team == null) {
			team = scoreboard.registerNewTeam(GHOST_TEAM_ID);
			team.setCanSeeFriendlyInvisibles(true);
		}
		return team;
	}

	/**
	 * Makes the player no longer appear as a ghost
	 *
	 * @param player player
	 * @param selfVisible whether the player should see themselves as a ghost
	 */
	public static void hideVisibility(Player player, boolean selfVisible) {
		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			Team team = ScoreboardUtil.getPlayerTeam(onlinePlayer);
			if (onlinePlayer == player) {
				team.setCanSeeFriendlyInvisibles(selfVisible);
				return;
			}
			team.removePlayer(player);
		});
	}

	/**
	 * Makes other player visible for the specified player
	 *
	 * @param player player
	 * @param other player that should be visible
	 */
	public static void addVisible(Player player, Player other) {
		Team team = ScoreboardUtil.getPlayerTeam(player);
		team.addPlayer(other);
	}

	/**
	 * Unregisters ghosts scoreboard team
	 */
	public static void unregisterGhosts() {
		Team ghost = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(GHOST_TEAM_ID);
		if (ghost != null)
			ghost.unregister();
	}

}
