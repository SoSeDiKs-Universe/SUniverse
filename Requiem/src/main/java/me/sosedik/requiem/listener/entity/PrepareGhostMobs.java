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
	public static final String GHOST_TEAM_ID = "Ghost";

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(@NotNull PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Scoreboard scoreboard = ScoreboardUtil.getScoreboard(player);

		Team team = scoreboard.registerNewTeam(GHOST_TEAM_ID);
		team.setCanSeeFriendlyInvisibles(true);
		team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
		team.addEntity(player);
		getGhostTem().getEntries().forEach(team::addEntry);

		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			Team hide = ScoreboardUtil.getScoreboard(onlinePlayer).getTeam(GHOST_TEAM_ID);
			if (hide != null)
				hide.addEntity(player);
			team.addEntity(onlinePlayer);
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(@NotNull PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			Team hide = onlinePlayer.getScoreboard().getTeam(GHOST_TEAM_ID);
			if (hide != null)
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

	private static @NotNull Team getGhostTem() {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		Team ghosts = scoreboard.getTeam(GHOST_TEAM_ID);
		if (ghosts == null)
			return scoreboard.registerNewTeam(GHOST_TEAM_ID);
		return ghosts;
	}

	/**
	 * Makes the player no longer appear as a ghost
	 *
	 * @param player player
	 * @param keepSelf whether the player should see themselves as a ghost
	 */
	public static void hideSelfGhost(@NotNull Player player, boolean keepSelf) {
		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			if (keepSelf && onlinePlayer == player) return;
			Team hide = ScoreboardUtil.getScoreboard(onlinePlayer).getTeam(GHOST_TEAM_ID);
			if (hide != null)
				hide.removeEntity(player);
		});
	}

	/**
	 * Makes the player appear as a ghost (see-through)
	 *
	 * @param player player
	 */
	public static void applySelfGhost(@NotNull Player player) {
		Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
			Team hide = ScoreboardUtil.getScoreboard(onlinePlayer).getTeam(GHOST_TEAM_ID);
			if (hide != null)
				hide.addEntity(player);
		});
	}

	public static void unregisterGhosts() {
		Team ghost = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(GHOST_TEAM_ID);
		if (ghost != null)
			ghost.unregister();
	}

}
