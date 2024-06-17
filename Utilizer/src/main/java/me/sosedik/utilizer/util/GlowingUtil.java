package me.sosedik.utilizer.util;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import me.sosedik.utilizer.listener.entity.EntityGlowTracker;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class GlowingUtil {

	private GlowingUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final String GLOW_TEAM_ID_PREFIX = "Glow-";

	/**
	 * Sets entity's glowing color for the player
	 *
	 * @param player player
	 * @param entity entity
	 * @param glowColor color
	 */
	public static void applyGlowingColor(@NotNull Player player, @NotNull Entity entity, @Nullable NamedTextColor glowColor) {
		setGlowingColor(player, entity, glowColor);
		byte mask = 0x00;
		if (entity.getFireTicks() > 0 || entity.isVisualFire()) {
			mask |= 0x01;
		}
		if (entity.getPose() == Pose.SNEAKING) {
			mask |= 0x02;
		}
		if (entity instanceof Player livingEntity && livingEntity.isSprinting()) {
			mask |= 0x08;
		}
		if (entity.getPose() == Pose.SWIMMING) {
			mask |= 0x10;
		}
		if (entity instanceof LivingEntity livingEntity && livingEntity.isInvisible()) {
			mask |= 0x20;
		}
		if (glowColor != null) {
			mask |= 0x40;
			EntityGlowTracker.getPlayers(entity.getEntityId(), true).add(player.getUniqueId());
		} else {
			List<UUID> playerUuids = EntityGlowTracker.getPlayers(entity.getEntityId(), false);
			if (playerUuids != null)
				playerUuids.remove(player.getUniqueId());
		}
		if (entity.getPose() == Pose.FALL_FLYING) {
			mask |= (byte) 0x80;
		}
		var entityData = List.of(new EntityData(0, EntityDataTypes.BYTE, mask));
		var packet = new WrapperPlayServerEntityMetadata(entity.getEntityId(), entityData);
		PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
	}

	/**
	 * Sets entity's glowing color for the player
	 *
	 * @param player player
	 * @param entity entity
	 * @param glowColor color
	 */
	public static void setGlowingColor(@NotNull Player player, @NotNull Entity entity, @Nullable NamedTextColor glowColor) {
		Scoreboard scoreboard = ScoreboardUtil.getScoreboard(player);
		for (NamedTextColor color : NamedTextColor.NAMES.values()) {
			Team glowTeam = getGlowTeam(scoreboard, color);
			glowTeam.removeEntity(entity);
			if (color == glowColor)
				glowTeam.addEntity(entity);
			else
				glowTeam.removeEntity(entity);
		}
	}

	/**
	 * Sets global glowing color for the entity
	 *
	 * @param entity entity
	 * @param glowColor color
	 */
	public static void setGlowingColor(@NotNull Entity entity, @Nullable NamedTextColor glowColor) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		for (NamedTextColor color : NamedTextColor.NAMES.values()) {
			Team glowTeam = getGlowTeam(scoreboard, color);
			if (color == glowColor)
				glowTeam.addEntity(entity);
			else
				glowTeam.removeEntity(entity);
		}
	}

	/**
	 * Gets or registers the scoreboard team responsible for glowing
	 *
	 * @param scoreboard scoreboard
	 * @param glowColor glow color
	 * @return scoreboard team
	 */
	public static @NotNull Team getGlowTeam(@NotNull Scoreboard scoreboard, @NotNull NamedTextColor glowColor) {
		Team glowTeam = scoreboard.getTeam(GLOW_TEAM_ID_PREFIX + glowColor);
		if (glowTeam == null) {
			glowTeam = scoreboard.registerNewTeam(GLOW_TEAM_ID_PREFIX + glowColor);
			glowTeam.color(glowColor);
		}
		return glowTeam;
	}

}
