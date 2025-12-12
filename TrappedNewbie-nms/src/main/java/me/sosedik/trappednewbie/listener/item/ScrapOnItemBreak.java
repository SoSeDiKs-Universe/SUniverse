package me.sosedik.trappednewbie.listener.item;

import me.sosedik.trappednewbie.impl.item.modifier.ScrapModifier;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.util.DurabilityUtil;
import me.sosedik.utilizer.util.InventoryUtil;
import org.bukkit.enchantments.Enchantment;
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
// MCCheck: 1.21.11, Maybe possible to set replacement item via API?
@NullMarked
public class ScrapOnItemBreak implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(PlayerItemBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack brokenItem = event.getBrokenItem();
		if (brokenItem.hasEnchant(Enchantment.VANISHING_CURSE)) return;

		ItemStack scrap = ScrapModifier.makeScrap(brokenItem);

		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (!player.canUseEquipmentSlot(slot)) continue;

			ItemStack item = player.getInventory().getItem(slot);
			if (!item.equals(brokenItem)) continue;

			player.getInventory().setItem(slot, scrap);
			return;
		}

		InventoryUtil.replaceOrAdd(player, EquipmentSlot.HAND, scrap);
	}

	@EventHandler
	public void onBreak(RemainingItemEvent event) {
		if (event.getResult() != null) return;

		ItemStack item = event.getItem();
		if (item.hasEnchant(Enchantment.VANISHING_CURSE)) return;
		if (!DurabilityUtil.hasDurability(item)) return;

		int durability = DurabilityUtil.getDurability(item);
		if (durability > event.getAmount()) return;

		event.setResult(ScrapModifier.makeScrap(item));
	}

}
