package me.sosedik.delightfulfarming.listener.sugar;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import me.sosedik.delightfulfarming.feature.sugar.SugarEater;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Eat any food any time
 */
@NullMarked
public class AlwaysAllowEating implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		SugarEater sugarEater = SugarEater.of(player);
		if (!sugarEater.isActive()) return;

		player.setFoodLevel(sugarEater.getHungerPoints());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		SugarEater sugarEater = SugarEater.of(player);
		if (!sugarEater.isActive()) return;

		event.setFoodLevel(sugarEater.getHungerPoints());
		event.setCancelled(true);
	}

	@EventHandler
	public void onFoodLevelChange(PlayerPostRespawnEvent event) {
		Player player = event.getPlayer();
		SugarEater sugarEater = SugarEater.of(player);
		if (!sugarEater.isActive()) return;

		player.setFoodLevel(sugarEater.getHungerPoints());
	}

}
