package me.sosedik.trappednewbie.listener.player;

import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.trappednewbie.api.item.VisualArmor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Drop possessor's items outside vanilla inventory on possessed death
 */
@NullMarked
public class ExtraPossessedDrops implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		Player rider = entity.getRider();
		if (rider == null) return;
		if (PossessingPlayer.getPossessed(rider) != entity) return;

		List<ItemStack> drops = event.getDrops();
		var visualArmor = VisualArmor.of(rider);
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (!visualArmor.hasItem(slot)) continue;

			ItemStack item = visualArmor.getItem(slot);
			if (!item.isEmpty()) drops.add(item);
			visualArmor.setItem(slot, null);
		}
	}

}
