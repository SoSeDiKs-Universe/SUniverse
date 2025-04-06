package me.sosedik.requiem.task;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.feature.GhostyPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;

/**
 * Ghosts spawn particles and scare nearby mobs
 */
@NullMarked
public class GhostAuraTask extends BukkitRunnable {

	private final Player player;

	public GhostAuraTask(Player player) {
		this.player = player;
		Requiem.scheduler().sync(this, 1L, 5L);
	}

	@Override
	public void run() {
		if (!player.isOnline() || !GhostyPlayer.isGhost(player)) {
			cancel();
			return;
		}

		player.sendRedTint(1);
		Location loc = player.getLocation().addY(player.getHeight() / 2);
		player.spawnParticle(Particle.SOUL, loc, 2, 0.03, 0.08, 0.03, 0.04);
		loc.getNearbyEntitiesByType(Animals.class, 2.5).forEach(animal -> animal.setPanicTicks(80));
	}

	@Override
	public void cancel() {
		super.cancel();

		player.sendRedTint(0);
	}

}
