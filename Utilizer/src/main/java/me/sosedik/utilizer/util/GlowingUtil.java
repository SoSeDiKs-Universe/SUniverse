package me.sosedik.utilizer.util;

import me.sosedik.utilizer.listener.entity.EntityGlowTracker;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

@NullMarked
public class GlowingUtil {

	private GlowingUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final String GLOW_TEAM_ID_PREFIX = "Glow-";

	/**
	 * Sets entity's glowing color for the player and refreshes it for the player
	 *
	 * @param player player
	 * @param entity entity
	 * @param glowColor color
	 */
	public static void applyGlowingColor(Player player, Entity entity, @Nullable NamedTextColor glowColor) {
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
	 * Sets entity's glowing color for the player without refreshing
	 *
	 * @param player player
	 * @param entity entity
	 * @param glowColor color
	 */
	public static void setGlowingColor(Player player, Entity entity, @Nullable NamedTextColor glowColor) {
		Scoreboard scoreboard = ScoreboardUtil.getScoreboard(player);
		if (glowColor == null) {
			Team glowTeam = scoreboard.getEntityTeam(entity);
			if (glowTeam != null)
				glowTeam.removeEntity(entity);
		} else {
			Team glowTeam = getGlowTeam(scoreboard, glowColor);
			glowTeam.addEntity(entity);
		}
	}

	/**
	 * Sets global glowing color for the entity
	 *
	 * @param entity entity
	 * @param glowColor color
	 */
	public static void setGlowingColor(Entity entity, @Nullable NamedTextColor glowColor) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		for (NamedTextColor color : NamedTextColor.NAMES.values()) {
			Team glowTeam = getGlowTeam(scoreboard, color);
			if (color == glowColor)
				glowTeam.addEntity(entity);
			else
				glowTeam.removeEntity(entity);
		}
		Bukkit.getOnlinePlayers().forEach(player -> applyGlowingColor(player, entity, glowColor));
	}

	/**
	 * Gets or registers the scoreboard team responsible for glowing
	 *
	 * @param scoreboard scoreboard
	 * @param glowColor glow color
	 * @return scoreboard team
	 */
	public static Team getGlowTeam(Scoreboard scoreboard, NamedTextColor glowColor) {
		Team glowTeam = scoreboard.getTeam(GLOW_TEAM_ID_PREFIX + glowColor);
		if (glowTeam == null) {
			glowTeam = scoreboard.registerNewTeam(GLOW_TEAM_ID_PREFIX + glowColor);
			glowTeam.color(glowColor);
		}
		return glowTeam;
	}

}
