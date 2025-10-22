package me.sosedik.trappednewbie.listener.item;

import me.sosedik.kiterino.event.entity.EntityItemConsumeEvent;
import me.sosedik.kiterino.event.entity.ItemConsumeEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

/**
 * Fizzlers candies explode upon consumption
 */
@NullMarked
public class FizzlersCandyExplosion implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onUse(PlayerItemConsumeEvent event) {
		tryToUse(event);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onUse(EntityItemConsumeEvent event) {
		tryToUse(event);
	}

	public void tryToUse(ItemConsumeEvent event) {
		ItemStack item = event.getItem();
		if (item.getType() != TrappedNewbieItems.FIZZLERS_CANDY) return;

		LivingEntity entity = event.getEntity();

		if (entity instanceof Player player)
			player.setCooldown(item, 40);

		Vector velocity = entity.getVelocity();
		if (velocity.getY() < 2)
			entity.setVelocity(velocity.setY(2));
		entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 4, false, false, true));
		entity.getWorld().createExplosion(entity, 6F, false, false);
	}

}
