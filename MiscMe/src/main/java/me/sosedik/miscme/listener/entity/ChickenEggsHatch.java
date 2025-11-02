package me.sosedik.miscme.listener.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Chicken eggs can hatch on despawn
 */
@NullMarked
public class ChickenEggsHatch implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEggLay(ItemDespawnEvent event) {
		ItemStack itemStack = event.getEntity().getItemStack();
		if (!Tag.ITEMS_EGGS.isTagged(itemStack.getType())) return;

		Location loc = event.getLocation();
		if (loc.getNearbyEntitiesByType(Chicken.class, 32, 128, 32).size() > 16) return;
		if (!loc.getNearbyEntitiesByType(Player.class, 0.5).isEmpty()) return;

		loc.getWorld().spawn(loc, Chicken.class, CreatureSpawnEvent.SpawnReason.EGG, chicken -> {
			chicken.setBaby();
			chicken.setVariant(getVariant(itemStack.getType()));
		});
	}

	private Chicken.Variant getVariant(Material type) {
		return switch (type) {
			case BLUE_EGG -> Chicken.Variant.COLD;
			case BROWN_EGG -> Chicken.Variant.WARM;
			default -> Chicken.Variant.TEMPERATE;
		};
	}

}
