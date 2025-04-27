package me.sosedik.miscme.api.event.player;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called when player tries to knock on a block
 */
@NullMarked
public class PlayerBlockKnockEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private final Block block;
	private boolean cancelled;

	public PlayerBlockKnockEvent(Player who, Block block, boolean allowed) {
		super(who);
		this.block = block;
		this.cancelled = !allowed;
	}

	/**
	 * Gets the block that was knocked
	 *
	 * @return knocked block
	 */
	public Block getBlock() {
		return this.block;
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
