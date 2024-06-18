package me.sosedik.utilizer.api.event.player;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when player's data has finished loading
 */
public class PlayerDataLoadedEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final ReadWriteNBT data;
	private final ReadWriteNBT backupData;

	public PlayerDataLoadedEvent(@NotNull Player who, @NotNull ReadWriteNBT data) {
		super(who);
		this.data = data;
		this.backupData = NBT.createNBTObject();
	}

	/**
	 * Gets player data
	 *
	 * @return player data
	 */
	public @NotNull ReadWriteNBT getData() {
		return data;
	}

	/**
	 * Gets backup player data.
	 * This data will not be erased after this event.
	 *
	 * @return player data
	 */
	public @NotNull ReadWriteNBT getBackupData() {
		return backupData;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	public static @NotNull HandlerList getHandlerList() {
		return HANDLERS;
	}

}
