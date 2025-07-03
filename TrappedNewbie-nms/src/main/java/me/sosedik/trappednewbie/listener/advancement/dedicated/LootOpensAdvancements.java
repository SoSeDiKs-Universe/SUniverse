package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LootOpensAdvancements implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLoot(LootGenerateEvent event) {
		if (event.isPlugin()) return;
		if (event.getInventoryHolder() == null) return;
		if (!(event.getEntity() instanceof Player player)) return;

		TrappedNewbieAdvancements.LOOT_10.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_100.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_500.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_1000.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_2500.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_5K.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_10K.awardNextCriterion(player);
	}

}
