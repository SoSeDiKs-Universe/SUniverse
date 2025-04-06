package me.sosedik.utilizer.api.event.player;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Called when player's data has finished loading
 */
@NullMarked
public class PlayerDataLoadedEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final ReadWriteNBT data;
	private final ReadWriteNBT backupData;

	public PlayerDataLoadedEvent(Player who, ReadWriteNBT data) {
		super(who);
		this.data = data;
		this.backupData = NBT.createNBTObject();
	}

	/**
	 * Gets player data
	 *
	 * @return player data
	 */
	public ReadWriteNBT getData() {
		return data;
	}

	/**
	 * Gets backup player data.
	 * This data will not be erased after this event.
	 *
	 * @return player data
	 */
	public ReadWriteNBT getBackupData() {
		return backupData;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
