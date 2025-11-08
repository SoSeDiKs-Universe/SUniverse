package me.sosedik.utilizer.listener.item;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import me.sosedik.kiterino.event.player.PlayerPutItemInBundleEvent;
import me.sosedik.utilizer.api.event.player.PlayerPlaceItemEvent;
import me.sosedik.utilizer.dataset.UtilizerTags;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Some items shouldn't be dropped
 */
@NullMarked
public class NotDroppableItems implements Listener {

	private static final List<NotDroppableRule> RULES = new ArrayList<>();

	static {
		addRule(new NotDroppableRule((entity, item) -> UtilizerTags.NOT_DROPPABLE.isTagged(item.getType())));
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onMove(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;

		Inventory clickedInventory = event.getClickedInventory();
		if (clickedInventory == null) return;

		ItemStack item;
		if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
			if (event.getSlot() != event.getRawSlot()) return; // Player inventory
			int slot = event.getHotbarButton();
			if (slot == -1) {
				item = player.getInventory().getItemInOffHand();
			} else {
				item = player.getInventory().getItem(slot);
			}
		} else if (event.isShiftClick()) {
			item = event.getCurrentItem();
		} else {
			if (clickedInventory == player.getInventory()) return;
			item = event.getCursor();
		}
		if (ItemStack.isEmpty(item)) return;

		boolean crafts = player.getOpenInventory().getType() == InventoryType.CRAFTING;

		for (NotDroppableRule rule : RULES) {
			if (crafts && rule.allowCrafts) continue;
			if (rule.exclusions != null && player.getOpenInventory().getTopInventory().getHolder() != null && rule.exclusions.test(player.getOpenInventory().getTopInventory().getHolder())) continue;
			if (!rule.rule.test(player, item)) continue;

			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onMove(InventoryDragEvent event) {
		if (!(event.getWhoClicked() instanceof Player player)) return;

		boolean crafts = player.getOpenInventory().equals(event.getView());
		for (ItemStack item : event.getNewItems().values()) {
			for (NotDroppableRule rule : RULES) {
				if (crafts && rule.allowCrafts) continue;
				if (rule.exclusions != null && event.getView().getTopInventory().getHolder() != null && rule.exclusions.test(event.getView().getTopInventory().getHolder())) continue;
				if (!rule.rule.test(player, item)) continue;

				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onClick(InventoryClickEvent event) {
		ItemStack currentItem = event.getCurrentItem();
		if (ItemStack.isEmpty(currentItem)) return;

		if (UtilizerTags.MATERIAL_AIR.isTagged(currentItem.getType()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAcquire(PlayerInventorySlotChangeEvent event) {
		if (event.getSlot() == event.getRawSlot()) return; // Not a player inventory

		ItemStack currentItem = event.getPlayer().getInventory().getItem(event.getSlot());
		if (ItemStack.isEmpty(currentItem)) return;
		if (!UtilizerTags.MATERIAL_AIR.isTagged(currentItem.getType())) return;

		currentItem.setAmount(0);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		if (UtilizerTags.MATERIAL_AIR.isTagged(item.getType())) return;

		for (NotDroppableRule rule : RULES) {
			if (!rule.rule.test(event.getPlayer(), item)) continue;

			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onDrop(EntityDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		if (!UtilizerTags.MATERIAL_AIR.isTagged(item.getType())) return;

		for (NotDroppableRule rule : RULES) {
			if (!rule.rule.test(event.getEntity(), item)) continue;

			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPickup(ItemSpawnEvent event) {
		ItemStack item = event.getEntity().getItemStack();
		for (NotDroppableRule rule : RULES) {
			if (!rule.rule.test(event.getEntity(), item)) continue;

			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDeath(EntityDeathEvent event) {
		event.getDrops().removeIf(item -> {
			for (NotDroppableRule rule : RULES) {
				if (!rule.rule.test(event.getEntity(), item)) continue;

				return true;
			}
			return false;
		});
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(PlayerItemFrameChangeEvent event) {
		if (event.getAction() != PlayerItemFrameChangeEvent.ItemFrameChangeAction.PLACE) return;

		ItemStack item = event.getItemStack();
		if (item.isEmpty()) return;

		for (NotDroppableRule rule : RULES) {
			if (rule.allowPlace) continue;
			if (!rule.rule.test(event.getPlayer(), item)) continue;

			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlace(PlayerPlaceItemEvent event) {
		for (NotDroppableRule rule : RULES) {
			if (rule.allowPlace) continue;
			if (!rule.rule.test(event.getPlayer(), event.getItem())) continue;

			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onBundle(PlayerPutItemInBundleEvent event) {
		for (NotDroppableRule rule : RULES) {
			if (rule.allowPlace) continue;
			if (!rule.rule.test(event.getPlayer(), event.getItem())) continue;

			event.setCancelled(true);
			return;
		}
	}

	public static void addRule(NotDroppableRule rule) {
		RULES.add(rule);
	}

	public static class NotDroppableRule {

		private @Nullable Predicate<InventoryHolder> exclusions;
		private final BiPredicate<Entity, ItemStack> rule;
		private boolean allowCrafts = false;
		private boolean allowPlace = false;

		public NotDroppableRule(BiPredicate<Entity, ItemStack> rule) {
			this.rule = rule;
		}

		public NotDroppableRule exclude(Predicate<InventoryHolder> exclusions) {
			this.exclusions = exclusions;
			return this;
		}

		public NotDroppableRule withAllowedCrafts() {
			this.allowCrafts = true;
			return this;
		}

		public NotDroppableRule withAllowedPlace() {
			this.allowPlace = true;
			return this;
		}

	}

}
