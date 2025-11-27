package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.List;

/**
 * Possessed mobs drop their owner's inventory on death
 */
@NullMarked
public class PossessedDropOwnerItems implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		Player rider = entity.getRider();
		if (rider == null) return;
		if (PossessingPlayer.getPossessed(rider) != entity) return;

		List<ItemStack> drops = event.getDrops();
		drops.clear();
		List<ItemStack> newDrops = Arrays.stream(rider.getInventory().getContents())
			.filter(item -> !ItemStack.isEmpty(item))
			.filter(item -> !item.hasEnchant(Enchantment.VANISHING_CURSE))
			.toList();
		drops.addAll(newDrops);
	}

}
