package me.sosedik.utilizer.listener.recipe;

import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.impl.recipe.WaterCraft;
import me.sosedik.utilizer.util.RecipeManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Crafting by submerging items into water
 */
@NullMarked
public class WaterCrafting implements Listener {

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSpawn(ItemSpawnEvent event) {
		startListening(event.getEntity());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMerge(ItemMergeEvent event) {
		startListening(event.getTarget());
	}

	private void startListening(Item item) {
		ItemStack itemStack = item.getItemStack();
		if (itemStack.isEmpty()) return;

		WaterCraft recipe = RecipeManager.getRecipe(WaterCraft.class, new ItemStack[] {item.getItemStack()});
		if (recipe == null) return;

		Utilizer.scheduler().sync(task -> {
			if (!item.isValid()) return true;

			Block block = item.getLocation().getBlock();
			if (block.getType() == Material.WATER) {
				if (!(block.getBlockData() instanceof Levelled levelled)) return false;
				if (levelled.getLevel() > 7) return false;
			} else if (block.getType() == Material.WATER_CAULDRON) {
				if (!(block.getBlockData() instanceof Levelled)) return false;
			} else {
				return false;
			}

			recipe.performAction(item);

			startListening(item);
			return true;
		}, 5L, 5L);
	}

}
