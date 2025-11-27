package me.sosedik.trappednewbie.listener.effect;

import me.sosedik.miscme.listener.player.FireExtinguishByHand;
import me.sosedik.miscme.listener.projectile.BurningProjectileCreatesFire;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

/**
 * Firefingers create fire
 */
@NullMarked
public class FirefingersFire implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHit(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player damager)) return;
		if (damager.isUnderWater()) return;
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (entity.getFireTicks() > 80) return;
		if (!damager.hasPotionEffect(TrappedNewbieEffects.FIREFINGERS)) return;

		entity.setFireTicks(80);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		if (player.isUnderWater()) return;
		if (!player.hasPotionEffect(TrappedNewbieEffects.FIREFINGERS)) return;
		if (!player.getInventory().getItem(event.getHand()).isEmpty()) return;

		Block block = event.getClickedBlock();
		if (block == null) return;
		if (!LocationUtil.isTrulySolid(player, block)) return;

		BlockFace blockFace = event.getBlockFace();
		block = block.getRelative(blockFace);
		if (!block.isEmpty()) return;

		FireExtinguishByHand.addFireImmunity(player, 10);
		BurningProjectileCreatesFire.createFireOrIgnite(block, blockFace, player, BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL);
	}

}
