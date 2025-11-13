package me.sosedik.trappednewbie.listener.item;

import com.destroystokyo.paper.MaterialTags;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Fish buckets don't have craft remainders in vanilla
 */
@NullMarked
public class FishBucketLeftovers implements Listener {

	@EventHandler
	public void onRemain(RemainingItemEvent event) {
		if (event.getResult() != null) return;

		ItemStack item = event.getItem();
		if (!isVanillaBucketable(item.getType())) return;

		event.setResult(ItemStack.of(Material.WATER_BUCKET));
	}

	private boolean isVanillaBucketable(Material type) {
		return MaterialTags.FISH_BUCKETS.isTagged(type)
			|| type == Material.AXOLOTL_BUCKET
			|| type == Material.TADPOLE_BUCKET;
	}

}
