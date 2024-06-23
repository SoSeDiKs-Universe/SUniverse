package me.sosedik.requiem.listener.player;

import me.sosedik.kiterino.event.player.PlayerLoadsProjectileEvent;
import me.sosedik.kiterino.event.player.PlayerStartUsingItemEvent;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.task.GoingThroughWallsTask;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Ghosts can phase through walls
 */
public class GhostsPhaseThroughWalls implements Listener {

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBow(@NotNull EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!GhostyPlayer.isGhost(player)) return;

		event.setCancelled(true);
		player.updateInventory();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(@NotNull PlayerInteractEvent event) {
		if (event.useItemInHand() != Event.Result.DENY) return;
		if (event.getClickedBlock() == null) return;

		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;
		if (!isBow(player, EquipmentSlot.HAND) && !isBow(player, EquipmentSlot.OFF_HAND)) return;

		event.setCancelled(false);
	}

	private boolean isBow(@NotNull Player player, @NotNull EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		return item.getType() == Material.BOW;
	}

	@EventHandler
	public void onLoad(@NotNull PlayerLoadsProjectileEvent event) {
		if (event.isFiringAllowed()) return;
		if (event.getWeapon().getType() != Material.BOW) return;

		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;

		event.setProjectile(null);
		event.setFiringAllowed(true);
	}

	@EventHandler
	public void onUse(@NotNull PlayerStartUsingItemEvent event) {
		Player player = event.getPlayer();
		if (!GhostyPlayer.isGhost(player)) return;

		ItemStack item = event.getItem();
		if (item.getType() != Material.BOW) return;

		Requiem.scheduler().sync(() -> {
			if (item.isSimilar(player.getActiveItem()))
				new GoingThroughWallsTask(player, item);
		}, 1L);
	}

}
