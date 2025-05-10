package me.sosedik.trappednewbie.listener.advancement.dedicated;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Advancements for eating food
 */
@NullMarked
public class FoodConsumingAdvancement implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEat(PlayerItemConsumeEvent event) {
		if (isFood(event.getItem()))
			increaseStats(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onStatIncrement(PlayerStatisticIncrementEvent event) {
		if (event.getStatistic() != Statistic.CAKE_SLICES_EATEN) return;

		Player player = event.getPlayer();
		int diff = event.getNewValue() - event.getPreviousValue();
		for (int i = 0; i < diff; i++)
			increaseStats(player);
	}

	private void increaseStats(Player player) {
		TrappedNewbieAdvancements.LOOT_10.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_100.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_500.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_1000.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_2500.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_5000.awardNextCriterion(player);
		TrappedNewbieAdvancements.LOOT_10000.awardNextCriterion(player);
	}

	private boolean isFood(ItemStack item) {
		return item.hasData(DataComponentTypes.FOOD);
	}

}
