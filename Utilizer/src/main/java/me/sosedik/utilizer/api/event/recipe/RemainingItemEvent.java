package me.sosedik.utilizer.api.event.recipe;

import me.sosedik.utilizer.api.recipe.CustomRecipe;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Called when picking a leftover item, primary for the items during crafting
 */
@NullMarked
public class RemainingItemEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final @Nullable Event parentEvent;
	private final @Nullable Recipe recipe;
	private final NamespacedKey key;
	private final String recipeGroup;
	private final @Nullable Player player;
	private final ItemStack item;
	private final int amount;
	private @Nullable ItemStack result;

	public RemainingItemEvent(@Nullable Event parentEvent, @Nullable Player player, @Nullable Recipe recipe, NamespacedKey key, ItemStack item, int amount) {
		super();
		this.parentEvent = parentEvent;
		this.recipe = recipe;
		this.key = key;
		this.player = player;
		this.item = item;
		this.amount = amount;
		this.result = null;

		if (recipe instanceof CraftingRecipe craftingRecipe) {
			this.recipeGroup = craftingRecipe.getGroup();
		} else if (recipe instanceof CustomRecipe customRecipe) {
			this.recipeGroup = customRecipe.getGroup();
		} else {
			this.recipeGroup = "";
		}
	}

	/**
	 * Gets the parent event
	 *
	 * @return the parent event
	 */
	public @Nullable Event getParentEvent() {
		return this.parentEvent;
	}

	/**
	 * Gets the crafted recipe
	 *
	 * @return the crafted recipe
	 */
	public @Nullable Recipe getRecipe() {
		return this.recipe;
	}

	/**
	 * Gets the recipe key
	 *
	 * @return the recipe key
	 */
	public NamespacedKey getKey() {
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
	public String getRecipeGroup() {
		return this.recipeGroup;
	}

	/**
	 * Gets the used item
	 *
	 * @return the used item
	 */
	public ItemStack getItem() {
		return this.item;
	}

	/**
	 * Gets the item amount
	 *
	 * @return amount
	 */
	public int getAmount() {
		return this.amount;
	}

	/**
	 * Gets the result item
	 *
	 * @return the result item
	 */
	public @Nullable ItemStack getResult() {
		return this.result;
	}

	/**
	 * Sets the result item
	 *
	 * @param result the result item
	 */
	public void setResult(@Nullable ItemStack result) {
		this.result = result;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}


}
