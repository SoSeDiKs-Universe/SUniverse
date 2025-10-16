package me.sosedik.requiem.listener.player.possessed;

import com.google.common.base.Function;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import io.papermc.paper.event.player.PlayerArmSwingEvent;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import me.sosedik.requiem.feature.PossessingPlayer;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Possessed mimic actions of their possessor
 */
@NullMarked
public class PossessedMimicPossessor implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onTarget(EntityTargetEvent event) {
		if (!(event.getTarget() instanceof Player player)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		event.setTarget(entity);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSwing(PlayerArmSwingEvent event) {
		Player player = event.getPlayer();
		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		entity.swingHand(event.getHand());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSlotChange(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		ItemStack mainHandItem = player.getInventory().getItem(event.getNewSlot());
		entityEquipment.setItemInMainHand(mainHandItem);
		updateMainHandVisuals(entity, mainHandItem);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSwap(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();
		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		ItemStack mainHandItem = event.getMainHandItem();
		entityEquipment.setItemInMainHand(mainHandItem);
		updateMainHandVisuals(entity, mainHandItem);
		entityEquipment.setItemInOffHand(event.getOffHandItem());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHeldItemChange(PlayerInventorySlotChangeEvent event) {
		Player player = event.getPlayer();
		if (event.getSlot() != player.getInventory().getHeldItemSlot()) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		ItemStack mainHandItem = player.getInventory().getItemInMainHand();
		entityEquipment.setItemInMainHand(mainHandItem);
		updateMainHandVisuals(entity, mainHandItem);
	}

	// MCCheck: 1.21.10, new mobs carrying items
	private void updateMainHandVisuals(LivingEntity entity, @Nullable ItemStack item) {
		if (entity instanceof Enderman enderman) {
			if (item != null && item.getType().isBlock()) {
				enderman.setCarriedBlock(item.hasBlockData() ? item.getBlockData(item.getType()) : item.getType().createBlockData());
			} else {
				enderman.setCarriedBlock(null);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onArmorChange(EntityEquipmentChangedEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		EntityEquipment entityEquipment = entity.getEquipment();
		if (entityEquipment == null) return;

		event.getEquipmentChanges().forEach((slot, change) -> entityEquipment.setItem(slot, change.newItem()));
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onCombust(EntityCombustEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		event.setCancelled(true);

		int ticks = (int) (event.getDuration() * 20);
		if (entity.getFireTicks() >= ticks) return;

		entity.setFireTicks(ticks);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDamageAnotherEntity(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player)) return;
		if (!PossessingPlayer.isPossessing(player)) return;
		if (!(event.getEntity() instanceof LivingEntity damaged)) return;

		LivingEntity entity = PossessingPlayer.getPossessed(player);
		if (entity == null) return;

		if (entity == damaged) {
			event.setDamage(0);
			event.setCancelled(true);
			return;
		}

		Map<EntityDamageEvent.DamageModifier, Double> modifiers = new HashMap<>();
		modifiers.put(EntityDamageEvent.DamageModifier.BASE, event.getFinalDamage());
		Map<EntityDamageEvent.DamageModifier, Function<Double, Double>> modifierFunctions = new HashMap<>();
		modifierFunctions.put(EntityDamageEvent.DamageModifier.BASE, d -> d);
		var fakedEvent = new EntityDamageByEntityEvent(entity, damaged, event.getCause(), event.getDamageSource(), modifiers, modifierFunctions, event.isCritical());
		fakedEvent.callEvent();

		event.setDamage(fakedEvent.getFinalDamage());
		event.setCancelled(fakedEvent.isCancelled());
	}

}
