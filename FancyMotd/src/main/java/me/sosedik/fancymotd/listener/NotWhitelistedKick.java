package me.sosedik.fancymotd.listener;

import me.sosedik.fancymotd.Pinger;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Custom whitelist messages
 */
@NullMarked
public class NotWhitelistedKick implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWhitelistKick(PlayerLoginEvent event) {
		if (!PlayerLoginEvent.Result.KICK_WHITELIST.equals(event.getResult())) return;

		String ip = event.getAddress().getHostAddress();
		var pinger = Pinger.getPinger(ip);
		Component message = Messenger.messenger(pinger.getLanguage()).getMessage("message.kick.not_whitelisted");
		event.kickMessage(message);
	}

}
