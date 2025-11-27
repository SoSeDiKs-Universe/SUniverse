package me.sosedik.miscme.listener.misc;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.PotionContents;
import me.sosedik.miscme.MiscMe;
import me.sosedik.miscme.dataset.MiscMeTags;
import me.sosedik.utilizer.api.event.recipe.ItemCraftPrepareEvent;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.impl.recipe.ShapelessCraft;
import me.sosedik.utilizer.util.InventoryUtil;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;
import org.jspecify.annotations.NullMarked;

/**
 * Converts bottles to water bottles when in water,
 * and allows converting potions to water bottles
 */
@NullMarked
public class WaterAwareBottleReset implements Listener {

	public WaterAwareBottleReset() {
		addPotionResetRecipe();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCraft(ItemCraftPrepareEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		if (!player.isUnderWater()) return;

		ItemStack item = event.getResult();
		if (!ItemStack.isType(item, Material.GLASS_BOTTLE)) return;

		event.setResult(getWaterBottle(item.getAmount()));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCraft(RemainingItemEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		if (!player.isUnderWater()) return;

		ItemStack item = event.getResult();
		if (!ItemStack.isType(item, Material.GLASS_BOTTLE)) return;

		int amount = event.getAmount();
		if (item.getAmount() == amount) {
			event.setResult(getWaterBottle(amount));
		} else {
			InventoryUtil.addOrDrop(player, getWaterBottle(amount), true);
			item.subtract(amount);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent event) {
		if (event.getReplacement() != null && !ItemStack.isType(event.getReplacement(), Material.GLASS_BOTTLE)) return;
		if (!event.getPlayer().isUnderWater()) return;
		if (!ItemUtil.isWaterPotion(event.getItem())) return;

		event.setReplacement(getWaterBottle(1));
	}

	private void addPotionResetRecipe() {
		new ShapelessCraft(ItemStack.of(Material.GLASS_BOTTLE), MiscMe.miscMeKey("bottle_reset"))
			.special()
			.addIngredients(MiscMeTags.RESETTABLE_BOTTLE_ITEMS.getValues())
			.withExemptLeftovers()
			.register();
	}

	public static ItemStack getWaterBottle(int amount) {
		var item = ItemStack.of(Material.POTION, amount);
		item.setData(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents().potion(PotionType.WATER).build());
		return item;
	}

}
