package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.impl.item.modifier.ScrapModifier;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.util.DurabilityUtil;
import me.sosedik.utilizer.util.InventoryUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Scrap is made upon item break
 */
// MCCheck: 1.21.8, Maybe possible to set replacement item via API?
@NullMarked
public class ScrapOnItemBreak implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(PlayerItemBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack brokenItem = event.getBrokenItem();
		ItemStack scrap = ScrapModifier.makeScrap(brokenItem);

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (!player.canUseEquipmentSlot(slot)) continue;

			ItemStack item = player.getInventory().getItem(slot);
			if (!item.equals(brokenItem)) continue;

			player.getInventory().setItem(slot, scrap);
			return;
		}

		if (InventoryUtil.tryToAdd(player, scrap))
			InventoryUtil.addOrDrop(player, scrap, false);
		else
			TrappedNewbie.scheduler().sync(() -> InventoryUtil.addOrDrop(player, scrap, false));
	}

	@EventHandler
	public void onBreak(RemainingItemEvent event) {
		if (event.getResult() != null) return;

		ItemStack item = event.getItem();
		if (!DurabilityUtil.hasDurability(item)) return;

		int durability = DurabilityUtil.getDurability(item);
		if (durability > event.getAmount()) return;

		event.setResult(ScrapModifier.makeScrap(item));
	}

}
