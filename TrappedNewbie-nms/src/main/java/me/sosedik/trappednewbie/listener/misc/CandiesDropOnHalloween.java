package me.sosedik.trappednewbie.listener.misc;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import org.bukkit.Tag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Some mobs drop candies on Halloween
 */
// MCCheck: 1.21.10, new mods
@NullMarked
public class CandiesDropOnHalloween implements Listener {

	public static final String LOOTER_TAG = "looter";

	private static final Map<EntityType, ItemStack> drops = new HashMap<>();

	static {
		addDrop(ItemStack.of(TrappedNewbieItems.BLAZE_GOODIE_BAG), EntityType.BLAZE);
		addDrop(ItemStack.of(TrappedNewbieItems.CREEPER_GOODIE_BAG), EntityType.CREEPER);
		addDrop(ItemStack.of(TrappedNewbieItems.DROWNED_GOODIE_BAG), EntityType.DROWNED);
		addDrop(ItemStack.of(TrappedNewbieItems.ENDERMAN_GOODIE_BAG), EntityType.ENDERMAN);
		addDrop(ItemStack.of(TrappedNewbieItems.GHAST_GOODIE_BAG), EntityType.GHAST, EntityType.HAPPY_GHAST);
		addDrop(ItemStack.of(TrappedNewbieItems.GUARDIAN_GOODIE_BAG), EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN);
		addDrop(ItemStack.of(TrappedNewbieItems.PHANTOM_GOODIE_BAG), EntityType.PHANTOM);
		addDrop(ItemStack.of(TrappedNewbieItems.SKELETON_GOODIE_BAG), Tag.ENTITY_TYPES_SKELETONS.getValues());
		addDrop(ItemStack.of(TrappedNewbieItems.SLIME_GOODIE_BAG), EntityType.SLIME, EntityType.MAGMA_CUBE);
		addDrop(ItemStack.of(TrappedNewbieItems.SPIDER_GOODIE_BAG), EntityType.SPIDER, EntityType.CAVE_SPIDER);
		addDrop(ItemStack.of(TrappedNewbieItems.ZOMBIE_GOODIE_BAG), Tag.ENTITY_TYPES_ZOMBIES.getValues().stream().filter(type -> type != EntityType.DROWNED).toList());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (!entity.getWorld().isHalloweenSeason()) return;

		Player killer = entity.getKiller();
		if (killer == null) return;

		ItemStack drop = drops.get(entity.getType());
		if (drop == null) return;
		if (Math.random() > 0.25) return;

		drop = drop.clone();
		NBT.modify(drop, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setUUID(LOOTER_TAG, killer.getUniqueId()));

		event.getDrops().add(drop);
	}

	public static void addDrop(ItemStack drop, Collection<EntityType> types) {
		for (EntityType type : types)
			drops.put(type, drop);
	}

	public static void addDrop(ItemStack drop, EntityType... types) {
		for (EntityType type : types)
			drops.put(type, drop);
	}

}
