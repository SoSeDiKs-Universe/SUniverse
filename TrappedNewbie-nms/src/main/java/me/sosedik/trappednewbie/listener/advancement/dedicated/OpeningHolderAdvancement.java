package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

/**
 * Per-world welcome message in Requiem Holder
 */
public class OpeningHolderAdvancement implements Listener {

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		TrappedNewbieAdvancements.OPENING_HOLDER.resend(event.getPlayer());
	}

}
