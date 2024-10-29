package me.sosedik.miscme.listener.entity;

import me.sosedik.miscme.MiscMe;
import org.bukkit.Sound;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Wool can be burnt on sheep
 */
public class SheepBurnableWool implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCombust(@NotNull EntityCombustEvent event) {
		if (event.getDuration() < 2) return;
		if (!(event.getEntity() instanceof Sheep sheep)) return;
		if (!sheep.readyToBeSheared()) return;

		MiscMe.scheduler().sync(() -> {
			if (sheep.getFireTicks() <= 0) return;
			if (sheep.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) return;

			sheep.setSheared(true);
			sheep.emitSound(Sound.BLOCK_FIRE_EXTINGUISH, 0.6F, 0.5F);
			sheep.emitSound(Sound.BLOCK_FIRE_EXTINGUISH, 0.6F, 0.5F);
		}, 40L);
	}

}
