package me.sosedik.trappednewbie.listener.thirst;

import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

/**
 * Thirst decreases over time
 */
@NullMarked
public class ThirstDecrease implements Listener {

	@EventHandler
	public void onThirst(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		if (player.getFoodLevel() < event.getFoodLevel()) return;
		if (player.hasPotionEffect(PotionEffectType.HUNGER)) return;

//		var temperature = TemperaturedPlayer.of(player); // TODO temperature :>
//		if (temperature.hasAnyFlag(TempFlag.IN_WARM_RAIN, TempFlag.IN_COLD_RAIN)) return;

		if (Math.random() < 0.1)
			event.setCancelled(true);
		double chance = 0.7;
		int value = -1;
		if (player.getWorld().isUltraWarm())
			value -= 1;
		if (player.isInRain())
			chance -= 0.15;

//		int display = temperature.getDisplay(); // TODO temperature :>
//		if (display > 30)
//			chance = 1;
//		else if (display > 20)
//			chance += 0.2;
//		else if (display < -30)
//			chance = 0;
//		else if (display < -20)
//			chance -= 0.2;

		if (Math.random() < chance) {
			event.setCancelled(event.isCancelled() || Math.random() < 0.75);
			ThirstyPlayer.of(player).addThirst(value);
		}
	}

}
