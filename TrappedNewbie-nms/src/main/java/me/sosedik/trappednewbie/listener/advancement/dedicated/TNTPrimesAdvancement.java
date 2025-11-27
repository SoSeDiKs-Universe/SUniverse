package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.miscme.api.event.player.PlayerIgniteExplosiveMinecartEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class TNTPrimesAdvancement implements Listener {

	@EventHandler
	public void onPrime(EntitySpawnEvent event) {
		if (!(event.getEntity() instanceof TNTPrimed entity)) return;
		if (!(entity.getSource() instanceof Player player)) return;

		increaseStat(player);
	}

	@EventHandler
	public void onPrime(PlayerIgniteExplosiveMinecartEvent event) {
		increaseStat(event.getPlayer());
	}

	private void increaseStat(Player player) {
		TrappedNewbieAdvancements.IGNITE_250_TNT.awardNextCriterion(player);
	}

}
