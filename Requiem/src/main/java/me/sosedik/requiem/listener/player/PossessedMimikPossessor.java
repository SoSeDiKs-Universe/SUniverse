package me.sosedik.requiem.listener.player;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Possessed mimik actions of their possessor
 */
public class PossessedMimikPossessor implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSwing(@NotNull PlayerArmSwingEvent event) {
		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		entity.swingHand(event.getHand());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSlotChange(@NotNull PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		entityEquipment.setItemInMainHand(player.getInventory().getItem(event.getNewSlot()));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSwap(@NotNull PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		entityEquipment.setItemInMainHand(event.getMainHandItem());
		entityEquipment.setItemInOffHand(event.getOffHandItem());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHeldItemChange(@NotNull PlayerInventorySlotChangeEvent event) {
		Player player = event.getPlayer();
		if (event.getRawSlot() != player.getInventory().getHeldItemSlot()) return;

		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		entityEquipment.setItemInMainHand(player.getInventory().getItemInMainHand());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onArmorChange(@NotNull PlayerArmorChangeEvent event) {
		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		entityEquipment.setItem(EquipmentSlot.valueOf(event.getSlotType().name()), event.getNewItem());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onItemDrop(@NotNull PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		entityEquipment.setItemInMainHand(null);
		Requiem.scheduler().sync(() -> entityEquipment.setItemInMainHand(player.getInventory().getItemInMainHand()), 1L);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCombust(@NotNull EntityCombustEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!PossessingPlayer.isPossessing(player)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		entity.setFireTicks((int) Math.max(event.getDuration() * 20, entity.getFireTicks()));
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamageAnotherEntity(@NotNull EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player)) return;
		if (!PossessingPlayer.isPossessing(player)) return;
		if (!(event.getEntity() instanceof LivingEntity damaged)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		Map<EntityDamageEvent.DamageModifier, Double> modifiers = new HashMap<>();
		modifiers.put(EntityDamageEvent.DamageModifier.BASE, event.getFinalDamage());
		var fakedEvent = new EntityDamageByEntityEvent(entity, damaged, event.getCause(), event.getDamageSource(), modifiers, new HashMap<>(), event.isCritical());
		fakedEvent.callEvent();

		event.setDamage(fakedEvent.getFinalDamage());
		event.setCancelled(fakedEvent.isCancelled());
	}

}