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
		return block;
	}

	public BlockFace getBlockFace() {
		return blockFace;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

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
