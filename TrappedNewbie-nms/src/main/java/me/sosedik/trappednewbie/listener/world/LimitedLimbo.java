package me.sosedik.trappednewbie.listener.world;

import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Limits interactions in Limbo world
 */
@NullMarked
public class LimitedLimbo implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (shouldDeny(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractAtEntityEvent event) {
		if (shouldDeny(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (shouldDeny(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (shouldDeny(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity().getWorld() == TrappedNewbie.limboWorld())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onMount(EntityMountEvent event) {
		if (event.getEntity().getWorld() == TrappedNewbie.limboWorld())
			event.setCancelled(true);
	}

	private boolean shouldDeny(Player player) {
		return !player.getGameMode().isInvulnerable() && player.getWorld() == TrappedNewbie.limboWorld();
	}

}
