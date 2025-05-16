package me.sosedik.miscme.listener.player;

import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.MathUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Tracks player's movement speed
 */
@NullMarked
public class PlayerSpeedTracker implements Listener {

	private static final Map<UUID, SpeedometerTracker> TRACKERS = new HashMap<>();

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		getSpeedometerTracker(player);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event) {
		getSpeedometerTracker(event.getPlayer()).setImmuneTicks(2);
	}

	/**
	 * Gets the player's speed
	 *
	 * @param player player
	 * @return player's speed
	 */
	public static double getSpeed(Player player) {
		return getSpeedometerTracker(player).getSpeed();
	}

	private static SpeedometerTracker getSpeedometerTracker(Player player) {
		return TRACKERS.computeIfAbsent(player.getUniqueId(), k -> new SpeedometerTracker(player));
	}

	private static class SpeedometerTracker extends BukkitRunnable {

		private final Player player;
		private final List<Double> speeds = new ArrayList<>();
		private double lastSpeed = 0;
		private double speed = 0;
		private Location lastLoc;
		private int immuneTicks = 0;

		public SpeedometerTracker(Player player) {
			this.player = player;
			this.lastLoc = player.getLocation();

			MiscMe.scheduler().sync(this, 1L, 1L);
		}

		@Override
		public void run() {
			if (!this.player.isOnline()) {
				cancel();
				return;
			}

			if (this.speeds.size() > 30)
				this.speeds.removeFirst();

			if (this.immuneTicks > 0) {
				this.immuneTicks--;
			} else {
				if (this.lastLoc.getWorld() == this.player.getWorld()) {
					double speed = this.lastLoc.distance(this.player.getLocation()) * 20D;
					this.lastSpeed = speed;
					this.speeds.add(speed);
				}
			}

			double speed = 0;
			if (!this.speeds.isEmpty()) {
				for (double s : this.speeds)
					speed += s;
				speed = speed / this.speeds.size();
			}

			this.speed = speed;
			this.lastLoc = this.player.getLocation();
		}

		@Override
		public void cancel() {
			super.cancel();
			TRACKERS.remove(this.player.getUniqueId());
		}

		public void setImmuneTicks(int ticks) {
			this.immuneTicks = ticks;
		}

		public double getSpeed() {
			if (this.lastSpeed == 0) return 0;
			if (this.speeds.size() < 10) return 0;
			return MathUtil.round(speed, 3);
		}

		private Component getSpeed(Player player) {
			return Messenger.messenger(player).getMessage("item.speedometer.speed", raw("speed", getSpeed()));
		}

	}

}
