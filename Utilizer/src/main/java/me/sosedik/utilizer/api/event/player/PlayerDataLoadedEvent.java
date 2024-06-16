package me.sosedik.utilizer.api.event.player;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when player's data has finished loading
 */
public class PlayerDataLoadedEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final NBTCompound data;
	private final NBTCompound backupData;

	public PlayerDataLoadedEvent(@NotNull Player who, @NotNull NBTCompound data) {
		super(who);
		this.data = data;
		this.backupData = new NBTContainer();
	}

	/**
	 * Gets player data
	 *
	 * @return player data
	 */
	public @NotNull NBTCompound getData() {
		return data;
	}

	/**
	 * Gets backup player data.
	 * This data will not be erased after this event.
	 *
	 * @return player data
	 */
	public @NotNull NBTCompound getBackupData() {
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
