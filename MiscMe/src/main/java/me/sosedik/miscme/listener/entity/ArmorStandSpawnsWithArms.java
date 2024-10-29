package me.sosedik.miscme.listener.entity;

import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Armor stands have arms when placed
 */
public class ArmorStandSpawnsWithArms implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onArmorStandSpawn(@NotNull EntitySpawnEvent event) {
		if (!(event.getEntity() instanceof ArmorStand armorStand)) return;
		if (!EntityUtil.isNaturallySpawned(armorStand)) return;

		armorStand.setArms(true);
	}

}
