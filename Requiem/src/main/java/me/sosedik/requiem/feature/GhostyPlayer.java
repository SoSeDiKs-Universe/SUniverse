package me.sosedik.requiem.feature;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.listener.entity.PrepareGhostMobs;
import me.sosedik.requiem.task.GhostAuraTask;
import me.sosedik.requiem.task.GhostMobVisionTask;
import me.sosedik.utilizer.util.EntityUtil;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Implements after-life ghost mechanic
 */
@NullMarked
public class GhostyPlayer {

	private static final PotionEffect NIGHT_VISION_EFFECT = new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, false, false, false);
	private static final Set<UUID> GHOSTS = new HashSet<>();

	/**
	 * Checks whether this player is a ghost
	 *
	 * @param player player
	 * @return whether player is a ghost
	 */
	public static boolean isGhost(Player player) {
		return GHOSTS.contains(player.getUniqueId());
	}

	/**
	 * Turns the player into ghost
	 *
	 * @param player player
	 */
	public static void markGhost(Player player) {
		GHOSTS.add(player.getUniqueId());

		EntityUtil.clearTargets(player);

		// Hide player from non-ghosts
		PrepareGhostMobs.hideVisibility(player, true);
		for (UUID uuid : GHOSTS) {
			Player onlinePlayer = Bukkit.getPlayer(uuid);
			if (onlinePlayer == null) continue;
			if (onlinePlayer == player) continue;
			if (!isGhost(onlinePlayer)) continue;

			PrepareGhostMobs.addVisible(player, onlinePlayer);
			PrepareGhostMobs.addVisible(onlinePlayer, player);
		}

		// Ghost attributes
		player.setHealth(player.getMaxHealth());
		player.setCollidable(false);
		player.lockFreezeTicks(true);
		player.setSleepingIgnored(true);
		player.setInvisible(true);
		player.setCanPickupItems(false);

		// Ghost abilities
		player.addPotionEffect(NIGHT_VISION_EFFECT);
		player.setAllowFlight(true);
		float speed = player.isSprinting() ? 0.1F : 0.2F;
		player.setWalkSpeed(speed);
		player.setFlySpeed(speed);
		player.setFlying(true);
		player.setFlyingFallDamage(TriState.FALSE);

		player.getInventory().setItem(0, new ItemStack(RequiemItems.GHOST_MOTIVATOR));

		new GhostAuraTask(player);
		new GhostMobVisionTask(player);

		Requiem.logger().info("Making {} a ghost", player.getName());
	}

	/**
	 * Removes ghost status from the player
	 *
	 * @param player player
	 */
	public static void clearGhost(Player player) {
		GHOSTS.remove(player.getUniqueId());

		PrepareGhostMobs.hideVisibility(player, false);

		// Restore attributes
		player.setCollidable(true);
		player.lockFreezeTicks(false);
		player.setSleepingIgnored(false);
		player.setInvisible(false);
		player.setCanPickupItems(true);

		// Remove abilities
		player.removePotionEffect(PotionEffectType.NIGHT_VISION);
		player.setWalkSpeed(0.2F);
		player.setFlySpeed(0F);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setFlyingFallDamage(TriState.NOT_SET);

		player.getInventory().remove(RequiemItems.GHOST_MOTIVATOR);

		Requiem.logger().info("Clearing ghost state for {}", player.getName());
	}

	/**
	 * Loads player's ghost data
	 *
	 * @param player player
	 * @return whether the player is now a ghost
	 */
	public static boolean loadGhostData(Player player) {
		if (!player.isInvisible()) return false;
		if (player.getGameMode().isInvulnerable()) return false;

		GhostyPlayer.markGhost(player);
		player.setCooldown(RequiemItems.GHOST_RELOCATOR, 5 * 20);

		return true;
	}

	/**
	 * Saves all data, should be called on server shutdown
	 */
	public static void saveAllData() {
		for (UUID uuid : GHOSTS) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null) continue;

			clearGhost(player);
			player.setInvisible(true);
		}
		GHOSTS.clear();
	}

}
