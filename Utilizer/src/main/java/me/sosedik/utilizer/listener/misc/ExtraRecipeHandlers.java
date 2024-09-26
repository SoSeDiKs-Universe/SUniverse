package me.sosedik.utilizer.listener.misc;

import me.sosedik.utilizer.api.event.recipe.ItemCraftEvent;
import me.sosedik.utilizer.api.event.recipe.ItemCraftPrepareEvent;
import me.sosedik.utilizer.api.event.recipe.RemainingItemEvent;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Extra handlers for crafting recipes
 */
public class ExtraRecipeHandlers implements Listener {

	private static final Map<NamespacedKey, Consumer<ItemCraftPrepareEvent>> PRE_EXTRA = new HashMap<>();
	private static final Map<NamespacedKey, Consumer<ItemCraftEvent>> EXTRA = new HashMap<>();
	private static final Map<NamespacedKey, Consumer<RemainingItemEvent>> REMAINS = new HashMap<>();

	@EventHandler(priority = EventPriority.HIGH)
	public void onPreCraft(@NotNull PrepareItemCraftEvent event) {
		if (!(event.getRecipe() instanceof Keyed keyed)) return;

		new ItemCraftPrepareEvent(event, keyed.getKey()).callEvent();
	}

	@EventHandler
	public void onPreCraft(@NotNull ItemCraftPrepareEvent event) {
		Consumer<ItemCraftPrepareEvent> extra = PRE_EXTRA.get(event.getKey());
		if (extra == null) return;

		extra.accept(event);
	}

	@EventHandler
	public void onCraft(@NotNull ItemCraftEvent event) {
		Consumer<ItemCraftEvent> extra = EXTRA.get(event.getKey());
		if (extra == null) return;

		extra.accept(event);
	}

	@EventHandler
	public void onCraft(@NotNull RemainingItemEvent event) {
		Consumer<RemainingItemEvent> extra = REMAINS.get(event.getKey());
		if (extra == null) return;

		extra.accept(event);
	}

	/**
	 * Adds a prepare item craft event check
	 *
	 * @param key recipe key
	 * @param check check
	 */
	public static void addPreCraftCheck(@NotNull NamespacedKey key, @NotNull Consumer<@NotNull ItemCraftPrepareEvent> check) {
		PRE_EXTRA.put(key, check);
	}

	/**
	 * Adds a leftover item check
	 *
	 * @param key recipe key
	 * @param check check
	 */
	public static void addLeftoverCheck(@NotNull NamespacedKey key, @NotNull Consumer<@NotNull RemainingItemEvent> check) {
		REMAINS.put(key, check);
	}

	/**
	 * Adds a post-craft check
	 *
	 * @param key recipe key
	 * @param check check
	 */
	public static void addExtraCheck(@NotNull NamespacedKey key, @NotNull Consumer<@NotNull ItemCraftEvent> check) {
		EXTRA.put(key, check);
	}

}
