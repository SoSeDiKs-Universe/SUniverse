package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.requiem.api.event.player.PlayerPossessedCuredEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GoodAsNewAdvancement implements Listener {

	@EventHandler
	public void onCure(PlayerPossessedCuredEvent event) {
		if (!(event.getEntity() instanceof Zombie)) return;

		Player player = event.getPlayer();
		if (TrappedNewbieAdvancements.GOOD_AS_NEW.awardAllCriteria(player))
			TrappedNewbieAdvancements.BASICS_TAB.switchTab(player);
	}

}
