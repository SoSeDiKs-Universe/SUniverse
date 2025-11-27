package me.sosedik.utilizer.api.event.player;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Called when a player tries to place an item somewhere
 */
@NullMarked
public class PlayerPlaceItemEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private final ItemStack item;
	private boolean cancelled;

	public PlayerPlaceItemEvent(Player who, ItemStack item) {
		super(who);
		Preconditions.checkArgument(!item.isEmpty(), "Item cannot be empty");
		this.item = item;
	}

	/**
	 * Gets the placed item
	 *
	 * @return the placed item
	 */
	public ItemStack getItem() {
		return this.item;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
