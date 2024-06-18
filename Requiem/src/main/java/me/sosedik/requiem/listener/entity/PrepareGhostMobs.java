package me.sosedik.requiem.listener.entity;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.utilizer.util.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

/**
 * Implements ghost mobs
 */
public class PrepareGhostMobs implements Listener {

	/**
	 * Scoreboard team id used for "ghosts" mechanics
	 */
	private static final String GHOST_TEAM_ID = "Ghost";

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(@NotNull PlayerJoinEvent event) {
		Player player = event.getPlayer();

		Team team = getGhostTem(ScoreboardUtil.getScoreboard(player));
		team.addEntity(player);
		getGhostTem(Bukkit.getScoreboardManager().getMainScoreboard()).getEntries().forEach(team::addEntry);

		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			team.addEntity(onlinePlayer);
			Team hide = getGhostTem(ScoreboardUtil.getScoreboard(onlinePlayer));
			hide.addEntity(player);
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(@NotNull PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			Team hide = getGhostTem(onlinePlayer.getScoreboard());
			hide.removeEntity(player);
		});
	}

	@EventHandler
	public void onChat(AsyncChatEvent event) { // TODO remove
		Requiem.scheduler().sync(() -> {
			if (GhostyPlayer.isGhost(event.getPlayer())) {
				GhostyPlayer.clearGhost(event.getPlayer());
			} else {
				GhostyPlayer.markGhost(event.getPlayer());
			}
		});
	}

	private static @NotNull Team getGhostTem(@NotNull Scoreboard scoreboard) {
		Team team = scoreboard.getTeam(GHOST_TEAM_ID);
		if (team == null) {
			team = scoreboard.registerNewTeam(GHOST_TEAM_ID);
			team.setCanSeeFriendlyInvisibles(true);
			team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
		}
		return team;
	}

	/**
	 * Makes the player no longer appear as a ghost
	 *
	 * @param player player
	 * @param selfVisible whether the player should see themselves as a ghost
	 */
	public static void makeInvisible(@NotNull Player player, boolean selfVisible) {
		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			if (selfVisible && onlinePlayer == player) return;
			Team hide = getGhostTem(onlinePlayer.getScoreboard());
			hide.removeEntity(player);
		});
	}

	/**
	 * Makes the player appear as a ghost (see-through)
	 *
	 * @param player player
	 */
	public static void makeVisible(@NotNull Player player) { // TODO what's the point?
		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			Team team = getGhostTem(ScoreboardUtil.getScoreboard(onlinePlayer));
			team.addEntity(player);
		});
	}

	public static void unregisterGhosts() {
		Team ghost = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(GHOST_TEAM_ID);
		if (ghost != null)
			ghost.unregister();
	}

}
