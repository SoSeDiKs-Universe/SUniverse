package me.sosedik.socializer.friendship;

import com.google.common.base.Preconditions;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.socializer.listener.FriendlyPlayers;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record FriendshipData(Player player, ReadWriteNBT data) {

	private static final String DATE_TAG = "date";

	public boolean isFriendsWith(Player player) {
		return data().hasTag(player.getUniqueId().toString());
	}

	public void befriend(Player player) {
		Preconditions.checkArgument(player != player(), "Can't befriend yourself");
		Preconditions.checkArgument(player.isOnline() && player().isOnline(), "Player must be online");

		String id = player.getUniqueId().toString();
		if (data().hasTag(id)) return;

		makeFriend(player);
		FriendlyPlayers.getFriendshipData(player).makeFriend(player());
	}

	private void makeFriend(Player player) {
		String id = player.getUniqueId().toString();
		ReadWriteNBT data = data().getOrCreateCompound(id);
		long time = System.currentTimeMillis();
		data.setLong(DATE_TAG, time);
	}

	public List<UUID> getFriends() {
		Set<String> keys = this.data.getKeys();
		List<UUID> uuids = new ArrayList<>(keys.size());
		keys.forEach(key -> uuids.add(UUID.fromString(key)));
		return uuids;
	}

}
