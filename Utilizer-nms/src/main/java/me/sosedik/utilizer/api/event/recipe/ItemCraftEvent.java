package me.sosedik.utilizer.api.event.recipe;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Campfire;
import org.bukkit.block.Crafter;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Called when an item is crafted
 */
@NullMarked
public class ItemCraftEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Event parentEvent;
	private final Recipe recipe;
	private final NamespacedKey key;
	private final @Nullable Player player;
	private final String recipeGroup;

	public ItemCraftEvent(Event parentEvent, NamespacedKey key) {
		super();
		this.parentEvent = parentEvent;
		this.key = key;

		switch (parentEvent) {
			case CraftItemEvent event -> {
				this.recipe = event.getRecipe();
				this.player = (Player) event.getWhoClicked();
			}
			case CrafterCraftEvent event -> {
				this.recipe = event.getRecipe();
				this.player = null;
			}
			case BlockCookEvent event -> {
				assert event.getRecipe() != null;
				this.recipe = event.getRecipe();
				this.player = null;
			}
			default -> throw new IllegalArgumentException("Unsupported parent event: " + parentEvent.getEventName());
		}

		if (recipe instanceof CraftingRecipe craftingRecipe) {
			this.recipeGroup = craftingRecipe.getGroup();
		} else if (recipe instanceof CookingRecipe<?> cookingRecipe) {
			this.recipeGroup = cookingRecipe.getGroup();
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
			case CraftItemEvent event -> event.getInventory().getResult();
			case CrafterCraftEvent event -> event.getResult();
			case BlockCookEvent event -> event.getResult();
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
			case CraftItemEvent event -> event.getInventory().setResult(result);
			case CrafterCraftEvent event -> event.setResult(result == null ? ItemStack.empty() : result);
			case BlockCookEvent event -> event.setResult(result == null ? ItemStack.empty() : result);
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
			case CraftItemEvent event -> event.getInventory().getMatrix();
			case CrafterCraftEvent event -> {
				if (!(event.getBlock().getState(false) instanceof Crafter container)) yield new ItemStack[]{};
				yield container.getInventory().getContents();
			}
			case BlockCookEvent event -> {
				if (event.getBlock().getState(false) instanceof Furnace container)
					yield new @Nullable ItemStack[]{container.getInventory().getSmelting()};
				if (event.getBlock().getState(false) instanceof Campfire container) {
					@Nullable ItemStack[] matrix = new ItemStack[container.getSize()];
					for (int i = 0; i < matrix.length; i++)
						matrix[i] = container.getItem(i);
					yield matrix;
				}
				yield new ItemStack[]{};
			}
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
