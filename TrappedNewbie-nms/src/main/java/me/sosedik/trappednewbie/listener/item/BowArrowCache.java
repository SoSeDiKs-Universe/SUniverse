package me.sosedik.trappednewbie.listener.item;

import me.sosedik.kiterino.event.entity.EntityLoadsProjectileEvent;
import me.sosedik.kiterino.inventory.InventorySlotHelper;
import me.sosedik.trappednewbie.TrappedNewbie;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Caches picked arrows for a bow to use for custom models
 */
@NullMarked
public class BowArrowCache implements Listener {

	private static final Map<UUID, ItemStack> CACHE = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLoad(EntityLoadsProjectileEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		Material weaponType = event.getWeapon().getType();
		if (weaponType != Material.BOW && weaponType != Material.CROSSBOW) return;

		ItemStack projectile = event.getProjectile();
		if (ItemStack.isEmpty(projectile)) return;

		CACHE.put(player.getUniqueId(), projectile);

		TrappedNewbie.scheduler().sync(() -> {
			EquipmentSlot hand = player.getActiveItemHand();
			int heldItemSlot = hand == EquipmentSlot.OFF_HAND ? InventorySlotHelper.OFF_HAND : InventorySlotHelper.FIRST_HOTBAR_SLOT + player.getInventory().getHeldItemSlot();
			ItemStack item = player.getInventory().getItem(hand);
			player.sendItem(heldItemSlot, item);
			Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
				if (onlinePlayer != player && onlinePlayer.getWorld() == player.getWorld())
					onlinePlayer.sendEquipmentChange(player, hand, item);
			});
		}, 1L);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuit(PlayerQuitEvent event) {
		CACHE.remove(event.getPlayer().getUniqueId());
	}

	/**
	 * Gets the last used projectile
	 * 
	 * @param player player
	 * @return last used projectile
	 */
	public static @Nullable ItemStack getLastCachedProjectile(Player player) {
		return CACHE.get(player.getUniqueId());
	}

}
