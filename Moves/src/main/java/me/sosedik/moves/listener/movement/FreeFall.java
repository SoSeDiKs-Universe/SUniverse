package me.sosedik.moves.listener.movement;

import me.sosedik.moves.Moves;
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
import org.bukkit.event.player.PlayerQuitEvent;
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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onFlightToggle(@NotNull PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		if (!isLeaping(player)) return;

		if (event.isFlying())
			stopLeaping(player);
		else
			MetadataUtil.removeMetadata(player, WAS_FLYING_META);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(@NotNull PlayerQuitEvent event) {
		stopLeaping(event.getPlayer()); // ToDo: persist leaping
	}

	private static void startLeaping(@NotNull Player player) {
		if (!LEAPING.add(player.getUniqueId())) return;

		boolean flying = player.isFlying();
		if (flying) {
			player.setFlying(false);
			MetadataUtil.setMetadata(player, WAS_FLYING_META, true);
		}

		player.setGliding(true);
		Location loc = player.getLocation().center().pitch(90F);
		player.teleport(loc);
		player.emitSound(Sound.ENTITY_ENDER_DRAGON_FLAP, 1F, 1.5F);

		Moves.scheduler().sync(task -> {
			if (!isLeaping(player)) return true;

			Location currentLoc = player.getLocation();
			double x = loc.getX() - currentLoc.getX();
			double z = loc.getZ() - currentLoc.getZ();
			player.setVelocity(new Vector(x, player.getVelocity().getY() - 0.3, z));

			if (player.isOnGround()) {
				Moves.scheduler().async(() -> stopLeaping(player), 15L);
				return true;
			}

			Block block = player.getLocation().getBlock();
			if (block.isLiquid() || block.getType() == Material.COBWEB || block.getType() == Material.SCAFFOLDING) {
				player.setFallDistance(0F);
				stopLeaping(player);
				player.setGliding(false);
				return true;
			}

			return false;
		}, 0L, 1L);
	}

	private static void stopLeaping(@NotNull Player player) {
		if (!LEAPING.remove(player.getUniqueId())) return;

		var metadata = MetadataUtil.removeMetadata(player, WAS_FLYING_META);
		if (metadata != null)
			player.setFlying(true);
	}

	/**
	 * Sets or removes leaping state
	 *
	 * @param player player
	 * @param leaping new leaping state
	 */
	public static void setLeaping(@NotNull Player player, boolean leaping) {
		if (leaping)
			startLeaping(player);
		else
			stopLeaping(player);
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
