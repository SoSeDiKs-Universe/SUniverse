package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class QuickDeathAdvancements implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getPlayer();
		int ticksLived = player.getTicksLived();
		if (ticksLived <= 10 * 10) TrappedNewbieAdvancements.DIE_TWICE_WITHIN_10S.awardAllCriteria(player);
		if (ticksLived <= 5 * 10) TrappedNewbieAdvancements.DIE_TWICE_WITHIN_5S.awardAllCriteria(player);
	}

}
