package me.sosedik.trappednewbie.listener.projectile;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import me.sosedik.trappednewbie.impl.item.modifier.TippedArrowPotionTypeModifier;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.SplashPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.jspecify.annotations.NullMarked;

/**
 * Splashable and lingering tipped arrows
 */
@NullMarked
public class VariedTippedArrows implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLaunch(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof Arrow arrow)) return;

		ItemStack item = arrow.getItemStack();
		if (item.getType() != Material.TIPPED_ARROW) return;

		var arrowPotionType = TippedArrowPotionTypeModifier.ArrowPotionType.fromItem(item);
		if (arrowPotionType == TippedArrowPotionTypeModifier.ArrowPotionType.POTION) return;

		arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onHit(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Arrow arrow)) return;

		ItemStack item = arrow.getItemStack();
		if (item.getType() != Material.TIPPED_ARROW) return;

		var arrowPotionType = TippedArrowPotionTypeModifier.ArrowPotionType.fromItem(item);
		if (arrowPotionType == TippedArrowPotionTypeModifier.ArrowPotionType.POTION) return;

//		// Prevent double-effect
//		if (event.getHitEntity() != null) {
//			event.setCancelled(true);
//			arrow.remove();
//			if (event.getHitEntity() instanceof LivingEntity hitEntity)
//				hitEntity.setArrowsInBody(hitEntity.getArrowsInBody() + 1, true);
//		}

		var potion = ItemStack.of(arrowPotionType == TippedArrowPotionTypeModifier.ArrowPotionType.SPLASH ? Material.SPLASH_POTION : Material.LINGERING_POTION);
		if (item.hasData(DataComponentTypes.POTION_CONTENTS)) {
			PotionContents data = item.getData(DataComponentTypes.POTION_CONTENTS);
			assert data != null;
			potion.setData(DataComponentTypes.POTION_CONTENTS, data);
		}
		if (arrowPotionType == TippedArrowPotionTypeModifier.ArrowPotionType.SPLASH) {
			arrow.getWorld().spawn(arrow.getLocation(), SplashPotion.class, entity -> {
				entity.setPotionMeta((PotionMeta) potion.getItemMeta());
				entity.setItem(potion);
				entity.setShooter(arrow.getShooter());
			}).splash();
		} else {
			arrow.getWorld().spawn(arrow.getLocation(), LingeringPotion.class, entity -> {
				entity.setPotionMeta((PotionMeta) potion.getItemMeta());
				entity.setItem(potion);
				entity.setShooter(arrow.getShooter());
			}).splash();
		}
	}

}
