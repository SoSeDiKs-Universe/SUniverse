package me.sosedik.trappednewbie.listener.effect;

import me.sosedik.requiem.dataset.RequiemEffects;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.trappednewbie.impl.blockstorage.FlowerPotBlockStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.listener.item.NotDroppableItems;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * Attrition limits the player
 */
@NullMarked
public class AttritionLimitations implements Listener {

	static {
		NotDroppableItems.addRule(new NotDroppableItems.NotDroppableRule(
			(entity, item) -> {
				if (!(entity instanceof Player player)) return false;
				if (!player.hasPotionEffect(RequiemEffects.ATTRITION)) return false;

				PotionEffect potionEffect = player.getPotionEffect(RequiemEffects.ATTRITION);
				if (potionEffect == null) return false;
				if (potionEffect.getAmplifier() < 4) return false;

				HudMessenger.of(player).displayMessage(Messenger.messenger(player).getMessage("attrition.too_high"));
				return true;
			})
			.exclude(AttritionLimitations::isAllowedInventory)
			.withAllowedCrafts()
			.withAllowedPlace()
		);
	}

	@EventHandler
	public void onLevel(PlayerLevelChangeEvent event) {
		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		int level = Math.min(event.getNewLevel(), 5);
		player.removePotionEffect(RequiemEffects.ATTRITION);
		player.addPotionEffect(new PotionEffect(RequiemEffects.ATTRITION, PotionEffect.INFINITE_DURATION, 5 - level));
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onOpen(InventoryOpenEvent event) {
		if (!(event.getPlayer() instanceof Player player)) return;
		if (!player.hasPotionEffect(RequiemEffects.ATTRITION)) return;

		PotionEffect potionEffect = player.getPotionEffect(RequiemEffects.ATTRITION);
		if (potionEffect == null) return;
		if (potionEffect.getAmplifier() < 3) return;

		InventoryHolder holder = event.getInventory().getHolder();
		if (holder != null && isAllowedInventory(holder)) return;

		event.setCancelled(true);
		HudMessenger.of(player).displayMessage(Messenger.messenger(player).getMessage("attrition.too_high"));
	}

	private static boolean isAllowedInventory(InventoryHolder holder) {
		return holder instanceof FlowerPotBlockStorage // It has GUI, but it's meant to be of primitive kind and there's no better way yet
			|| holder instanceof AbstractHorse; // Can't open player's own inventory otherwise
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPossessedDeath(EntityDeathEvent event) {
		LivingEntity entity = event.getEntity();
		Player rider = entity.getRider();
		if (rider == null) return;
		if (PossessingPlayer.getPossessed(rider) != entity) return;
		if (!rider.hasPotionEffect(RequiemEffects.ATTRITION)) return;

		PotionEffect potionEffect = rider.getPotionEffect(RequiemEffects.ATTRITION);
		if (potionEffect == null) return;
		if (potionEffect.getAmplifier() < 3) return;

		List<ItemStack> drops = event.getDrops();
		if (drops.isEmpty()) return;

		drops = new ArrayList<>(drops);
		event.getDrops().clear();
		for (ItemStack stack : drops) {
			int originalAmount = stack.getAmount();
			int remainingItems = 0;

			for (int i = 0; i < originalAmount; i++) {
				if (Math.random() > 0.6)
					remainingItems++;
			}

			if (remainingItems > 0) {
				ItemStack remainingStack = stack.asQuantity(remainingItems);
				event.getDrops().add(remainingStack);
			}
		}
	}

}
