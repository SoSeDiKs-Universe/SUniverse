package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.resourcelib.dataset.RLibItemTags;
import org.bukkit.entity.AbstractSkeleton;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

/**
 * Milk adds buffs to skeletons
 */
@NullMarked
public class MilkHelpsSkeletons implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent event) {
		if (!RLibItemTags.MILK_DRINKABLES.isTagged(event.getItem().getType())) return;

		LivingEntity possessed = PossessingPlayer.getPossessed(event.getPlayer());
		if (possessed == null) return;
		if (!isSkeletonEntity(possessed)) return;

		possessed.setFireTicks(0);
		possessed.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 30 * 20, 0));
	}

	private boolean isSkeletonEntity(LivingEntity entity) {
		return entity instanceof AbstractSkeleton
			|| entity.getType() == EntityType.SKELETON_HORSE
			|| entity.getType() == EntityType.WITHER;
	}

}
