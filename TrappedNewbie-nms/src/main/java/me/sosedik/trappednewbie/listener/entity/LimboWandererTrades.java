package me.sosedik.trappednewbie.listener.entity;

import me.sosedik.miscme.dataset.MoreMobHeads;
import me.sosedik.packetadvancements.util.ToastMessage;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import me.sosedik.trappednewbie.dataset.TrappedNewbieFonts;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.impl.item.modifier.LetterModifier;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.ItemUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.view.MerchantView;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Special trades for Wandering Trader in Limbo
 */
@NullMarked
public class LimboWandererTrades implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onInteract(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof WanderingTrader entity)) return;
		if (entity.getWorld() != Utilizer.limboWorld()) return;

		event.setCancelled(true);
		Player player = event.getPlayer();

		if (!TrappedNewbieAdvancements.BRAVE_NEW_WORLD.isDone(player)) {
			openFreeFriendshipTradeScreen(player);
		} else {
			TrappedNewbieAdvancements.REQUIEM_ROOT.awardAllCriteria(player); // Just in case
		}
	}

	private void openFreeFriendshipTradeScreen(Player player) {
		if (!TrappedNewbieAdvancements.REQUIEM_ROOT.hasCriteria(player, "interact", "open")) return;

		ItemStack item = player.getInventory().getItemInMainHand();

		// Already obtained the letter, trying to befriend the trader
		if (item.getType() == TrappedNewbieItems.LETTER && LetterModifier.isUnboundFriendshipLetter(item) && !TrappedNewbieAdvancements.REQUIEM_ROOT.hasCriteria(player, "friendship")) {
			player.swingMainHand();
			item.subtract();
			TrappedNewbieAdvancements.REQUIEM_ROOT.awardCriteria(player, "friendship");
			ToastMessage.showToast(player, ItemUtil.texturedHead(MoreMobHeads.WANDERING_TRADER), Messenger.messenger(player).getMessage("friendship.limbo"));
			return;
		}

		MerchantRecipe merchantRecipe = new MerchantRecipe(LetterModifier.getFriendshipLetter(), Integer.MAX_VALUE);
		merchantRecipe.addIngredient(ItemStack.of(TrappedNewbieItems.MATERIAL_AIR));

		Merchant merchant = Bukkit.createMerchant();
		merchant.setRecipes(List.of(merchantRecipe));

		MerchantView inv = MenuType.MERCHANT.builder()
			.merchant(merchant)
			.checkReachable(false)
			.build(player);

		player.openInventory(inv);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onTrade(TradeSelectEvent event) {
		if (event.getMerchant() instanceof Entity) return;
		if (!(event.getWhoClicked() instanceof Player player)) return;
		if (player.getWorld() != Utilizer.limboWorld()) return;

		event.setCancelled(true);
		if (TrappedNewbieAdvancements.BRAVE_NEW_WORLD.isDone(player)) return;

		ItemStack item = LetterModifier.getFriendshipLetter();
		if (player.getInventory().contains(item)) {
			player.sendMessage(Mini.combine(Component.space(), TrappedNewbieFonts.WANDERING_TRADER_HEAD.mapping(), Messenger.messenger(player).getMessage("limbo.free_letter_already_obtained")));
			return;
		}

		MerchantInventory inventory = event.getInventory();
		inventory.setContents(new ItemStack[]{ ItemStack.of(TrappedNewbieItems.MATERIAL_AIR), ItemStack.empty(), item });
	}

}
