package me.sosedik.utilizer.listener.entity;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.sosedik.utilizer.util.GlowingUtil;
import me.sosedik.utilizer.util.ScoreboardUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implements per-player glowing
 */
public class EntityGlowTracker implements PacketListener, Listener {

	private static final Map<Integer, List<UUID>> ENTITY_TO_PLAYER_GLOW = new HashMap<>();

	public EntityGlowTracker() {
		// Init teams responsible for glowing
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		for (NamedTextColor color : NamedTextColor.NAMES.values()) {
			GlowingUtil.getGlowTeam(scoreboard, color);
		}
	}

	@Override
	public void onPacketSend(@NotNull PacketSendEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.ENTITY_METADATA) return;

		var packet = new WrapperPlayServerEntityMetadata(event);
		List<UUID> playerUuids = ENTITY_TO_PLAYER_GLOW.get(packet.getEntityId());
		if (playerUuids == null) return;
		if (!(event.getPlayer() instanceof Player player)) return;
		if (!playerUuids.contains(player.getUniqueId())) return;

		for (EntityData entityData : packet.getEntityMetadata()) {
			if (entityData.getIndex() != 0) continue;

			byte mask = (byte) entityData.getValue();
			mask |= 0x40;
			entityData.setValue(mask);
			return;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDespawn(@NotNull EntityRemoveFromWorldEvent event) {
		Entity entity = event.getEntity();
		List<UUID> uuids = ENTITY_TO_PLAYER_GLOW.remove(entity.getEntityId());
		if (uuids == null) return;

		for (UUID uuid : uuids) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null) continue;

			GlowingUtil.setGlowingColor(player, entity, null);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onJoin(@NotNull PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Scoreboard scoreboard = ScoreboardUtil.getScoreboard(player);
		registerToScoreboard(scoreboard);
	}

	private void registerToScoreboard(@NotNull Scoreboard scoreboard) {
		for (NamedTextColor color : NamedTextColor.NAMES.values()) {
			GlowingUtil.getGlowTeam(scoreboard, color);
		}
	}

	@Contract("_, true -> !null")
	public static @Nullable List<@NotNull UUID> getPlayers(int entityId, boolean compute) {
		return compute ? ENTITY_TO_PLAYER_GLOW.computeIfAbsent(entityId, k -> new ArrayList<>()) : ENTITY_TO_PLAYER_GLOW.get(entityId);
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
