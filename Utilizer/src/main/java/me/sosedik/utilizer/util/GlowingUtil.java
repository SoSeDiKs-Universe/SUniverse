package me.sosedik.utilizer.util;

import me.sosedik.utilizer.listener.entity.EntityGlowTracker;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
		if (glowColor != null) {
			EntityGlowTracker.getPlayers(entity.getEntityId(), true).add(player.getUniqueId());
		} else {
			List<UUID> playerUuids = EntityGlowTracker.getPlayers(entity.getEntityId(), false);
			if (playerUuids != null)
				playerUuids.remove(player.getUniqueId());
		}
		entity.resendMetadata(0);
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
