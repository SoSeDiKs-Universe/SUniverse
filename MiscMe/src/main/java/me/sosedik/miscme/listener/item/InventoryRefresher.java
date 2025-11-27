package me.sosedik.miscme.listener.item;

import me.sosedik.miscme.MiscMe;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Constantly updates player's inventory to update items in it.
 * <p>
 * This is useful for items like clock that constantly need updates.
 */
@NullMarked
public class InventoryRefresher implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		startUpdateInventoryTask(player);
	}

	private void startUpdateInventoryTask(Player player) {
		MiscMe.scheduler().sync(task -> {
			if (!player.isOnline()) return true;
			if (player.isDead()) return false;
			if (player.getGameMode() == GameMode.CREATIVE) return false; // TODO figure out why items disappear from cursor in creative?

			player.updateInventory();
			return false;
		}, 10L, 10L);
	}

}
