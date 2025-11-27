package me.sosedik.miscme.listener.player;

import me.sosedik.utilizer.util.ScoreboardUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;

/**
 * Hide player name tags
 */
@NullMarked
public class HidePlayerNameTags implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Team team = ScoreboardUtil.getPlayerTeam(player);
		team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (onlinePlayer == player) continue;

			team.addPlayer(onlinePlayer);
			Team otherTeam = ScoreboardUtil.getPlayerTeam(onlinePlayer);
			otherTeam.addPlayer(player);
		}
	}

}
