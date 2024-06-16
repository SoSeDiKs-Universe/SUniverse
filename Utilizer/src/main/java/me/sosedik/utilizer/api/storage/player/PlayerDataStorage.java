package me.sosedik.utilizer.api.storage.player;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import me.sosedik.utilizer.listener.player.PlayerDataLoadSave;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface PlayerDataStorage {

	/**
	 * Gets currently present player nbt data
	 *
	 * @param player player
	 * @return player nbt data
	 */
	static @NotNull NBTCompound getData(@NotNull Player player) {
		return getData(player.getUniqueId());
	}

	/**
	 * Gets currently present player nbt data
	 *
	 * @param uuid uuid
	 * @return player nbt data
	 */
	static @NotNull NBTCompound getData(@NotNull UUID uuid) {
		return PlayerDataLoadSave.getData(uuid);
	}

}
