package me.sosedik.requiem.listener.player.ghost;

import me.sosedik.requiem.feature.GhostyPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Ghosts have infinite night vision
 */
public class GhostsKeepNightVision implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEffectRemove(@NotNull EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		PotionEffect effect = event.getOldEffect();
		if (effect == null) return;
		if (!PotionEffectType.NIGHT_VISION.equals(effect.getType())) return;
		if (effect.getDuration() != PotionEffect.INFINITE_DURATION) return;
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(true);
	}

}
