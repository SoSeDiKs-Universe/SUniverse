package me.sosedik.trappednewbie.listener.thirst;

import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractCow;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Goat;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;

/**
 * Cows produce milk, drink it
 */
@NullMarked
public class DrinkableCows implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrink(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (!(event.getRightClicked() instanceof Ageable mob)) return;
		if (!(mob instanceof AbstractCow) && !(mob instanceof Goat)) return;
		if (!mob.isAdult()) return;
		if (mob.getTarget() != null) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;
		if (!player.getInventory().getItemInMainHand().isEmpty()) return;
		if (!player.getInventory().getItemInOffHand().isEmpty()) return;

		player.swingMainHand();
		player.emitSound(Sound.ENTITY_GENERIC_DRINK, 1F, 1F);
		ThirstyPlayer.of(player).addThirst(ThirstData.of(ItemStack.of(Material.MILK_BUCKET)));
		EntityType mobType = mob.getType();
		if (mobType == EntityType.COW || mobType == EntityType.GOAT) {
			Collection<PotionEffect> effects = player.getActivePotionEffects();
			for (PotionEffect effect : effects) {
				if (player.removePotionEffect(effect.getType(), EntityPotionEffectEvent.Cause.MILK))
					break;
			}
			if (mob instanceof Goat goat)
				mob.emitSound(goat.isScreaming() ? Sound.ENTITY_GOAT_SCREAMING_MILK : Sound.ENTITY_GOAT_MILK, 1F, 1F);
			else
				mob.emitSound(Sound.ENTITY_COW_MILK, 1F, 1F);
		} else if (mob instanceof MushroomCow cow) {
			if (cow.hasEffectsForNextStew()) {
				player.addPotionEffects(cow.getEffectsForNextStew());
				cow.clearEffectsForNextStew();
				mob.emitSound(Sound.ENTITY_MOOSHROOM_SUSPICIOUS_MILK, 1F, 1F);
			} else {
				mob.emitSound(Sound.ENTITY_MOOSHROOM_MILK, 1F, 1F);
			}
		}
	}

}
