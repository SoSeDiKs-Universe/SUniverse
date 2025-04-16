package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.requiem.api.event.player.PlayerPossessedCuredEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GoodAsNewAdvancement implements Listener {

	@EventHandler
	public void onCure(PlayerPossessedCuredEvent event) {
		if (!(event.getEntity() instanceof Zombie)) return;

		TrappedNewbieAdvancements.GOOD_AS_NEW.awardAllCriteria(event.getPlayer());
	}

}
