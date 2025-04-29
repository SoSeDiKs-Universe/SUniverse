package me.sosedik.trappednewbie.api.event.player;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerTargetBlockEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final Block block;
	private final BlockFace blockFace;
	private boolean cancelled = false;

	public PlayerTargetBlockEvent(Player who, Block block, BlockFace blockFace) {
		super(who);
		this.block = block;
		this.blockFace = blockFace;
	}

	public Block getBlock() {
		return this.block;
	}

	public BlockFace getBlockFace() {
		return this.blockFace;
	}

	/**
	 * Checks whether targeting is allowed
	 *
	 * @return whether targeting is allowed
	 */
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	/**
	 * Sets whether targeting is allowed
	 *
	 * @param cancel {@code true} if you wish allow targeting
	 */
	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
