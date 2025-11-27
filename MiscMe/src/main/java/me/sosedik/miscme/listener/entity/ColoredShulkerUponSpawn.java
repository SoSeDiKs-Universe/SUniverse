package me.sosedik.miscme.listener.entity;

import de.tr7zw.nbtapi.NBT;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.DyeColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.jspecify.annotations.NullMarked;

import java.util.Random;

/**
 * Shulkers spawn colored
 */
@NullMarked
public class ColoredShulkerUponSpawn implements Listener {

	private static final Random RANDOM = new Random();

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onSpawn(CreatureSpawnEvent event) {
		checkShulker(event.getEntity());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onSpawn(EntitiesLoadEvent event) {
		event.getEntities().forEach(this::checkShulker);
	}

	private void checkShulker(Entity entity) {
		if (!EntityUtil.isNaturallySpawned(entity)) return;
		if (!(entity instanceof Shulker shulker)) return;
		if (shulker.getColor() != null) return;
		if (NBT.getPersistentData(shulker, nbt -> nbt.hasTag(DyeableShulkers.CLEARED_SHULKER_KEY))) return;

		DyeColor color = DyeColor.values()[RANDOM.nextInt(DyeColor.values().length)];
		shulker.setColor(color);
	}

}
