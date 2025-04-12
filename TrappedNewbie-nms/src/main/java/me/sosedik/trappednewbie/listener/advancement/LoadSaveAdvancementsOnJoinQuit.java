package me.sosedik.trappednewbie.listener.advancement;

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import me.sosedik.packetadvancements.api.event.PlayerReadyForAdvancementsEvent;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieAdvancements;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Load and save advancements
 */
@NullMarked
public class LoadSaveAdvancementsOnJoinQuit implements Listener {

	public LoadSaveAdvancementsOnJoinQuit() {
		Bukkit.getOnlinePlayers().forEach(player -> TrappedNewbie.scheduler().async(() -> TrappedNewbieAdvancements.MANAGER.showTabs(player)));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onReady(PlayerReadyForAdvancementsEvent event) {
		Player player = event.getPlayer();
		TrappedNewbieAdvancements.MANAGER.showTabs(player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onReady(PlayerClientLoadedWorldEvent event) {
		Player player = event.getPlayer();
		TrappedNewbieAdvancements.MANAGER.showTabs(player);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		TrappedNewbieAdvancements.MANAGER.saveProgress(event.getPlayer(), true);
	}

}
