package me.sosedik.trappednewbie.listener.thirst;

import me.sosedik.essence.api.event.AsyncPlayerHealCommandEvent;
import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * /heal command restores thirst
 */
@NullMarked
public class HealRestoresThirst implements Listener {

	@EventHandler
	public void onHeal(AsyncPlayerHealCommandEvent event) {
		ThirstyPlayer.of(event.getPlayer()).setThirst(ThirstyPlayer.MAX_THIRST);
	}

}
