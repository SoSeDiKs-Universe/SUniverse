package me.sosedik.utilizer.api.event.recipe;

import me.sosedik.kiterino.event.inventory.CrafterCraftPreviewEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Crafter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Called when a resulting item is prepared for the craft
 */
@NullMarked
public class ItemCraftPrepareEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Event parentEvent;
	private final Recipe recipe;
	private final NamespacedKey key;
	private final @Nullable Player player;
	private final String recipeGroup;

	public ItemCraftPrepareEvent(Event parentEvent, NamespacedKey key) {
		super();
		this.parentEvent = parentEvent;
		this.key = key;

		switch (parentEvent) {
			case PrepareItemCraftEvent event -> {
				assert event.getRecipe() != null;
				this.recipe = event.getRecipe();
				this.player = event.getViewers().isEmpty() ? null : (Player) event.getViewers().getFirst();
			}
			case CrafterCraftEvent event -> {
				this.recipe = event.getRecipe();
				this.player = null;
			}
			case CrafterCraftPreviewEvent event -> {
				this.recipe = event.getRecipe();
				this.player = null;
			}
			default -> throw new IllegalArgumentException("Unsupported parent event: " + parentEvent.getEventName());
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
	public Event getParentEvent() {
		return this.parentEvent;
	}

	/**
	 * Gets the crafted recipe
	 *
	 * @return the crafted recipe
	 */
	public Recipe getRecipe() {
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
	 * Gets the result item
	 *
	 * @return the result item
	 */
	public @Nullable ItemStack getResult() {
		return switch (this.parentEvent) {
			case PrepareItemCraftEvent event -> event.getInventory().getResult();
			case CrafterCraftEvent event -> event.getResult();
			case CrafterCraftPreviewEvent event -> event.getResult();
			default -> null;
		};
	}

	/**
	 * Sets the result item
	 *
	 * @param result the result item
	 */
	public void setResult(@Nullable ItemStack result) {
		switch (this.parentEvent) {
			case PrepareItemCraftEvent event -> event.getInventory().setResult(result);
			case CrafterCraftEvent event -> event.setResult(result == null ? ItemStack.empty() : result);
			case CrafterCraftPreviewEvent event -> event.setResult(result);
			default -> {}
		}
	}

	/**
	 * Gets the crafting matrix
	 *
	 * @return the crafting matrix
	 */
	public @Nullable ItemStack[] getMatrix() {
		return switch (this.parentEvent) {
			case PrepareItemCraftEvent event -> event.getInventory().getMatrix();
			case CrafterCraftEvent event -> {
				if (!(event.getBlock().getState(false) instanceof Crafter container)) yield new ItemStack[]{};
				yield container.getInventory().getContents();
			}
			case CrafterCraftPreviewEvent event -> event.getView().getTopInventory().getStorageContents();
			default -> new ItemStack[]{};
		};
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
