package me.sosedik.requiem.listener.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.sosedik.requiem.Requiem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Fakes saddles for clients to allow controlling horses without actual saddles
 */
// MCCheck: 1.21, abstract horse metadata index for saddles
public class FakeHorseSaddles implements PacketListener, Listener {

	private static final int HORSE_META_INDEX = 17;
	private static final Map<Integer, List<UUID>> ENTITY_TO_PLAYER_SADDLES = new HashMap<>();

	@Override
	public void onPacketSend(@NotNull PacketSendEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) return;

		var packet = new WrapperPlayServerEntityMetadata(event);
		List<UUID> playerUuids = ENTITY_TO_PLAYER_SADDLES.get(packet.getEntityId());
		if (playerUuids == null) return;
		if (!(event.getPlayer() instanceof Player player)) return;
		if (!playerUuids.contains(player.getUniqueId())) return;

		for (EntityData entityData : packet.getEntityMetadata()) {
			if (entityData.getIndex() != HORSE_META_INDEX) continue;

			byte mask = (byte) entityData.getValue();
			mask |= 0x04; // Is saddled, required to control entity client-side
			entityData.setValue(mask);
			break;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDespawn(@NotNull EntityRemoveFromWorldEvent event) {
		ENTITY_TO_PLAYER_SADDLES.remove(event.getEntity().getEntityId());
	}

	/**
	 * Adds player to faking list
	 *
	 * @param player player
	 * @param entity entity
	 */
	public static void startTracking(@NotNull Player player, @NotNull Entity entity) {
		ENTITY_TO_PLAYER_SADDLES.computeIfAbsent(entity.getEntityId(), k -> new ArrayList<>()).add(player.getUniqueId());
		entity.resendMetadata(HORSE_META_INDEX);
	}

	/**
	 * Stops tracking the entity
	 *
	 * @param entity entity
	 */
	public static void stopTracking(@NotNull Player player, @NotNull Entity entity) {
		List<UUID> playerUuids = ENTITY_TO_PLAYER_SADDLES.get(entity.getEntityId());
		if (playerUuids == null) return;
		if (!playerUuids.remove(player.getUniqueId())) return;

		if (playerUuids.isEmpty()) {
			ENTITY_TO_PLAYER_SADDLES.remove(entity.getEntityId());
		}

		entity.resendMetadata(HORSE_META_INDEX);
	}

}