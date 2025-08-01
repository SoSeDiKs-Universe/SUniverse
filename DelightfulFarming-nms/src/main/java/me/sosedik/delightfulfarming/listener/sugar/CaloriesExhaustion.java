package me.sosedik.delightfulfarming.listener.sugar;

import me.sosedik.delightfulfarming.feature.sugar.SugarEater;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CaloriesExhaustion implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onExhaust(EntityExhaustionEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		var sugarEater = SugarEater.of(player);
		sugarEater.onExhaustion(event.getExhaustion());
	}

}
