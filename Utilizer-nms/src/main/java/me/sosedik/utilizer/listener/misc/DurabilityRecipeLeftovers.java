package me.sosedik.utilizer.listener.misc;

import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.util.DurabilityUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Adds support for using durability in custom crafts
 */
@NullMarked
public class DurabilityRecipeLeftovers implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCraft(RemainingItemEvent event) {
		if (event.getResult() != null) return;
		// Ignore vanilla recipes
		if (NamespacedKey.MINECRAFT.equals(event.getKey().getNamespace())) return;

		ItemStack item = event.getItem();
		if (!DurabilityUtil.hasDurability(item)) return;

		Player player = event.getPlayer();
		ItemStack replacement;
		if (player != null) {
			replacement = item.damage(event.getAmount(), player);
		} else {
			replacement = item;
			item.damage();
		}
		event.setResult(replacement);
	}

}
