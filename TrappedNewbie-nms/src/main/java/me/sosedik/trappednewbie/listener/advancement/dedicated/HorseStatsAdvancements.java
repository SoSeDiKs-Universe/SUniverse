package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.miscme.listener.player.PlayerSpeedTracker;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Advancements related to horse stats
 */
public class HorseStatsAdvancements implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (!event.hasExplicitlyChangedBlock()) return;

		Player player = event.getPlayer();
		if (!(player.getVehicle() instanceof Horse horse)) return;
		if (horse.hasRider()) return;

		AttributeInstance attribute = horse.getAttribute(Attribute.MOVEMENT_SPEED);
		if (attribute == null) return;

		double movingBps = PlayerSpeedTracker.getSpeed(player);
		if (movingBps == 0) return;

		double statBps = attribute.getValue() * 42.16;
//		if (statBps >= 9 && movingBps >= 9) TrappedNewbieAdvancements.HORSE_SPEED_1.awardAllCriteria(player);
//		if (statBps >= 11 && movingBps >= 11) TrappedNewbieAdvancements.HORSE_SPEED_2.awardAllCriteria(player);
//		if (statBps >= 13 && movingBps >= 13) TrappedNewbieAdvancements.HORSE_SPEED_3.awardAllCriteria(player);
//		if (statBps >= 14.1 && movingBps >= 14.1) TrappedNewbieAdvancements.HORSE_SPEED_4.awardAllCriteria(player);
	}

}
