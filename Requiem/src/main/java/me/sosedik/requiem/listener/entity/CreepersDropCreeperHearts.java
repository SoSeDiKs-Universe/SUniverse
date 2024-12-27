package me.sosedik.requiem.listener.entity;

import me.sosedik.requiem.dataset.RequiemItems;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Charged creepers drop their hearts when killed by a player
 */
public class CreepersDropCreeperHearts implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Creeper creeper)) return;
		if (!creeper.isPowered()) return;
		if (creeper.getKiller() == null) return;

		event.getDrops().add(ItemStack.of(RequiemItems.CREEPER_HEART));
	}

}
