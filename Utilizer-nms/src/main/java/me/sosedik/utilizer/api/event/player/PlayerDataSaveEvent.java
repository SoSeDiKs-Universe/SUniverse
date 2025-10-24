package me.sosedik.utilizer.api.event.player;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called when player's data is being saved
 */
@NullMarked
public class PlayerDataSaveEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final ReadWriteNBT preData;
	private final ReadWriteNBT data;
	private final boolean quit;

	public PlayerDataSaveEvent(Player who, ReadWriteNBT preData, boolean quit) {
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
	public ReadWriteNBT getPreData() {
		return this.preData;
	}

	/**
	 * Gets player data after save
	 *
	 * @return player data
	 */
	public ReadWriteNBT getData() {
		return this.data;
	}

	/**
	 * Checks whether this is a save upon player quitting
	 *
	 * @return whether called upon the player quitting
	 */
	public boolean isQuit() {
		return this.quit;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
