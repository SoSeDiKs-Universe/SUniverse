package me.sosedik.miscme.listener.entity;

import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Chickens lay eggs upon breeding instead of baby chickens
 */
@NullMarked
public class ChickensBreedEggs implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onChickenSpawn(CreatureSpawnEvent event) {
		if (event.getEntityType() != EntityType.CHICKEN) return;
		if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.BREEDING) return;

		event.setCancelled(true);
		Location loc = event.getLocation();
		loc.getWorld().dropItemNaturally(loc, ItemStack.of(Material.EGG, MathUtil.getRandomIntInRange(1, 3)));
		loc.getWorld().playSound(loc, Sound.ENTITY_CHICKEN_EGG, 1F, (float) Math.random() * 0.4F + 0.8F);
	}

}
