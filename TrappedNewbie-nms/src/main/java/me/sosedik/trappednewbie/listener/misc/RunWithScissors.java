package me.sosedik.trappednewbie.listener.misc;

import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.utilizer.api.message.Messenger;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Extra actions for Purpur's damage sources
 */
@NullMarked
public class RunWithScissors implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getDamageSource() instanceof CraftDamageSource damageSource)) return;
		if (!damageSource.getHandle().isScissors()) return;

		HudMessenger.of(player).displayMessage(Messenger.messenger(player).getMessage("damage.run_with_scissors"));
	}

}
