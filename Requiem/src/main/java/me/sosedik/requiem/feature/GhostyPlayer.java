package me.sosedik.requiem.feature;

import me.sosedik.requiem.Requiem;
import me.sosedik.requiem.api.event.player.PlayerStartGhostingEvent;
import me.sosedik.requiem.api.event.player.PlayerStopGhostingEvent;
import me.sosedik.requiem.dataset.RequiemItems;
import me.sosedik.requiem.listener.entity.PrepareGhostMobs;
import me.sosedik.requiem.task.GhostAuraTask;
import me.sosedik.requiem.task.GhostMobVisionTask;
import me.sosedik.utilizer.util.EntityUtil;
import me.sosedik.utilizer.util.MetadataUtil;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Implements after-life ghost mechanic
 */
@NullMarked
public class GhostyPlayer {

	private static final PotionEffect NIGHT_VISION_EFFECT = new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0, false, false, false);
	private static final Set<UUID> GHOSTS = new HashSet<>();
	private static final List<Predicate<Player>> FLIGHT_RULES = new ArrayList<>();
	private static final List<Predicate<Player>> ITEM_RULES = new ArrayList<>();
	private static final String AURA_TASK_KEY = "ghost_aura_task";
	private static final String VISION_TASK_KEY = "ghost_vision_task";

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
		if (isGhost(player)) return;

		for (ItemStack item : player.getInventory()) {
			if (ItemStack.isEmpty(item)) continue;

			player.dropItem(item, true, i -> i.setPickupDelay(5));
			item.setAmount(0);
		}

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
		float speed = player.isSprinting() ? 0.1F : 0.2F;
		player.setWalkSpeed(speed);
		player.setFlySpeed(speed);
		player.setFlyingFallDamage(TriState.FALSE);

		checkCanGhostFly(player);
		checkCanHoldGhostItems(player);

		MetadataUtil.setMetadata(player, AURA_TASK_KEY, new GhostAuraTask(player));
		MetadataUtil.setMetadata(player, VISION_TASK_KEY, new GhostMobVisionTask(player));

		new PlayerStartGhostingEvent(player).callEvent();

		Requiem.logger().info("Making {} a ghost", player.getName());
	}

	/**
	 * Removes ghost status from the player
	 *
	 * @param player player
	 */
	public static void clearGhost(Player player) {
		boolean callEvent = isGhost(player);

		GHOSTS.remove(player.getUniqueId());

		cancelTask(player, AURA_TASK_KEY);
		cancelTask(player, VISION_TASK_KEY);

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
		player.setFlyingFallDamage(TriState.NOT_SET);

		checkCanGhostFly(player);
		checkCanHoldGhostItems(player);

		if (callEvent)
			new PlayerStopGhostingEvent(player).callEvent();

		Requiem.logger().info("Clearing ghost state for {}", player.getName());
	}
	
	private static void cancelTask(Player player, String taskId) {
		MetadataUtil.MetadataValue metadataValue = MetadataUtil.removeMetadata(player, taskId);
		if (metadataValue != null)
			metadataValue.get(BukkitRunnable.class).cancel();
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

	/**
	 * Adds a flight rule
	 *
	 * @param rule flight rule
	 */
	public static void addFlightDenyRule(Predicate<Player> rule) {
		FLIGHT_RULES.add(rule);
	}

	/**
	 * Adds items rule
	 *
	 * @param rule items rule
	 */
	public static void addItemsDenyRule(Predicate<Player> rule) {
		ITEM_RULES.add(rule);
	}

	/**
	 * Checks if this ghost can fly
	 *
	 * @param player player
	 * @return whether the player can fly
	 */
	public static boolean checkCanGhostFly(Player player) {
		if (!isGhost(player)) {
			player.setAllowFlight(false);
			player.setFlying(false);
			return false;
		}

		for (Predicate<Player> predicate : FLIGHT_RULES) {
			if (predicate.test(player)) {
				player.setAllowFlight(false);
				player.setFlying(false);
				return false;
			}
		}

		player.setAllowFlight(true);
		player.setFlying(true);
		return true;
	}

	/**
	 * Checks if this ghost can hold ghost items
	 *
	 * @param player player
	 * @return whether the player can hold ghost items
	 */
	public static boolean checkCanHoldGhostItems(Player player) {
		if (!isGhost(player)) {
			player.getInventory().remove(RequiemItems.GHOST_MOTIVATOR);
			player.getInventory().remove(RequiemItems.GHOST_RELOCATOR);
			return false;
		}

		for (Predicate<Player> predicate : ITEM_RULES) {
			if (predicate.test(player)) {
				player.getInventory().remove(RequiemItems.GHOST_MOTIVATOR);
				player.getInventory().remove(RequiemItems.GHOST_RELOCATOR);
				return false;
			}
		}

		if (!player.getInventory().contains(RequiemItems.GHOST_MOTIVATOR)) player.getInventory().addItem(ItemStack.of(RequiemItems.GHOST_MOTIVATOR));
		if (!player.getInventory().contains(RequiemItems.GHOST_RELOCATOR)) player.getInventory().addItem(ItemStack.of(RequiemItems.GHOST_RELOCATOR));
		return true;
	}

}
