package me.sosedik.requiem.listener.player;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Players turn into ghosts upon death instead of respawning
 */
public class DeathMakesGhosts implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onRespawn(@NotNull PlayerRespawnEvent event) {
		if (event.getRespawnFlags().contains(PlayerRespawnEvent.RespawnFlag.END_PORTAL)) return;

		Player player = event.getPlayer();
		event.setRespawnLocation(player.getLocation());
		GhostyPlayer.markGhost(player);
	}

	@EventHandler
	public void onPostRespawn(@NotNull PlayerPostRespawnEvent event) {
		if (event.getRespawnFlags().contains(PlayerRespawnEvent.RespawnFlag.END_PORTAL)) return;

		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;

		EntityUtil.clearTargets(player);

		// TODO hm?
//		if (MetadataUtil.removeMetadata(player, SuicideCommand.SUICIDE_META) != null) // ToDo: use normal damage API once merged in Paper
//			BlockUtil.runRtp(player).thenRun(() -> FreeFall.setLeaping(player, true));
	}

	// Ghosts should never die, but in case they do...
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDeath(@NotNull PlayerDeathEvent event) {
		if (!GhostyPlayer.isGhost(event.getPlayer())) return;

		event.getDrops().clear();
		event.setDroppedExp(0);
		event.setShouldDropExperience(false);
	}

}
