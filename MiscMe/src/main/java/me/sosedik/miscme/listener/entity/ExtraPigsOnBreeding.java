package me.sosedik.miscme.listener.entity;

import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.jspecify.annotations.NullMarked;

/**
 * More pigs are produced during breeding
 */
@NullMarked
public class ExtraPigsOnBreeding implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreed(EntityBreedEvent event) {
		if (event.getMother().getType() != EntityType.PIG) return;
		if (event.getFather().getType() != EntityType.PIG) return;

		LivingEntity child = event.getEntity();
		Location loc = child.getLocation();
		for (int i = 0; i < MathUtil.getRandomIntInRange(1, 5); i++) {
			child.copy().spawnAt(loc, CreatureSpawnEvent.SpawnReason.BREEDING);
			child.getWorld().spawn(loc, ExperienceOrb.class, orb -> orb.setExperience(MathUtil.getRandomIntInRange(1, 8)));
		}
	}

}
