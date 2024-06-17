package me.sosedik.requiem.task;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.MobPossessing;
import me.sosedik.utilizer.util.GlowingUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Ghosts can see other mobs
 */
public class GhostMobVisionTask extends BukkitRunnable {

	private final Player player;
	private final Map<UUID, LivingEntity> glowingMobs = new HashMap<>();

	public GhostMobVisionTask(@NotNull Player player) {
		this.player = player;
		Requiem.scheduler().sync(this, 1L, 20L);
	}

	@Override
	public void run() {
		if (!player.isOnline() || !GhostyPlayer.isGhost(player)) {
			cancel();
			if (player.isOnline()) glowingMobs.forEach((uuid, mob) -> clearGlowing(mob));
			glowingMobs.clear();
			return;
		}

		Location loc = player.getLocation().addY(player.getHeight() / 2);

		new ArrayList<>(glowingMobs.values()).forEach(mob -> {
			Location mobLoc = mob.getLocation();
			if (!loc.getWorld().getUID().equals(mobLoc.getWorld().getUID())) {
				glowingMobs.remove(mob.getUniqueId());
				return;
			}
			if (mobLoc.distanceSquared(loc) < 900) return;
			glowingMobs.remove(mob.getUniqueId());
			clearGlowing(mob);
		});

		loc.getNearbyLivingEntities(25, entity -> !glowingMobs.containsKey(entity.getUniqueId()) && !shouldSkipGlow(entity)).forEach(entity -> {
			glowingMobs.put(entity.getUniqueId(), entity);
			NamedTextColor glowColor;
			if (MobPossessing.hasSoul(entity))
				glowColor = NamedTextColor.GREEN;
			else if (MobPossessing.isAllowedForCapture(player, entity))
				glowColor = NamedTextColor.YELLOW;
			else
				glowColor = NamedTextColor.RED;
			GlowingUtil.applyGlowingColor(player, entity, glowColor);
		});
	}

	private boolean shouldSkipGlow(@NotNull LivingEntity entity) {
		if (entity.isInvisible()) return true;
		return switch (entity.getType()) {
			case ARMOR_STAND, PLAYER -> true;
			default -> false;
		};
	}

	private void clearGlowing(@NotNull LivingEntity entity) {
		GlowingUtil.applyGlowingColor(player, entity, null);
	}

}
