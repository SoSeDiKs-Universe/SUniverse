package me.sosedik.socializer.friendship;

import com.google.common.base.Preconditions;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.socializer.listener.FriendlyPlayers;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Represents friendship data for a player, stored in NBT format.
 *
 * @param player the player
 * @param data the NBT data containing friendship information
 */
public record FriendshipData(Player player, ReadWriteNBT data) {

	private static final String DATE_TAG = "date";

	/**
	 * Checks if this player is friends with the given player.
	 *
	 * @param player the player to check friendship with
	 * @return true if they are friends, false otherwise
	 */
	public boolean isFriendsWith(Player player) {
		return data().hasTag(player.getUniqueId().toString());
	}

	/**
	 * Befriends the given player, updating both players' friendship data.
	 *
	 * @param player the player to befriend
	 * @throws IllegalArgumentException if trying to befriend oneself or if either player is offline
	 */
	public void befriend(Player player) {
		Preconditions.checkArgument(!player.getUniqueId().equals(player().getUniqueId()), "Can't befriend yourself");
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

	/**
	 * Gets the list of UUIDs of friends for this player.
	 *
	 * @return a list of friend UUIDs
	 */
	public List<UUID> getFriends() {
		Set<String> keys = this.data.getKeys();
		List<UUID> uuids = new ArrayList<>(keys.size());
		keys.forEach(key -> uuids.add(UUID.fromString(key)));
		return uuids;
	}

}
