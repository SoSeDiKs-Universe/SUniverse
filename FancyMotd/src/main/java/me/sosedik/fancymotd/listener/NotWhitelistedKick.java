package me.sosedik.fancymotd.listener;

import com.destroystokyo.paper.event.profile.ProfileWhitelistVerifyEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.connection.PlayerLoginConnection;
import io.papermc.paper.event.connection.PlayerConnectionValidateLoginEvent;
import me.sosedik.fancymotd.Pinger;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Custom whitelist messages
 */
@NullMarked
public class NotWhitelistedKick implements Listener {

	private static final Set<UUID> KICKED_DUE_TO_WHITELIST = new HashSet<>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWhitelistKick(ProfileWhitelistVerifyEvent event) {
		if (!event.isWhitelistEnabled()) return;
		if (event.isWhitelisted()) return;

		UUID uuid = event.getPlayerProfile().getId();
		if (uuid != null)
			KICKED_DUE_TO_WHITELIST.add(uuid);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWhitelistKick(PlayerConnectionValidateLoginEvent event) {
		if (!(event.getConnection() instanceof PlayerLoginConnection connection)) return;

		PlayerProfile profile = connection.getAuthenticatedProfile();
		if (profile == null) {
			profile = connection.getUnsafeProfile();
			if (profile == null) return;
		}
		if (!KICKED_DUE_TO_WHITELIST.remove(profile.getId())) return;
		if (event.isAllowed()) return;

		String ip = connection.getClientAddress().getAddress().getHostAddress();
		var pinger = Pinger.getPinger(ip);
		Component message = Messenger.messenger(pinger.getLanguage()).getMessage("message.kick.not_whitelisted");
		event.kickMessage(message);
	}

}
