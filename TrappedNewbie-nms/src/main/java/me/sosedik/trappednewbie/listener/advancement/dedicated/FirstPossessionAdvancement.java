package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.requiem.api.event.player.PlayerStartPossessingEntityEvent;
import me.sosedik.requiem.api.event.player.PlayerStopGhostingEvent;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FirstPossessionAdvancement implements Listener {

	@EventHandler
	public void onPosses(PlayerStartPossessingEntityEvent event) {
		TrappedNewbieAdvancements.FIRST_POSSESSION.awardAllCriteria(event.getPlayer());
	}

	// In case player may have skipped the possession
	@EventHandler
	public void onRevive(PlayerStopGhostingEvent event) {
		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player))
			TrappedNewbieAdvancements.FIRST_POSSESSION.awardAllCriteria(player);
	}

}
