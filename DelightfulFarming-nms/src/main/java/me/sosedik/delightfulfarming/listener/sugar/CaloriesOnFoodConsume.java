package me.sosedik.delightfulfarming.listener.sugar;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import me.sosedik.delightfulfarming.feature.sugar.SugarEater;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Increase calories by eating food
 */
@NullMarked
public class CaloriesOnFoodConsume implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		if (!item.hasData(DataComponentTypes.FOOD)) return;

		FoodProperties data = item.getData(DataComponentTypes.FOOD);
		assert data != null;
		SugarEater.of(event.getPlayer()).addCalories(data);
	}

}
