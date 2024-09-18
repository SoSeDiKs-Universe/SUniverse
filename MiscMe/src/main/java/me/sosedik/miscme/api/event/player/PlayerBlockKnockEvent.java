package me.sosedik.miscme.api.event.player;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when player tries to knock on a block
 */
public class PlayerBlockKnockEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private final Block block;
	private boolean cancelled;

	public PlayerBlockKnockEvent(@NotNull Player who, @NotNull Block block, boolean allowed) {
		super(who);
		this.block = block;
		this.cancelled = !allowed;
	}

	/**
	 * Gets the block that was knocked
	 *
	 * @return knocked block
	 */
	public @NotNull Block getBlock() {
		return this.block;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static @NotNull HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

}
