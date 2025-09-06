package me.sosedik.requiem.listener.item;

import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Golem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Stopping possessment with host revocator
 */
public class HostRevocatorBodyLeaving implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBow(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!ItemStack.isType(event.getBow(), RequiemItems.HOST_REVOCATOR)) return;
		if (event.getForce() < 1F) return;

		event.setCancelled(true);

		LivingEntity possessed = PossessingPlayer.getPossessed(player);
		if (possessed == null || possessed instanceof Golem) {
			PossessingPlayer.stopPossessing(player);
			GhostyPlayer.markGhost(player);
		} else {
			possessed.damage(Double.MAX_VALUE, DamageSource.builder(DamageType.GENERIC_KILL).build());
		}
	}

}
