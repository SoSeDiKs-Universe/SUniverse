package me.sosedik.requiem.api.event.player;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerStartPossessingEntityEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final LivingEntity entity;

	public PlayerStartPossessingEntityEvent(@NotNull Player who, @NotNull LivingEntity entity) {
		super(who);
		this.entity = entity;
	}

	/**
	 * Gets the possessed entity
	 *
	 * @return the possessed entity
	 */
	public @NotNull LivingEntity getEntity() {
		return this.entity;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static @NotNull HandlerList getHandlerList() {
		return HANDLERS;
	}

}
