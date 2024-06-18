package me.sosedik.utilizer.api.event.player;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when player's data is being saved
 */
public class PlayerDataSaveEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final ReadWriteNBT preData;
	private final ReadWriteNBT data;
	private final boolean quit;

	public PlayerDataSaveEvent(@NotNull Player who, @NotNull ReadWriteNBT preData, boolean quit) {
		super(who);
		this.preData = preData;
		this.data = NBT.createNBTObject();
		this.quit = quit;
	}

	/**
	 * Gets player data before save
	 *
	 * @return player data
	 */
	public @NotNull ReadWriteNBT getPreData() {
		return preData;
	}

	/**
	 * Gets player data after save
	 *
	 * @return player data
	 */
	public @NotNull ReadWriteNBT getData() {
		return data;
	}

	/**
	 * Checks whether this is a save upon player quitting
	 *
	 * @return whether called upon the player quitting
	 */
	public boolean isQuit() {
		return quit;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static @NotNull HandlerList getHandlerList() {
		return HANDLERS;
	}

}
