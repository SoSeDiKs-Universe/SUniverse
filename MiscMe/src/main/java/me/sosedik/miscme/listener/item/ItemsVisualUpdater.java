package me.sosedik.miscme.listener.item;

import me.sosedik.miscme.MiscMe;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.MathUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Constantly updates player's inventory to update items in it.
 * <p>
 * This is useful for items like clock that constantly need updates.
 */
@NullMarked
public class ItemsVisualUpdater implements Listener {

	private static final Map<UUID, SpeedometerTracker> TRACKERS = new HashMap<>();

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		getSpeedometerTracker(player);
		startUpdateInventoryTask(player);
	}

	private void startUpdateInventoryTask(Player player) {
		MiscMe.scheduler().sync(task -> {
			if (!player.isOnline()) return true;
			if (player.isDead()) return false;

			player.updateInventory();
			return false;
		}, 10L, 10L);
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
		private Location lastLoc;
		private double speed = 0D;

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

			this.speed = this.lastLoc.getWorld() == this.player.getWorld() ? this.lastLoc.distance(this.player.getLocation()) * 20D : 0D;
			this.lastLoc = this.player.getLocation();
		}

		@Override
		public void cancel() {
			super.cancel();
			TRACKERS.remove(this.player.getUniqueId());
		}

		public double getSpeed() {
			return MathUtil.round(this.speed, 3);
		}

		private Component getSpeed(Player player) {
			return Messenger.messenger(player).getMessage("item.speedometer.speed", raw("speed", getSpeed()));
		}

	}

}
