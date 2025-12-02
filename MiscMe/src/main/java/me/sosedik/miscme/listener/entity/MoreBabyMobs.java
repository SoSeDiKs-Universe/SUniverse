package me.sosedik.miscme.listener.entity;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.miscme.api.event.entity.EntityTurnBabyEvent;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.world.EntitiesLoadEvent;

import java.util.function.Consumer;

/**
 * More baby mobs
 */
public class MoreBabyMobs implements Listener {

	private static final double SPAWN_CHANCE = 0.05;
	private static final double SPEED_MODIFIER = 1.5;
	private static final String BABY_TAG = "baby";

	// Randomly spawn babies
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSpawn(EntitySpawnEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (!shouldBecomeBaby(entity)) return;

		makeBaby(entity);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSplit(EntityTransformEvent event) {
		if (!(event.getEntity() instanceof Slime slime)) return;
		if (!isNonVanillaBaby(slime)) return;

		event.getTransformedEntities().forEach(entity -> {
			if (entity instanceof Slime living)
				makeBaby(living);
		});
	}

	private boolean shouldBecomeBaby(LivingEntity entity) {
		if (NBT.get(entity, nbt -> (boolean) nbt.hasTag(BABY_TAG))) return true;
		return !(entity instanceof Ageable)
			&& EntityUtil.isNaturallySpawned(entity)
			&& Math.random() > SPAWN_CHANCE;
	}

	// Re-apply attributes to custom babies on load
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(EntitiesLoadEvent event) {
		for (Entity entity : event.getEntities()) {
			if (!(entity instanceof LivingEntity livingEntity)) continue;
			if (!isNonVanillaBaby(livingEntity)) continue;

			makeBaby(livingEntity);
		}
	}

	/**
	 * Makes the entity into its baby form
	 *
	 * @param entity entity
	 */
	public static boolean makeBaby(LivingEntity entity) {
		if (entity instanceof Ageable ageable) {
			ageable.setBaby();
			return true;
		}

		if (entity.getAttribute(Attribute.SCALE) == null) return false;
		if (!new EntityTurnBabyEvent(entity).callEvent()) return false;

		modifyAttribute(entity, Attribute.SCALE, 0.5);
		modifyAttribute(entity, Attribute.MOVEMENT_SPEED, SPEED_MODIFIER);
		modifyAttribute(entity, Attribute.SNEAKING_SPEED, SPEED_MODIFIER);
		modifyAttribute(entity, Attribute.FLYING_SPEED, SPEED_MODIFIER);
		NBT.modifyPersistentData(entity, (Consumer<ReadWriteNBT>) nbt -> nbt.setBoolean(BABY_TAG, true));
		return true;
	}

	/**
	 * Checks whether this entity is a non-vanilla baby
	 *
	 * @param entity entity
	 * @return whether this entity is a non-vanilla baby
	 */
	public static boolean isNonVanillaBaby(LivingEntity entity) {
		if (entity instanceof Ageable) return false;
		return NBT.getPersistentData(entity, nbt -> nbt.getOrDefault(BABY_TAG, false));
	}

	private static void modifyAttribute(LivingEntity entity, Attribute attribute, double modifier) {
		AttributeInstance attributeInstance = entity.getAttribute(attribute);
		if (attributeInstance == null) return;

		attributeInstance.setBaseValue(attributeInstance.getBaseValue() * modifier);
	}

}
