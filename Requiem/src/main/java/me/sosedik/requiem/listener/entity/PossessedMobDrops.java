package me.sosedik.requiem.listener.entity;

import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.requiem.listener.player.possessed.PossessingOverMobs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Possessed mobs drop their stored inventory upon death
 */
public class PossessedMobDrops implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		Player rider = entity.getRider();
		if (rider != null && PossessingPlayer.isPossessingSoft(rider)) return;

		List<ItemStack> storedItems = PossessingPlayer.getStoredItems(entity);
		storedItems.removeIf(item -> {
			if (item.hasEnchant(Enchantment.VANISHING_CURSE)) return true;

			float dropChance = PossessingOverMobs.getPossessedSoulboundItemDropChance(item, true);
			return dropChance < 1F && Math.random() > dropChance;
		});
		event.getDrops().addAll(storedItems);
	}

}
