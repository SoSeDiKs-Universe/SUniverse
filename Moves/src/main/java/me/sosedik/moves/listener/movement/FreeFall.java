package me.sosedik.moves.listener.movement;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.entity.TeleportFlag;
import me.sosedik.moves.Moves;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.util.MetadataUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Free-falling mechanics
 */
public class FreeFall implements Listener {

	private static final Set<UUID> LEAPING = new HashSet<>();
	private static final String LEAPING_TAG = "leaping";
	private static final String WAS_FLYING_META = "WasFlyingBeforeLeap";

	@EventHandler(ignoreCancelled = true)
	public void onSneak(@NotNull PlayerToggleSneakEvent event) {
		if (!event.isSneaking()) return;

		Player player = event.getPlayer();
		if (player.isOnGround()) return;
		if (player.isFlying()) return;
		if (player.isGliding()) return;
		if (player.isSwimming()) return;
		if (player.getFallDistance() < 3) return;
		if (isLeaping(player)) return;
		if (SneakCounter.getSneaksCount(player) < 3) return;

		startLeaping(player);
	}

	@EventHandler(ignoreCancelled = true)
	public void onGlide(@NotNull EntityToggleGlideEvent event) {
		if (event.isGliding()) return;
		if (!(event.getEntity() instanceof Player player)) return;
		if (!isLeaping(player)) return;

		event.setCancelled(true);
	}

//	@EventHandler(ignoreCancelled = true)
//	public void onFallInPuddle(@NotNull PlayerFallInWaterPuddleEvent event) { // TODO custom damage
//		if (isLeaping(event.getPlayer()))
//			event.setCancelled(true);
//	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onFlightToggle(@NotNull PlayerToggleFlightEvent event) {
		if (!event.isFlying()) return;

		Player player = event.getPlayer();
		if (!isLeaping(player)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onSave(@NotNull PlayerDataSaveEvent event) {
		if (!event.isQuit()) return;
		if (!isLeaping(event.getPlayer())) return;

		ReadWriteNBT data = event.getData();
		data.setBoolean(LEAPING_TAG, true);

		stopLeaping(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(@NotNull PlayerDataLoadedEvent event) {
		ReadWriteNBT data = event.getData();
		if (!data.hasTag(LEAPING_TAG)) return;

		startLeaping(event.getPlayer());
	}

	/**
	 * Starts free-falling for the player
	 *
	 * @param player player
	 */
	public static void startLeaping(@NotNull Player player) {
		if (!LEAPING.add(player.getUniqueId())) return;

		boolean flying = player.isFlying();
		if (flying) {
			player.setFlying(false);
			MetadataUtil.setMetadata(player, WAS_FLYING_META, true);
		}

		player.setGliding(true);
		Location loc = player.getLocation().center().pitch(90F);
		player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN, TeleportFlag.Relative.YAW, TeleportFlag.Relative.PITCH, TeleportFlag.EntityState.RETAIN_VEHICLE, TeleportFlag.EntityState.RETAIN_PASSENGERS);
		player.emitSound(Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 1.5F);

		Moves.scheduler().sync(task -> {
			if (!isLeaping(player)) return true;

			// Gliding might've been reset by external factors
			player.setGliding(true);

			Location currentLoc = player.getLocation();
			double x = loc.getX() - currentLoc.getX();
			double z = loc.getZ() - currentLoc.getZ();
			player.setVelocity(new Vector(x, player.getVelocity().getY() - 0.3, z));

			if (player.isOnGround() || player.isInWater()) {
				Moves.scheduler().sync(() -> stopLeaping(player), 15L);
				return true;
			}

			if (isSaveFallLocation(player)) {
				player.setFallDistance(0F);
				stopLeaping(player);
				player.setGliding(false);
				return true;
			}

			return false;
		}, 0L, 1L);
	}

	private static boolean isSaveFallLocation(@NotNull Player player) {
		Block block = player.getLocation().getBlock();
		return block.isLiquid()
				|| block.getType() == Material.COBWEB
				|| block.getType() == Material.SCAFFOLDING;
	}

	/**
	 * Stops free-falling for the player
	 *
	 * @param player player
	 */
	public static void stopLeaping(@NotNull Player player) {
		if (!LEAPING.remove(player.getUniqueId())) return;

		var metadata = MetadataUtil.removeMetadata(player, WAS_FLYING_META);
		if (metadata != null)
			player.setFlying(true);
	}

	/**
	 * Checks if the player is currently leaping
	 *
	 * @param player player
	 * @return whether player is leaping
	 */
	public static boolean isLeaping(@NotNull Player player) {
		return LEAPING.contains(player.getUniqueId());
	}

}
