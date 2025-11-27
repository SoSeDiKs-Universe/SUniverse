package me.sosedik.trappednewbie.listener.player;

import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import me.sosedik.trappednewbie.api.task.BossBarTask;
import me.sosedik.trappednewbie.api.task.event.PlayerCompletedTaskEvent;
import me.sosedik.trappednewbie.misc.ProgressionManager;
import me.sosedik.utilizer.Utilizer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Managing tasks :s
 */
public class TaskManagement implements Listener {

	private static final Map<UUID, ProgressionManager> PROGRESSIONS = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onReady(PlayerClientLoadedWorldEvent event) {
		ProgressionManager manager = progressions(event.getPlayer());
		manager.checkProgressionTasks();
		manager.startTasks();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		progressions(player).abort();
		PROGRESSIONS.remove(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onTaskCompletion(PlayerCompletedTaskEvent event) {
		progressions(event.getPlayer()).complete(event.getTask());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLanguageChange(PlayerLocaleChangeEvent event) {
		Utilizer.scheduler().sync(() -> progressions(event.getPlayer()).bossBar().run(), 1L);
	}

	public static ProgressionManager progressions(Player player) {
		return PROGRESSIONS.computeIfAbsent(player.getUniqueId(), k -> new ProgressionManager(player));
	}

	public static BossBarTask bossBar(Player player) {
		return progressions(player).bossBar();
	}

}
