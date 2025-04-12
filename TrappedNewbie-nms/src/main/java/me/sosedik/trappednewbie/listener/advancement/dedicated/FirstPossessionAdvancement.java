package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.requiem.api.event.player.PlayerStartPossessingEntityEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FirstPossessionAdvancement implements Listener {

	@EventHandler
	public void onPosses(PlayerStartPossessingEntityEvent event) {
		TrappedNewbieAdvancements.FIRST_POSSESSION.awardAllCriteria(event.getPlayer());
	}

}
