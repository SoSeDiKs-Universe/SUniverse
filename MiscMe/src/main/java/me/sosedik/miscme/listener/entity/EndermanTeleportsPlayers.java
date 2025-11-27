package me.sosedik.miscme.listener.entity;

import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.utilizer.util.LocationUtil;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Enderman can teleport unreachable players
 */
@NullMarked
public class EndermanTeleportsPlayers implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Enderman enderman)) return;
		if (!(event.getDamager() instanceof LivingEntity damager)) return;
		if (enderman.getTarget() != null && enderman.getTarget() != damager) return;

		Block block = damager.getEyeLocation().getBlock();
		if (!LocationUtil.isTrulySolid(enderman, block)) {
			block = block.getRelative(BlockFace.UP);
			double minBlockY = LocationUtil.getMinYPoint(block);
			if (block.getLocation().getBlockY() + minBlockY > enderman.getLocation().getY() + enderman.getHeight()) return;
		}

		// TP methods pick one random block, which could easily be obstructed, so try to do so multiple times
		// Not ideal and has possibility of checking the same block multiple times, but oh well
		int tries = 15;
		while (tries-- > 0)
			enderman.teleportTowards(damager);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEndermanTeleport(EntityTeleportEvent event) {
		if (!(event.getEntity() instanceof Enderman enderman)) return;
		if (enderman.getTarget() == null) return;
		if (enderman.getLastDamageCause() != null && enderman.getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

		LivingEntity target = enderman.getTarget();
		if (enderman.getWorld() != target.getWorld()) return;
		if (Math.random() > 0.5) return;

		Block destinationBlock;
		// Switch places if far to confuse
		if (target.getLocation().distanceSquared(enderman.getLocation()) > 75) {
			destinationBlock = enderman.getLocation().getBlock();
			event.setTo(target.getLocation());
		}
		// Move player with enderman if close
		else if (event.getTo() != null) {
			destinationBlock = event.getTo().getBlock();
		} else {
			return;
		}

		while (!destinationBlock.getType().isAir())
			destinationBlock = destinationBlock.getRelative(BlockFace.UP);

		int destY = destinationBlock.getLocation().getBlockY();
		int targetY = target.getLocation().getBlockY();
		if (Math.abs(targetY - destY) > 10) return;

		target.teleport(destinationBlock.getLocation().center(), PlayerTeleportEvent.TeleportCause.ENDER_PEARL, TeleportFlag.Relative.values());
		target.emitSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1F, 1F);
	}

}
