package me.sosedik.trappednewbie.listener.entity;

import me.sosedik.moves.listener.movement.CrawlingMechanics;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.utilizer.util.MetadataUtil;
import org.bukkit.Particle;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.tag.DamageTypeTags;
import org.jspecify.annotations.NullMarked;

/**
 * Creepers (unless charged or forced) don't explode on crawling players
 */
@NullMarked
public class CreepersLoveCrawlers implements Listener {

	private static final String WAITING_META = "waiting";
	private static final String FORCE_BOOM_META = "force_explosion";

	@EventHandler(ignoreCancelled = true)
	public void onPrime(ExplosionPrimeEvent event) {
		if (!(event.getEntity() instanceof Creeper creeper)) return;
		if (shouldBoom(creeper)) return;

		if (isWaiting(creeper)) {
			event.setCancelled(true);
			return;
		}

		if (!(creeper.getTarget() instanceof Player player)) return;
		if (!CrawlingMechanics.isCrawling(player)) return;
		for (Player nearbyPlayer : player.getLocation().getNearbyEntitiesByType(Player.class, 5)) {
			if (!CrawlingMechanics.isCrawling(nearbyPlayer))
				return;
		}

		event.setCancelled(true);
		startWait(creeper, player);
	}

	@EventHandler
	public void onHurt(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Creeper entity)) return;

		if (DamageTypeTags.IS_FIRE.isTagged(event.getDamageSource().getDamageType())) {
			event.setDamage(0);
			if (entity.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE)) return;
			if (!entity.isIgnited())
				entity.setIgnited(true);
			return;
		}

		if (isWaiting(entity)) {
			forceBoom(entity);
			entity.explode();
		}
	}

	private boolean isWaiting(Creeper creeper) {
		return MetadataUtil.hasMetadata(creeper, WAITING_META);
	}

	private void startWait(Creeper creeper, Player player) {
		MetadataUtil.setMetadata(creeper, WAITING_META, true);

		TrappedNewbie.scheduler().sync(task -> {
			if (shouldStopWaiting(creeper, player)) {
				MetadataUtil.removeMetadata(creeper, WAITING_META);
				return true;
			}

			if (!CrawlingMechanics.isCrawling(player)) {
				MetadataUtil.removeMetadata(creeper, WAITING_META);
				if (creeper.getWorld() == player.getWorld() && player.getLocation().distanceSquared(creeper.getLocation()) < 9)
					creeper.explode();
				return true;
			}

			creeper.getWorld().spawnParticle(Particle.HEART, creeper.getEyeLocation(), 1, 0.7, 0.5, 0.7);

			return false;
		}, 30L, 20L);
	}

	private boolean shouldStopWaiting(Creeper creeper, Player player) {
		return !creeper.isValid()
			|| creeper.isPowered()
			|| !player.isOnline()
			|| player.isDead()
			|| creeper.getWorld() != player.getWorld()
			|| player.getLocation().distanceSquared(creeper.getLocation()) > 25;
	}

	private void forceBoom(Creeper creeper) {
		MetadataUtil.setMetadata(creeper, FORCE_BOOM_META, true);
	}

	private boolean shouldBoom(Creeper creeper) {
		return creeper.isPowered()
				|| MetadataUtil.hasMetadata(creeper, FORCE_BOOM_META)
				|| ShearableCreepers.isSheared(creeper);
	}

}
