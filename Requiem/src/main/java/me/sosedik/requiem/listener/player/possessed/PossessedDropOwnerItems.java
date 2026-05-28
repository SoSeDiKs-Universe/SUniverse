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

		List<ItemStack> newDrops = Arrays.stream(rider.getInventory().getContents())
			.filter(item -> {
				if (ItemStack.isEmpty(item)) return false;
				if (item.hasEnchant(Enchantment.VANISHING_CURSE)) return false;

				float dropChance = PossessingOverMobs.getPossessedSoulboundItemDropChance(item, true);
				return dropChance >= 1F || Math.random() <= dropChance;
			})
			.toList();

		drops.addAll(newDrops);
	}

}
