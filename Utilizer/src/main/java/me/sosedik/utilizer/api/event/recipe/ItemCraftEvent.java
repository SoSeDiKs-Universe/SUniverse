package me.sosedik.utilizer.api.event.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when an item is crafted
 */
public class ItemCraftEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Event parentEvent;
	private final Recipe recipe;
	private final NamespacedKey key;
	private final @Nullable Player player;
	private final String recipeGroup;

	public ItemCraftEvent(@NotNull Event parentEvent, @NotNull NamespacedKey key) {
		super();
		this.parentEvent = parentEvent;
		this.key = key;

		if (parentEvent instanceof CraftItemEvent event) {
			this.recipe = event.getRecipe();
			this.player = (Player) event.getWhoClicked();
		} else if (parentEvent instanceof CrafterCraftEvent event) {
			this.recipe = event.getRecipe();
			this.player = null;
		} else {
			throw new IllegalArgumentException("Unsupported parent event: " + parentEvent.getEventName());
		}

		if (recipe instanceof CraftingRecipe craftingRecipe) {
			this.recipeGroup = craftingRecipe.getGroup();
		} else {
			this.recipeGroup = "";
		}
	}

	/**
	 * Gets the parent event
	 *
	 * @return the parent event
	 */
	public @NotNull Event getParentEvent() {
		return this.parentEvent;
	}

	/**
	 * Gets the crafted recipe
	 *
	 * @return the crafted recipe
	 */
	public @NotNull Recipe getRecipe() {
		return this.recipe;
	}

	/**
	 * Gets the recipe key
	 *
	 * @return the recipe key
	 */
	public @NotNull NamespacedKey getKey() {
		return this.key;
	}

	/**
	 * Gets the player who crafted the item
	 *
	 * @return the player who crafted the item
	 */
	public @Nullable Player getPlayer() {
		return this.player;
	}

	/**
	 * Gets the recipe group
	 *
	 * @return the recipe group
	 */
	public @NotNull String getRecipeGroup() {
		return this.recipeGroup;
	}

	/**
	 * Gets the result item
	 *
	 * @return the result item
	 */
	public @Nullable ItemStack getResult() {
		if (this.parentEvent instanceof CraftItemEvent event) {
			return event.getInventory().getResult();
		} else if (this.parentEvent instanceof CrafterCraftEvent event) {
			return event.getResult();
		} else {
			return null;
		}
	}

	/**
	 * Sets the result item
	 *
	 * @param result the result item
	 */
	public void setResult(@Nullable ItemStack result) {
		if (this.parentEvent instanceof CraftItemEvent event) {
			event.getInventory().setResult(result);
		} else if (this.parentEvent instanceof CrafterCraftEvent event) {
			event.setResult(result == null ? ItemStack.empty() : result);
		}
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static @NotNull HandlerList getHandlerList() {
		return handlers;
	}

}
