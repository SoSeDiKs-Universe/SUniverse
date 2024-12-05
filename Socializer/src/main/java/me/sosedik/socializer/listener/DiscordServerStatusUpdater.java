package me.sosedik.socializer.listener;

import com.destroystokyo.paper.event.server.WhitelistToggleEvent;
import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.util.DiscordUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Update server status when needed
 */
public class DiscordServerStatusUpdater implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(@NotNull PlayerJoinEvent event) {
		Socializer.scheduler().async(DiscordUtil::updateStatus, 20L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(@NotNull PlayerQuitEvent event) {
		Socializer.scheduler().async(DiscordUtil::updateStatus, 20L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWhitelist(@NotNull WhitelistToggleEvent event) {
		Socializer.scheduler().async(DiscordUtil::updateStatus, 20L);
	}

}
