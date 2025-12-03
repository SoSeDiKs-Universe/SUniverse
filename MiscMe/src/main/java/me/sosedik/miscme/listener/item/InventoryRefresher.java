package me.sosedik.miscme.listener.item;

import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.dataset.MiscMeItems;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Constantly updates player's inventory to update items in it.
 * <p>
 * This is useful for items like clock that constantly need updates.
 */
@NullMarked
public class InventoryRefresher implements Listener {

	private static final Set<Material> REFRESHABLE = new HashSet<>();

	static {
		addRefreshable(
			Material.CLOCK,
			Material.COMPASS,
			Material.RECOVERY_COMPASS,
			MiscMeItems.BAROMETER,
			MiscMeItems.LUNAR_CLOCK,
			MiscMeItems.DEPTH_METER,
			MiscMeItems.SPEEDOMETER,
			MiscMeItems.LUXMETER
		);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		startUpdateInventoryTask(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player player)
			MiscMe.scheduler().sync(() -> player.sendCursorItem(player.getItemOnCursor()), 1L);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDrag(InventoryDragEvent event) {
		if (event.getWhoClicked() instanceof Player player)
			MiscMe.scheduler().sync(() -> player.sendCursorItem(player.getItemOnCursor()), 1L);
	}

	// TODO This resets bundle's picked item
	private void startUpdateInventoryTask(Player player) {
		MiscMe.scheduler().sync(task -> {
			if (!player.isOnline()) return true;
			if (player.isDead()) return false;
			if (player.getGameMode() == GameMode.CREATIVE) return false; // TODO figure out why items disappear from cursor in creative?

			InventoryView view = player.getOpenInventory();

			ItemStack cursor = view.getCursor();
			if (REFRESHABLE.contains(cursor.getType()))
				player.sendCursorItem(cursor);

			for (int i = 0; i < view.countSlots(); i++) {
				Inventory inv = view.getInventory(i);
				if (inv == null) continue;

				ItemStack item = inv.getItem(view.convertSlot(i));
				if (ItemStack.isEmpty(item)) continue;
				if (!REFRESHABLE.contains(item.getType())) continue;

				player.sendItem(i, item);
			}

			return false;
		}, 10L, 10L);
	}

	public static void addRefreshable(Material... types) {
		REFRESHABLE.addAll(List.of(types));
	}

}
