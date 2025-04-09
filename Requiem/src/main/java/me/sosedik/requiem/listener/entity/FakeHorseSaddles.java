package me.sosedik.requiem.listener.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.api.event.player.PlayerStartPossessingEntityEvent;
import me.sosedik.requiem.api.event.player.PlayerStopPossessingEntityEvent;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Fakes saddles for clients to allow controlling horses without actual saddles
 */
// MCCheck: 1.21.5, abstract horse metadata index for saddles // TODO seems to no longer work, instead requires FakeHorseSaddlesModifier?
@NullMarked
public class FakeHorseSaddles implements PacketListener, Listener {

	private static final int HORSE_META_INDEX = 17;
	private static final Map<Integer, List<UUID>> ENTITY_TO_PLAYER_SADDLES = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) return;

		var packet = new WrapperPlayServerEntityMetadata(event);
		List<UUID> playerUuids = ENTITY_TO_PLAYER_SADDLES.get(packet.getEntityId());
		if (playerUuids == null) return;
		if (!(event.getPlayer() instanceof Player player)) return;
		if (!playerUuids.contains(player.getUniqueId())) return;

		for (EntityData<?> entityDataRaw : packet.getEntityMetadata()) {
			if (entityDataRaw.getIndex() != HORSE_META_INDEX) continue;

			EntityData<Byte> entityData = (EntityData<Byte>) entityDataRaw;
			byte mask = (byte) entityDataRaw.getValue();
			mask |= 0x04; // Is saddled, required to control entity client-side
			entityData.setValue(mask);
			break;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDespawn(EntityRemoveFromWorldEvent event) {
		ENTITY_TO_PLAYER_SADDLES.remove(event.getEntity().getEntityId());
	}

	@EventHandler
	public void onPossess(PlayerStartPossessingEntityEvent event) {
		if (event.getEntity() instanceof AbstractHorse entity)
			startTracking(event.getPlayer(), entity);
	}

	@EventHandler
	public void onUnPossess(PlayerStopPossessingEntityEvent event) {
		if (event.getEntity() instanceof AbstractHorse entity)
			stopTracking(event.getPlayer(), entity);
	}

	/**
	 * Adds player to faking list
	 *
	 * @param player player
	 * @param entity entity
	 */
	public static void startTracking(Player player, Entity entity) {
		ENTITY_TO_PLAYER_SADDLES.computeIfAbsent(entity.getEntityId(), k -> new ArrayList<>()).add(player.getUniqueId());
		entity.resendMetadata(HORSE_META_INDEX);
		Requiem.scheduler().sync(player::updateInventory, 3L);
	}

	/**
	 * Stops tracking the entity
	 *
	 * @param entity entity
	 */
	public static void stopTracking(Player player, Entity entity) {
		List<UUID> playerUuids = ENTITY_TO_PLAYER_SADDLES.get(entity.getEntityId());
		if (playerUuids == null) return;
		if (!playerUuids.remove(player.getUniqueId())) return;

		if (playerUuids.isEmpty()) {
			ENTITY_TO_PLAYER_SADDLES.remove(entity.getEntityId());
		}

		entity.resendMetadata(HORSE_META_INDEX);
	}

}
