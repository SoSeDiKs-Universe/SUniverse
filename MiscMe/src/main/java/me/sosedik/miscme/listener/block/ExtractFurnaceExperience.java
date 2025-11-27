package me.sosedik.miscme.listener.block;

import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Extract furnace experience when clicking on the empty output slot
 */
@NullMarked
public class ExtractFurnaceExperience implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onClick(InventoryClickEvent event) {
		if (!(event.getClickedInventory() instanceof FurnaceInventory inventory)) return;
		if (event.getRawSlot() != 2) return;
		if (!event.isLeftClick()) return;
		if (!ItemStack.isEmpty(event.getCursor())) return;
		if (!ItemStack.isEmpty(event.getCurrentItem())) return;
		if (!(inventory.getHolder(false) instanceof Furnace furnace)) return;
		if (!(event.getWhoClicked() instanceof Player player)) return;

		furnace.awardUsedRecipesAndPopExperience(player);
	}

}
