package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.requiem.api.event.player.PlayerResurrectEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class IHateSandAdvancement implements Listener {

	@EventHandler
	public void onResurrect(PlayerResurrectEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity.getType() != EntityType.HUSK) return;

		TrappedNewbieAdvancements.I_HATE_SAND.awardAllCriteria(event.getPlayer());
	}

}
