package me.sosedik.socializer.listener;

import com.destroystokyo.paper.event.server.WhitelistToggleEvent;
import me.sosedik.socializer.Socializer;
import me.sosedik.socializer.util.DiscordUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Update server status when needed
 */
@NullMarked
public class DiscordServerStatusUpdater implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		Socializer.scheduler().async(DiscordUtil::updateStatus, 20L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onLeave(PlayerQuitEvent event) {
		Socializer.scheduler().async(DiscordUtil::updateStatus, 20L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWhitelist(WhitelistToggleEvent event) {
		Socializer.scheduler().async(DiscordUtil::updateStatus, 20L);
	}

}
