package me.sosedik.trappednewbie.listener.player;

import me.sosedik.kiterino.event.entity.EntityItemConsumeEvent;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.dataset.RLibItemTags;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import org.bukkit.Tag;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

/**
 * Milk adds buffs to skeletons
 */
@NullMarked
public class MilkHelpsSkeletons implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConsume(EntityItemConsumeEvent event) {
		if (!hasMilk(event.getItem())) return;

		LivingEntity entity = event.getEntity();
		if (!isSkeletonEntity(entity)) return;

		onConsume(entity);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent event) {
		if (!hasMilk(event.getItem())) return;

		LivingEntity possessed = PossessingPlayer.getPossessed(event.getPlayer());
		if (possessed == null) return;
		if (!isSkeletonEntity(possessed)) return;

		onConsume(possessed);
	}

	private boolean hasMilk(ItemStack item) {
		return RLibItemTags.MILK_DRINKABLES.isTagged(item.getType())
			|| ThirstData.of(item).drinkType() == ThirstData.DrinkType.MILK;
	}

	public void onConsume(LivingEntity entity) {
		entity.setFireTicks(0);
		entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 30 * 20, 0));
	}

	private boolean isSkeletonEntity(LivingEntity entity) {
		return Tag.ENTITY_TYPES_SKELETONS.isTagged(entity.getType());
	}

}
