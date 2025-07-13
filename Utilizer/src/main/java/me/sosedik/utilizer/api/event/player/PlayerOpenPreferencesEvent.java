package me.sosedik.utilizer.api.event.player;

import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

/**
 * Called before the player's preferences screen is shown
 */
@NullMarked
public class PlayerOpenPreferencesEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	private final List<DialogInput> preferences = new ArrayList<>();
	private boolean cancelled;

	public PlayerOpenPreferencesEvent(Player player) {
		super(player);
	}

	public List<DialogInput> getPreferences() {
		return this.preferences;
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
