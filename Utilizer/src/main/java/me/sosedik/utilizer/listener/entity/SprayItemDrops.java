package me.sosedik.utilizer.listener.entity;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.Utilizer;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

import java.util.Random;
import java.util.function.Consumer;

/**
 * Handles fake item drops
 */
@NullMarked
public class SprayItemDrops implements Listener {

	private static final String SPRAY_TAG = "spray";
	private static final Random RANDOM = new Random();

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSprayPickup(EntityPickupItemEvent event) {
		if (isSpray(event.getItem()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSprayHopper(InventoryPickupItemEvent event) {
		if (isSpray(event.getItem()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onSprayMerge(ItemMergeEvent event) {
		if (isSpray(event.getEntity()) || isSpray(event.getTarget()))
			event.setCancelled(true);
	}

	/**
	 * Spawns a new spray item
	 *
	 * @param loc location
	 * @param itemStack item
	 * @return dropped item
	 */
	public static Item spawnSpray(Location loc, ItemStack itemStack) {
		return spawnSpray(loc, itemStack, 15 + RANDOM.nextInt(30));
	}

	/**
	 * Spawns a new spray item
	 *
	 * @param loc location
	 * @param itemStack item
	 * @param lifeSpawn lifespan
	 * @return dropped item
	 */
	public static Item spawnSpray(Location loc, ItemStack itemStack, int lifeSpawn) {
		Item item = loc.getWorld().dropItem(loc, itemStack, drop -> {
			NBT.modifyPersistentData(drop, (Consumer<ReadWriteNBT>) nbt -> nbt.setBoolean(SPRAY_TAG, true));
			drop.setCanMobPickup(false);
			drop.setPersistent(false);
			drop.setVelocity(new Vector(RANDOM.nextInt(3) * 0.1, 0.25, RANDOM.nextInt(3) * 0.1));
		});
		Utilizer.scheduler().sync(item::remove, lifeSpawn);
		return item;
	}

	/**
	 * Checks whether the item is a spray item
	 *
	 * @param item item entity
	 * @return whether the item is a spray item
	 */
	public static boolean isSpray(Item item) {
		return NBT.getPersistentData(item, nbt -> nbt.getOrDefault(SPRAY_TAG, false));
	}

}
