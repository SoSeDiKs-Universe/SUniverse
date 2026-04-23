package me.sosedik.fancymotd.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.sosedik.fancymotd.feature.MotdIconStorage;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;
import org.jspecify.annotations.NullMarked;

/**
 * Base class for MOTD randomization listeners
 */
@NullMarked
public abstract class AbstractMotdRandomizer implements Listener {

	private static final String VERSION = " [" + Bukkit.getMinecraftVersion() + "]";
	protected static final int MAX_PROTOCOL_VERSION = Integer.MAX_VALUE;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public final void onPing(PaperServerListPingEvent event) {
		// Change version requirement if whitelist
		boolean whitelist = Bukkit.hasWhitelist();
		if (whitelist) {
			event.setProtocolVersion(MAX_PROTOCOL_VERSION);
		} else {
			event.setVersion(Bukkit.getServerName() + VERSION);
		}

		// Apply random icon
		CachedServerIcon icon = MotdIconStorage.getRandomIcon();
		if (icon != null)
			event.setServerIcon(icon);

		// Delegate to subclass for MOTD content
		event.motd(getMotd(event, whitelist));
	}

	/**
	 * Gets the MOTD component for the event
	 *
	 * @param event     the ping event
	 * @param whitelist whether the server has whitelist enabled
	 * @return MOTD component
	 */
	protected abstract Component getMotd(PaperServerListPingEvent event, boolean whitelist);

}
