package me.sosedik.trappednewbie.listener.thirst;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieDamageTypes;
import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

/**
 * Buckets have fluids, fluids are drinkable
 */
@NullMarked
public class DrinkableBuckets implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrink(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		if (item.getType() != Material.LAVA_BUCKET) return;

		Player player = event.getPlayer();
		if (player.isImmuneToFire()) return;
		if (player.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) return;

		TrappedNewbie.scheduler().sync(() -> {
			player.setFireTicks(Integer.MAX_VALUE);
			player.damage(Integer.MAX_VALUE, DamageSource.builder(TrappedNewbieDamageTypes.LAVA_DRINK).build());
		}, 1L); // Make sure the event passes
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

		Player player = event.getPlayer();
		if (!isDrinkableBucket(player.getInventory().getItemInMainHand())) return;

		player.startUsingItem(EquipmentSlot.HAND);
	}

	private boolean isDrinkableBucket(ItemStack item) {
		return item.getType() == Material.WATER_BUCKET || item.getType() == Material.LAVA_BUCKET;
	}

}
