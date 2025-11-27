package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Advancements for obtaining levels
 */
@NullMarked
public class LevelAdvancements implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLevelUp(PlayerLevelChangeEvent event) {
		int level = event.getNewLevel();
		if (level < event.getOldLevel()) return;

		Player player = event.getPlayer();
		if (GhostyPlayer.isGhost(player)) return;

		if (level >= 30) TrappedNewbieAdvancements.LEVEL_30.awardAllCriteria(player);
		if (level >= 100) TrappedNewbieAdvancements.LEVEL_100.awardAllCriteria(player);
		if (level >= 300) TrappedNewbieAdvancements.LEVEL_300.awardAllCriteria(player);
		if (level >= 1000) TrappedNewbieAdvancements.LEVEL_1000.awardAllCriteria(player);
		if (level >= 2500) TrappedNewbieAdvancements.LEVEL_2500.awardAllCriteria(player);
		if (level >= 5000) TrappedNewbieAdvancements.LEVEL_5000.awardAllCriteria(player);
	}

}
