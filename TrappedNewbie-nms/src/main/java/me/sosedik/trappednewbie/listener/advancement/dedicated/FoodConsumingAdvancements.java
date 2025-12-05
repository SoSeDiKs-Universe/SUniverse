package me.sosedik.trappednewbie.listener.advancement.dedicated;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

/**
 * Advancements for eating food
 */
@NullMarked
public class FoodConsumingAdvancements implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEat(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		if (!isFood(item)) return;

		increaseEatStats(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMiracleDrink(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		if (!UtilizerTags.POISON_CURES.isTagged(item.getType())) return;

		Player player = event.getPlayer();
		if (player.getHealth() > 1) return;
		if (!player.hasPotionEffect(PotionEffectType.POISON)) return;

		TrappedNewbieAdvancements.MIRACLE_DRINK.awardAllCriteria(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onStatIncrement(PlayerStatisticIncrementEvent event) {
		if (event.getStatistic() != Statistic.CAKE_SLICES_EATEN) return;

		Player player = event.getPlayer();
		int diff = event.getNewValue() - event.getPreviousValue();
		for (int i = 0; i < diff; i++)
			increaseEatStats(player);
	}

	private void increaseEatStats(Player player) {
		TrappedNewbieAdvancements.EAT_200.awardNextCriterion(player);
		TrappedNewbieAdvancements.EAT_1K.awardNextCriterion(player);
		TrappedNewbieAdvancements.EAT_2500.awardNextCriterion(player);
		TrappedNewbieAdvancements.EAT_5K.awardNextCriterion(player);
		TrappedNewbieAdvancements.EAT_10K.awardNextCriterion(player);
		TrappedNewbieAdvancements.EAT_25K.awardNextCriterion(player);
		TrappedNewbieAdvancements.EAT_50K.awardNextCriterion(player);
	}

	private boolean isFood(ItemStack item) {
		return item.hasData(DataComponentTypes.FOOD);
	}

}
