package me.sosedik.trappednewbie.listener.effect;

import me.sosedik.moves.api.event.PlayerStartFallingEvent;
import me.sosedik.moves.api.event.PlayerStopFallingEvent;
import me.sosedik.moves.listener.movement.PlayerFallTicker;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

/**
 * No fall damage with bouncy effect
 */
@NullMarked
public class BouncyEffectHandler implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFall(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (player.isGliding()) return;
		if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
		if (!player.hasPotionEffect(TrappedNewbieEffects.BOUNCY)) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onFall(PlayerStartFallingEvent event) {
		handleStick(event.getPlayer());
	}

	@EventHandler
	public void onLand(PlayerStopFallingEvent event) {
		handleLanding(event.getPlayer());
	}

	public static void handleLanding(Player player) {
		float fallDistance = PlayerFallTicker.getStoredFallDistance(player);
		if (fallDistance <= 13) return;
		if (!player.hasPotionEffect(TrappedNewbieEffects.BOUNCY)) return;

		Vector velocity = player.getVelocity().multiply(1.15);
		velocity.setY(Math.max(0, PlayerFallTicker.getStoredVelocity(player).getY() * -0.8));
		player.setVelocity(velocity);
		player.emitSound(Sound.BLOCK_SLIME_BLOCK_FALL, 1F, 1F);
		player.getWorld().spawnParticle(Particle.ITEM_SLIME, player.getLocation(), 20);
	}

	public static void handleStick(Player player) {
		if (!player.hasPotionEffect(TrappedNewbieEffects.BOUNCY)) return;
		if (!shouldStick(player)) return;

		Vector velocity = player.getVelocity();
		velocity.setY(Math.min(-1, -PlayerFallTicker.getStoredPreVelocity(player).getY()));
		player.setVelocity(velocity);
		player.emitSound(Sound.BLOCK_SLIME_BLOCK_FALL, 1F, 1F);
		player.getWorld().spawnParticle(Particle.ITEM_SLIME, player.getEyeLocation(), 20);
	}

	private static boolean shouldStick(Player player) {
		if (player.getPose() != Pose.STANDING) return false;

		Location upperLoc = player.getLocation().shiftTowards(BlockFace.UP, player.getHeight() + 0.1);
		Block upperBlock = upperLoc.getBlock();
		if (!upperBlock.isSolid()) return false;

		return player.collidesAt(upperLoc);
	}

}
