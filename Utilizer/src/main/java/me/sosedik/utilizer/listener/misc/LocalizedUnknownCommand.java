package me.sosedik.utilizer.listener.misc;

import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Localize unknown command message
 */
@NullMarked
public class LocalizedUnknownCommand implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onUnknownCommand(UnknownCommandEvent event) {
		Component ogMessage = event.message();
		if (ogMessage == null) return;

		String reason = ChatUtil.getPlainText(ogMessage);
		Component message = Messenger.messenger(event.getSender()).getMessageIfExists(reason);
		if (message != null)
			event.message(message);
	}

}
