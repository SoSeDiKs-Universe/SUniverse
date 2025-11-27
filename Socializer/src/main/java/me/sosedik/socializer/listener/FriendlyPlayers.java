package me.sosedik.socializer.listener;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.socializer.friendship.FriendshipData;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

/**
 * Let's all be friends
 */
@NullMarked
public class FriendlyPlayers implements Listener {

	private static final String FRIENDS_TAG = "friends";

	@EventHandler
	public void onLoad(PlayerDataLoadedEvent event) {
		persistData(event.getData(), event.getBackupData());
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onSave(PlayerDataSaveEvent event) {
		persistData(event.getPreData(), event.getData());
	}

	private void persistData(ReadWriteNBT preData, ReadWriteNBT data) {
		if (preData.hasTag(FRIENDS_TAG))
			data.getOrCreateCompound(FRIENDS_TAG).mergeCompound(preData.getOrCreateCompound(FRIENDS_TAG));
	}

	public static FriendshipData getFriendshipData(Player player) {
		return new FriendshipData(player, PlayerDataStorage.getData(player).getOrCreateCompound(FRIENDS_TAG));
	}

}
