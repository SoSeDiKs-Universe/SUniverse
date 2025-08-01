package me.sosedik.utilizer.listener.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.sosedik.utilizer.util.GlowingUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Implements per-player glowing
 */
@NullMarked
public class EntityGlowTracker implements PacketListener, Listener {

	private static final int GLOWING_METADATA_INDEX = 0;

	private static final Map<Integer, HashSet<UUID>> ENTITY_TO_PLAYER_GLOW = new HashMap<>();

	public EntityGlowTracker() {
		// Init teams responsible for glowing
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		for (NamedTextColor color : NamedTextColor.NAMES.values()) {
			GlowingUtil.getGlowTeam(scoreboard, color);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) return;

		var packet = new WrapperPlayServerEntityMetadata(event);
		Set<UUID> playerUuids = ENTITY_TO_PLAYER_GLOW.get(packet.getEntityId());
		if (playerUuids == null) return;
		if (!(event.getPlayer() instanceof Player player)) return;
		if (!playerUuids.contains(player.getUniqueId())) return;

		for (EntityData<?> entityData : packet.getEntityMetadata()) {
			if (entityData.getIndex() != GLOWING_METADATA_INDEX) continue;

			EntityData<Byte> glowEntityData = (EntityData<Byte>) entityData;
			byte mask = glowEntityData.getValue();
			mask |= 0x40; // Glowing
			glowEntityData.setValue(mask);
			return;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDespawn(EntityRemoveFromWorldEvent event) {
		Entity entity = event.getEntity();
		Set<UUID> uuids = ENTITY_TO_PLAYER_GLOW.remove(entity.getEntityId());
		if (uuids == null) return;

		for (UUID uuid : uuids) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null) continue;

			GlowingUtil.setGlowingColor(player, entity, null);
		}
	}

	/**
	 * Makes the entity glow for the player
	 *
	 * @param player player
	 * @param entity entity
	 */
	public static void addPlayer(Player player, Entity entity) {
		ENTITY_TO_PLAYER_GLOW.computeIfAbsent(entity.getEntityId(), k -> new HashSet<>()).add(player.getUniqueId());
		entity.resendMetadata(GLOWING_METADATA_INDEX);
	}

	/**
	 * Makes the entity no longer glow for the player
	 *
	 * @param player player
	 * @param entity entity
	 */
	public static void removePlayer(Player player, Entity entity) {
		HashSet<UUID> uuids = ENTITY_TO_PLAYER_GLOW.get(entity.getEntityId());
		if (uuids == null) return;
		if (!uuids.remove(player.getUniqueId())) return;

		entity.resendMetadata(GLOWING_METADATA_INDEX);

		if (uuids.isEmpty())
			ENTITY_TO_PLAYER_GLOW.remove(entity.getEntityId());
	}

	/**
	 * Unregisters glow scoreboard teams
	 */
	public static void unregisterTeams() {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		for (NamedTextColor color : NamedTextColor.NAMES.values()) {
			Team team = GlowingUtil.getGlowTeam(scoreboard, color);
			team.unregister();
		}
	}

}
