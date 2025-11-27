package me.sosedik.trappednewbie.listener.effect;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * Baked potato is a hot potato during Halloween
 */
@NullMarked
public class HotPotatoHalloween implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSlotChange(PlayerInventorySlotChangeEvent event) {
		Player player = event.getPlayer();
		if (!player.getWorld().isHalloweenSeason()) return;

		ItemStack item = event.getNewItemStack();
		if (item.getType() != Material.BAKED_POTATO) return;
		if (player.hasPotionEffect(TrappedNewbieEffects.HOT_POTATO)) return;

		player.addPotionEffect(new PotionEffect(TrappedNewbieEffects.HOT_POTATO, 8 * 20, 0));
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEntityAttack(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (!(event.getDamager() instanceof LivingEntity entity)) return;
		if (entity.getEquipment() == null) return;

		if (!tryToPutPotato(entity, player, EquipmentSlot.OFF_HAND))
			tryToPutPotato(entity, player, EquipmentSlot.HAND);
	}

	private boolean tryToPutPotato(LivingEntity entity, Player player, EquipmentSlot slot) {
		EntityEquipment equipment = entity.getEquipment();
		if (equipment == null) return false;

		ItemStack hand = equipment.getItem(slot);
		if (hand.getType() != Material.BAKED_POTATO) return false;
		if (!player.getInventory().addItem(hand.asOne()).isEmpty()) return false;

		equipment.setItem(slot, hand.subtract());

		return true;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player)) return;
		if (!(event.getEntity() instanceof LivingEntity entity)) return;
		if (!player.getWorld().isHalloweenSeason()) return;
		if (!player.hasPotionEffect(TrappedNewbieEffects.HOT_POTATO)) return;

		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType() != Material.BAKED_POTATO) return;

		if (entity instanceof Player other) {
			if (!other.getInventory().addItem(item.asOne()).isEmpty()) return;
		} else if (entity.getEquipment() != null) {
			if (!tryToPutPotato(entity, item, EquipmentSlot.HAND) && !(hasOffHand(entity) && tryToPutPotato(entity, item, EquipmentSlot.OFF_HAND))) return;
		} else {
			return;
		}
		item.subtract();
		if (player.getInventory().contains(Material.BAKED_POTATO) || isBakedPotato(player.getItemOnCursor()) || isBakedPotato(player.getInventory().getItemInOffHand())) {
			player.addPotionEffect(new PotionEffect(TrappedNewbieEffects.HOT_POTATO, 10 * 20, 0));
		} else {
			player.removePotionEffect(TrappedNewbieEffects.HOT_POTATO);
		}
	}

	private boolean hasOffHand(LivingEntity entity) {
		EntityType type = entity.getType();
		return type != EntityType.FOX && type != EntityType.ALLAY;
	}

	private boolean isBakedPotato(ItemStack item) {
		return item.getType() == Material.BAKED_POTATO;
	}

	private boolean tryToPutPotato(LivingEntity entity, ItemStack item, EquipmentSlot slot) {
		EntityEquipment equipment = entity.getEquipment();
		assert equipment != null;
		ItemStack hand = equipment.getItem(slot);
		if (hand.getType() != Material.AIR) return false;

		equipment.setItem(slot, item.asOne());

		return true;
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (!player.getWorld().isHalloweenSeason()) return;

		ItemStack item = event.getItemDrop().getItemStack();
		if (item.getType() != Material.BAKED_POTATO) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onEat(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		if (!player.getWorld().isHalloweenSeason()) return;

		ItemStack item = event.getItem();
		if (item.getType() != Material.BAKED_POTATO) return;

		event.setCancelled(true);
		if (player.getFireTicks() < 80) player.setFireTicks(80);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onMove(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (event.getClickedInventory() == null) return;
		if (!player.getWorld().isHalloweenSeason()) return;

		InventoryAction action = event.getAction();
		if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
			// Allow moving inside player inventory
			if (event.getClickedInventory() == player.getInventory() && event.getInventory().getType() == InventoryType.CRAFTING) return;

			ItemStack item = event.getCurrentItem();
			if (!ItemStack.isType(item, Material.BAKED_POTATO)) return;
		} else if (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME) {
			if (event.getClickedInventory() == player.getInventory()) return;

			ItemStack item = event.getCursor();
			if (!ItemStack.isType(item, Material.BAKED_POTATO)) return;
		} else if (action == InventoryAction.DROP_ONE_CURSOR || action == InventoryAction.DROP_ALL_CURSOR) {
			ItemStack item = event.getCursor();
			if (!ItemStack.isType(item, Material.BAKED_POTATO)) return;
		} else if (action == InventoryAction.DROP_ONE_SLOT || action == InventoryAction.DROP_ALL_SLOT) {
			ItemStack item = event.getCurrentItem();
			if (!ItemStack.isType(item, Material.BAKED_POTATO)) return;
		} else {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onMove(InventoryDragEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (!player.getWorld().isHalloweenSeason()) return;

		ItemStack item = event.getOldCursor();
		if (!ItemStack.isType(item, Material.BAKED_POTATO)) return;

		Set<Integer> slots = event.getInventorySlots();
		for (int slot : event.getRawSlots()) {
			if (!slots.contains(slot)) continue; // If raw slot matches non-raw slot, then we are inside non-player inventory

			event.setCancelled(true);
			return;
		}
	}

}
