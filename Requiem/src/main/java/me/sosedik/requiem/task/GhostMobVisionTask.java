package me.sosedik.requiem.task;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import me.sosedik.requiem.feature.PossessingPlayer;
import me.sosedik.utilizer.util.GlowingUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Ghosts can see other mobs
 */
@NullMarked
public class GhostMobVisionTask extends BukkitRunnable {

	private final Player player;
	private final Map<UUID, LivingEntity> glowingMobs = new HashMap<>();

	public GhostMobVisionTask(Player player) {
		this.player = player;
		Requiem.scheduler().sync(this, 1L, 20L);
	}

	@Override
	public void run() {
		if (!this.player.isOnline() || !GhostyPlayer.isGhost(this.player)) {
			cancel();
			return;
		}

		Location loc = this.player.getLocation().addY(this.player.getHeight() / 2);

		new ArrayList<>(this.glowingMobs.values()).forEach(mob -> {
			Location mobLoc = mob.getLocation();
			if (loc.getWorld() != mobLoc.getWorld()) {
				this.glowingMobs.remove(mob.getUniqueId());
				return;
			}
			if (mobLoc.distanceSquared(loc) < 900) return;

			this.glowingMobs.remove(mob.getUniqueId());
			clearGlowing(mob);
		});

		loc.getNearbyLivingEntities(25, entity -> !shouldSkipGlow(entity)).forEach(entity -> {
			this.glowingMobs.put(entity.getUniqueId(), entity);
			NamedTextColor glowColor;
			if (PossessingPlayer.isAllowedForCapture(this.player, entity))
				glowColor = NamedTextColor.GREEN;
			else if (PossessingPlayer.isPossessable(entity))
				glowColor = NamedTextColor.YELLOW;
			else
				glowColor = NamedTextColor.RED;
			GlowingUtil.applyGlowingColor(this.player, entity, glowColor);
		});
	}

	@Override
	public void cancel() {
		super.cancel();
		if (this.player.isOnline()) this.glowingMobs.forEach((uuid, mob) -> clearGlowing(mob));
		this.glowingMobs.clear();
	}

	private boolean shouldSkipGlow(LivingEntity entity) {
		if (entity.isInvisible()) return true;
		if (!entity.getType().isSpawnable()) return true;
		return !entity.getType().isAlive();
	}

	private void clearGlowing(LivingEntity entity) {
		GlowingUtil.applyGlowingColor(this.player, entity, null);
	}

}
