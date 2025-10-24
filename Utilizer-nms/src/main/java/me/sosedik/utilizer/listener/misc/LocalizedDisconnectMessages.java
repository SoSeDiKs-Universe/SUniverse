package me.sosedik.utilizer.listener.misc;

import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Localize disconnect messages
 */
@NullMarked
public class LocalizedDisconnectMessages implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onKick(PlayerKickEvent event) {
		String reason = ChatUtil.getPlainText(event.reason());
		Component message = Messenger.messenger(event.getPlayer()).getMessageIfExists(reason);
		if (message != null)
			event.reason(message);
	}

}
