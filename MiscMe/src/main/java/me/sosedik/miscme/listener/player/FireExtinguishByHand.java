package me.sosedik.miscme.listener.player;

import me.sosedik.miscme.MiscMe;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Extinguishing fire by hand sets you on fire
 */
@NullMarked
public class FireExtinguishByHand implements Listener {

	private static final Map<UUID, Integer> IMMUNE_TO_MECHANIC = new HashMap<>();

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onFireExtinguish(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

		Block block = event.getClickedBlock();
		if (block == null) return;

		if (!Tag.FIRE.isTagged(block.getType())) {
			block = block.getRelative(event.getBlockFace());
			if (!Tag.FIRE.isTagged(block.getType()))
				return;
		}

		Player player = event.getPlayer();
		if (!player.getInventory().getItemInMainHand().isEmpty()) return;

		if (hasFireExtinguishImmunity(player)) {
			event.setCancelled(true);
			return;
		}

		player.setFireTicks(100);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		removeFireImmunity(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		removeFireImmunity(event.getPlayer());
	}

	/**
	 * Adds fire immunity for specified amount of ticks
	 *
	 * @param player player
	 * @param ticks time
	 */
	public static void addFireImmunity(Player player, int ticks) {
		UUID uuid = player.getUniqueId();
		Integer currentImmunity = IMMUNE_TO_MECHANIC.get(uuid);
		if (currentImmunity != null) {
			IMMUNE_TO_MECHANIC.put(uuid, Math.max(ticks, currentImmunity));
			return;
		}

		IMMUNE_TO_MECHANIC.put(uuid, ticks);
		MiscMe.scheduler().sync(task -> {
			Integer immunity = IMMUNE_TO_MECHANIC.get(uuid);
			if (immunity == null) return true;

			immunity--;
			if (immunity <= 0)
				removeFireImmunity(player);
			else
				IMMUNE_TO_MECHANIC.put(uuid, immunity);

			return false;
		}, 1L, 1L);
	}

	private static void removeFireImmunity(Player player) {
		IMMUNE_TO_MECHANIC.remove(player.getUniqueId());
	}

	/**
	 * Checks whether this player has fire immunity
	 *
	 * @param player player
	 * @return whether this player has fire immunity
	 */
	public static boolean hasFireExtinguishImmunity(Player player) {
		return IMMUNE_TO_MECHANIC.containsKey(player.getUniqueId());
	}

}
