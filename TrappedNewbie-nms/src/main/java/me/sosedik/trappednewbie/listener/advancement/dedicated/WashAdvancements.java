package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Advancements for washing items
 */
// MCCheck: 1.21.10, new washable items
@NullMarked
public class WashAdvancements implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onWash(CauldronLevelChangeEvent event) {
		if (!isWashing(event.getReason())) return;
		if (!(event.getEntity() instanceof Player player)) return;

		TrappedNewbieAdvancements.WASH_10.awardNextCriterion(player);
		TrappedNewbieAdvancements.WASH_50.awardNextCriterion(player);
		TrappedNewbieAdvancements.WASH_250.awardNextCriterion(player);
		TrappedNewbieAdvancements.WASH_1K.awardNextCriterion(player);
		TrappedNewbieAdvancements.WASH_5K.awardNextCriterion(player);
		TrappedNewbieAdvancements.WASH_10K.awardNextCriterion(player);
	}

	private boolean isWashing(CauldronLevelChangeEvent.ChangeReason changeReason) {
		return changeReason == CauldronLevelChangeEvent.ChangeReason.ARMOR_WASH
				|| changeReason == CauldronLevelChangeEvent.ChangeReason.BANNER_WASH
				|| changeReason == CauldronLevelChangeEvent.ChangeReason.SHULKER_WASH;
	}

}
