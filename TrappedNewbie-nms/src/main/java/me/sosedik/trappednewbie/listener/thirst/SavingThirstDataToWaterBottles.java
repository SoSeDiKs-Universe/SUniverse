package me.sosedik.trappednewbie.listener.thirst;

import io.papermc.paper.event.block.BlockFillBottleEvent;
import io.papermc.paper.event.player.PlayerFillBottleEvent;
import me.sosedik.miscme.listener.misc.WaterAwarePotionReset;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.utilizer.api.event.recipe.ItemCraftPrepareEvent;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.util.InventoryUtil;
import me.sosedik.utilizer.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Saving thirst data to water bottles
 */
@NullMarked
public class SavingThirstDataToWaterBottles implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCraft(ItemCraftPrepareEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		if (!player.isUnderWater()) return;

		ItemStack item = event.getResult();
		if (item == null) return;
		if (!ItemUtil.isWaterPotion(item)) return;

		Block block = event.getPlayer().getEyeLocation().getBlock();
		event.setResult(ThirstData.of(block).saveInto(item));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onCraft(RemainingItemEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		if (!player.isUnderWater()) return;

		ItemStack item = event.getResult();
		if (!ItemStack.isType(item, Material.GLASS_BOTTLE)) return;

		Block block = event.getPlayer().getEyeLocation().getBlock();

		int amount = event.getAmount();
		if (item.getAmount() == amount) {
			event.setResult(ThirstData.of(block).saveInto(WaterAwarePotionReset.getWaterBottle(amount)));
		} else {
			InventoryUtil.addOrDrop(player, ThirstData.of(block).saveInto(WaterAwarePotionReset.getWaterBottle(amount)), true);
			item.subtract(amount);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent event) {
		ItemStack replacement = event.getReplacement();
		if (replacement == null) return;
		if (!event.getPlayer().isUnderWater()) return;
		if (!ItemUtil.isWaterPotion(replacement)) return;

		Block block = event.getPlayer().getEyeLocation().getBlock();
		event.setReplacement(ThirstData.of(block).saveInto(replacement));
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFill(PlayerFillBottleEvent event) {
		if (event.getBottle().getType() != Material.GLASS_BOTTLE) return;
		if (!ItemUtil.isWaterPotion(event.getResultItem())) return;

		Player player = event.getPlayer();
		Block block = player.getEyeLocation().getBlock();
		event.setResultItem(ThirstData.of(block).saveInto(event.getResultItem()));
//		TrappedNewbieAdvancements.PICK_UP_SOME_WATER.awardAllCriteria(player); // TODO adv
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFill(BlockFillBottleEvent event) {
		if (event.getBottle().getType() != Material.GLASS_BOTTLE) return;
		if (!ItemUtil.isWaterPotion(event.getResultItem())) return;

		Block block = event.getBlock();
		if (block.getBlockData() instanceof Dispenser dispenser)
			block = block.getRelative(dispenser.getFacing());
		event.setResultItem(ThirstData.of(block).saveInto(event.getResultItem()));
	}

}
