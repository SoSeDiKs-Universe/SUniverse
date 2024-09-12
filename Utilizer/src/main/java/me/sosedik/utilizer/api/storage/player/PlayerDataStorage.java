package me.sosedik.utilizer.api.storage.player;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
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
	static @NotNull ReadWriteNBT getData(@NotNull Player player) {
		return getData(player.getUniqueId());
	}

	/**
	 * Gets currently present player nbt data
	 *
	 * @param uuid uuid
	 * @return player nbt data
	 */
	static @NotNull ReadWriteNBT getData(@NotNull UUID uuid) {
		return PlayerDataLoadSave.getData(uuid);
	}

}
