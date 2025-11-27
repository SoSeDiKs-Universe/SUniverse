package me.sosedik.trappednewbie.listener.advancement.dedicated;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ChargedProjectiles;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class CrossbowsHotbarAdvancement implements Listener {

	@EventHandler
	public void onSlotChange(PlayerInventorySlotChangeEvent event) {
		ItemStack newItemStack = event.getNewItemStack();
		if (!isChargedCrossbow(newItemStack)) return;

		Player player = event.getPlayer();
		if (TrappedNewbieAdvancements.CROSSBOWS_HOTBAR.isDone(player)) return;

		PlayerInventory inventory = player.getInventory();
		for (int i = 0; i < 9; i++) {
			if (!isChargedCrossbow(inventory.getItem(i)))
				return;
		}

		TrappedNewbieAdvancements.CROSSBOWS_HOTBAR.awardAllCriteria(player);
	}

	private boolean isChargedCrossbow(@Nullable ItemStack item) {
		if (!ItemStack.isType(item, Material.CROSSBOW)) return false;

		ChargedProjectiles data = item.getData(DataComponentTypes.CHARGED_PROJECTILES);
		if (data == null) return false;
		return !data.projectiles().isEmpty();
	}

}
