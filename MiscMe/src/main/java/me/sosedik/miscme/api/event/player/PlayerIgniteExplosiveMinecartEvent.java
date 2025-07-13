package me.sosedik.miscme.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called when player ignites explosive minecart
 */
@NullMarked
public class PlayerIgniteExplosiveMinecartEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final ExplosiveMinecart minecart;

	public PlayerIgniteExplosiveMinecartEvent(Player player, ExplosiveMinecart minecart) {
		super(player);
		this.minecart = minecart;
	}

	public ExplosiveMinecart getMinecart() {
		return this.minecart;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
