package me.sosedik.miscme.listener.entity;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.util.EntityUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.function.Consumer;

/**
 * More baby mobs
 */
public class MoreBabyMobs implements Listener {

	private static final double SPAWN_CHANCE = 0.05;
	private static final double SPEED_MODIFIER = 1.4;
	private static final String BABY_TAG = "baby";

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSpawn(EntitySpawnEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (entity instanceof Ageable) return;
		if (!EntityUtil.isNaturallySpawned(entity)) return;
		if (Math.random() > SPAWN_CHANCE) return;

		makeBaby(entity);
	}

	/**
	 * Makes the entity into its baby form
	 *
	 * @param entity entity
	 */
	public static void makeBaby(LivingEntity entity) {
		if (entity instanceof Ageable ageable) {
			ageable.setBaby();
			return;
		}

		modifyAttribute(entity, Attribute.SCALE, 0.5);
		modifyAttribute(entity, Attribute.MOVEMENT_SPEED, SPEED_MODIFIER);
		modifyAttribute(entity, Attribute.SNEAKING_SPEED, SPEED_MODIFIER);
		modifyAttribute(entity, Attribute.FLYING_SPEED, SPEED_MODIFIER);
		NBT.modifyPersistentData(entity, (Consumer<ReadWriteNBT>) nbt -> nbt.setBoolean(BABY_TAG, true));
	}

	private static void modifyAttribute(LivingEntity entity, Attribute attribute, double modifier) {
		AttributeInstance attributeInstance = entity.getAttribute(attribute);
		if (attributeInstance != null)
			attributeInstance.setBaseValue(attributeInstance.getBaseValue() * modifier);
	}

}
