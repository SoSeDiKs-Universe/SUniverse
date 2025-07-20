package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.dataset.TrappedNewbieTags;
import me.sosedik.utilizer.dataset.UtilizerTags;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * Throwable blocks do damage and bounce of water
 */
public class ThrowableRockBehavior implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLaunch(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof Snowball snowball)) return;

		ItemStack projectile = snowball.getItem();
		if (!TrappedNewbieTags.ROCKS.isTagged(projectile.getType())) return;

		TrappedNewbie.scheduler().sync(task -> {
			if (!snowball.isValid()) return true;

			Block block = snowball.getLocation().getBlock();
			if (!LocationUtil.isWatery(block)) return false;
			if (LocationUtil.isWatery(block.getRelative(BlockFace.UP))) return false;

			Vector velocity = snowball.getVelocity();
			if (velocity.clone().normalize().setY(0).length() < 0.38) return true;

			double y = Math.abs(velocity.getY()) * 0.9;
			velocity.multiply(0.98).setY(y);
			snowball.setVelocity(velocity);

			return false;
		}, 1L, 2L);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityHit(ProjectileHitEvent event) {
		if (!(event.getHitEntity() instanceof LivingEntity target)) return;
		if (!(event.getEntity() instanceof Snowball snowball)) return;

		ItemStack projectile = snowball.getItem();
		if (projectile.getType() == TrappedNewbieItems.BALL_OF_MUD) {
			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 5 * 20, 2));
		} else if (TrappedNewbieTags.ROCKS.isTagged(projectile.getType())) {
			LivingEntity shooter = snowball.getShooter() instanceof LivingEntity livingEntity ? livingEntity : null;
			DamageSource.Builder builder = DamageSource.builder(DamageType.THROWN).withDirectEntity(snowball).withDamageLocation(snowball.getLocation());
			if (shooter != null) builder = builder.withCausingEntity(shooter);
			target.damage(1, builder.build());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockHit(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Snowball snowball)) return;

		Block block = event.getHitBlock();
		if (block == null) return;

		ItemStack projectile = snowball.getItem();
		if (!TrappedNewbieTags.ROCKS.isTagged(projectile.getType())) return;

		if (!UtilizerTags.FRAGILE_BLOCKS.isTagged(block.getType())) {
			block.emitSound(Sound.BLOCK_STONE_HIT, 1F, 1F);
			return;
		}

		block.breakNaturally(projectile, true, true);
	}

}
