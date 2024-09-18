package me.sosedik.miscme.listener.block;

import io.papermc.paper.event.entity.EntityInsideBlockEvent;
import me.sosedik.miscme.MiscMe;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

/**
 * Standing on a burning campfire is a bad idea
 */
public class CampfireSetsOnFire implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onCollide(@NotNull EntityInsideBlockEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (isFireExempt(event.getBlock(), entity)) return;

		MiscMe.scheduler().sync(() -> {
			if (isFireExempt(entity.getLocation().getBlock(), entity)) return;

			entity.setFireTicks(6 * 20);
		}, 30L);
	}

	private boolean isFireExempt(@NotNull Block block, @NotNull LivingEntity entity) {
		if (!(block.getBlockData() instanceof Campfire campfire)) return true;
		if (!campfire.isLit()) return true;
		if (campfire.isWaterlogged()) return true;
		if (entity.getFireTicks() > 60) return true;
		if (entity.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) return true;

		EntityEquipment equipment = entity.getEquipment();
		if (equipment != null) {
			ItemStack boots = equipment.getBoots();
			return !ItemStack.isEmpty(boots) && boots.hasEnchant(Enchantment.FROST_WALKER);
		}

		return false;
	}

}
