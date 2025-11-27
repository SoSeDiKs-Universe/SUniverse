package me.sosedik.requiem.listener.effect;

import me.sosedik.requiem.dataset.RequiemEffects;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.jspecify.annotations.NullMarked;

/**
 * Attrition level is synced to player's level
 */
@NullMarked
public class AttritionEffectLevel implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLevel(PlayerLevelChangeEvent event) {
		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		player.removePotionEffect(RequiemEffects.ATTRITION);

		int level = Math.clamp(5 - event.getNewLevel(), 0, 5);
		player.addPotionEffect(new PotionEffect(RequiemEffects.ATTRITION, PotionEffect.INFINITE_DURATION, level));
		PossessingPlayer.checkPossessedExtraItems(player);
	}

}
