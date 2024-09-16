package me.sosedik.requiem.listener.player.possessed;

import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.Material;
import org.bukkit.entity.AbstractSkeleton;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Milk adds buffs to skeletons
 */
public class MilkHelpsSkeletons implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConsume(@NotNull PlayerItemConsumeEvent event) {
		if (event.getItem().getType() != Material.MILK_BUCKET) return; // TODO milk bottle

		LivingEntity possessed = PossessingPlayer.getPossessed(event.getPlayer());
		if (possessed == null) return;
		if (!isSkeletonEntity(possessed)) return;

		possessed.setFireTicks(0);
		possessed.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 30 * 20, 0));
	}

	private boolean isSkeletonEntity(@NotNull LivingEntity entity) {
		return entity instanceof AbstractSkeleton
				|| entity.getType() == EntityType.SKELETON_HORSE
				|| entity.getType() == EntityType.WITHER;
	}

}
