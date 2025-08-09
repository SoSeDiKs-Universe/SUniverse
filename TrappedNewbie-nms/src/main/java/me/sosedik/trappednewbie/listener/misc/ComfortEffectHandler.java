package me.sosedik.trappednewbie.listener.misc;

import me.sosedik.moves.api.event.PlayerStartSittingEvent;
import me.sosedik.moves.listener.movement.SittingMechanics;
import me.sosedik.trappednewbie.api.event.player.PlayerComfortEraseEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.potion.PotionEffect;
import org.jspecify.annotations.NullMarked;

/**
 * Controls Comfort effect
 */
@NullMarked
public class ComfortEffectHandler implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLay(PlayerBedEnterEvent event) {
		if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

		event.getPlayer().addPotionEffect(new PotionEffect(TrappedNewbieEffects.COMFORT, PotionEffect.INFINITE_DURATION, 1));
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBedComfort(PlayerComfortEraseEvent event) {
		if (event.getPlayer().isSleeping())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSit(PlayerStartSittingEvent event) {
		event.getPlayer().addPotionEffect(new PotionEffect(TrappedNewbieEffects.COMFORT, PotionEffect.INFINITE_DURATION, 0));
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onSitComfort(PlayerComfortEraseEvent event) {
		if (SittingMechanics.isSitting(event.getPlayer()))
			event.setCancelled(true);
	}

}
