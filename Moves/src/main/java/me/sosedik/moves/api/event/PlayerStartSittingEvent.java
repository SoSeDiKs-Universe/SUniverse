package me.sosedik.moves.api.event;

import me.sosedik.moves.listener.movement.SittingMechanics;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called when the player starts sitting
 */
@NullMarked
public class PlayerStartSittingEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final Block block;
	private SittingMechanics.SitCase sitCase;

	public PlayerStartSittingEvent(Player who, Block block, SittingMechanics.SitCase sitCase) {
		super(who);
		this.block = block;
		this.sitCase = sitCase;
	}

	public Block getBlock() {
		return this.block;
	}

	public SittingMechanics.SitCase getSitCase() {
		return this.sitCase;
	}

	public void setSitCase(SittingMechanics.SitCase sitCase) {
		this.sitCase = sitCase;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
