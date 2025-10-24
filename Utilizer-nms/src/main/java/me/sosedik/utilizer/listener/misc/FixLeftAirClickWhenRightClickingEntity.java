package me.sosedik.utilizer.listener.misc;

import me.sosedik.kiterino.event.player.PlayerArmSwingFromServerEvent;
import me.sosedik.utilizer.Utilizer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Fixes calling {@link PlayerInteractEvent} when
 * hand swings because of unrelated causes
 */
@NullMarked
public class FixLeftAirClickWhenRightClickingEntity implements Listener {

	private static final Set<UUID> ON_COOLDOWN = new HashSet<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) return;
		if (event.useItemInHand() == Event.Result.DENY) return;

		Player player = event.getPlayer();
		if (!ON_COOLDOWN.contains(player.getUniqueId())) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		setCooldown(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractAtEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();
		if (player.isSneaking()) return;

		setCooldown(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player)) return;

		setCooldown(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(VehicleDamageEvent event) {
		if (!(event.getAttacker() instanceof Player player)) return;

		setCooldown(player);
	}

	@EventHandler
	public void onSwing(PlayerArmSwingFromServerEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		setCooldown(event.getPlayer());
	}

	private void setCooldown(Player player) {
		UUID uuid = player.getUniqueId();
		ON_COOLDOWN.add(uuid);
		Utilizer.scheduler().sync(() -> ON_COOLDOWN.remove(uuid), 2L);
	}

}
