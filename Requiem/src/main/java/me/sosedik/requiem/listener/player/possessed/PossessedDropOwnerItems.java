package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Possessed mobs drop their owner's inventory on death
 */
public class PossessedDropOwnerItems implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		Player rider = entity.getRider();
		if (rider == null) return;
		if (PossessingPlayer.getPossessed(rider) != entity) return;

		List<ItemStack> drops = event.getDrops();
		drops.clear();
		drops.addAll(Arrays.stream(rider.getInventory().getContents()).filter(Objects::nonNull).toList());
	}

}
