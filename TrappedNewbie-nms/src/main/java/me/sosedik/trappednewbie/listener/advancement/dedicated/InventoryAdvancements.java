package me.sosedik.trappednewbie.listener.advancement.dedicated;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class InventoryAdvancements implements Listener {

	public InventoryAdvancements() {
		TrappedNewbie.scheduler().sync(() -> {
			for (Player player : Bukkit.getOnlinePlayers())
				checkInventory(player);
		}, 20L, 20L);
	}

	// Check inventory pre-death in case the timer has missed
	// If you did it, you did it, GG!
	@EventHandler(ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		checkInventory(event.getPlayer());
	}

	private void checkInventory(Player player) {
		if (checkMoreShields(player)) TrappedNewbieAdvancements.MORE_SHIELDS.awardAllCriteria(player);
	}

	private boolean checkMoreShields(Player player) {
		PlayerInventory inventory = player.getInventory();
		if (!ItemStack.isType(inventory.getItemInOffHand(), Material.SHIELD)) return false;
		for (ItemStack item : inventory.getStorageContents()) {
			if (!ItemStack.isType(item, Material.SHIELD))
				return false;
		}
		return true;
	}

}
