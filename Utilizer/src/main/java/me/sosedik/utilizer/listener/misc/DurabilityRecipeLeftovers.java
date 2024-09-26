package me.sosedik.utilizer.listener.misc;

import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import me.sosedik.utilizer.util.Durability;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Adds support for using durability in custom crafts
 */
public class DurabilityRecipeLeftovers implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCraft(@NotNull RemainingItemEvent event) {
		if (event.getResult() != null) return;
		// Ignore vanilla recipes
		if (NamespacedKey.MINECRAFT.equals(event.getKey().getNamespace())) return;

		ItemStack item = event.getItem();
		if (!Durability.hasDurability(item)) return;

		ItemStack replacement = Durability.damageItem(item.clone(), event.getAmount());
		event.setResult(replacement);
	}

}
