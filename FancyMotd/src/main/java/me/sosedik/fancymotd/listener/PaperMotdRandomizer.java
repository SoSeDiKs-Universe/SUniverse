package me.sosedik.fancymotd.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.sosedik.fancymotd.feature.MotdIconStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

/**
 * Custom motd messages
 */
@NullMarked
public class PaperMotdRandomizer implements Listener {

	private static final String VERSION = " [" + Bukkit.getMinecraftVersion() + "]";
	private static final MiniMessage MINI = MiniMessage.miniMessage();
	private static final PaperServerListPingEvent.ListedPlayerInfo MAINTENANCE_INFO = new PaperServerListPingEvent.ListedPlayerInfo(LegacyComponentSerializer.legacySection().serialize(Component.text("Server is under maintenance", NamedTextColor.RED)), UUID.randomUUID());

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPing(PaperServerListPingEvent event) {
		// Change version requirement if whitelist
		boolean whitelist = Bukkit.hasWhitelist();
		if (whitelist) event.setProtocolVersion(Integer.MAX_VALUE);
		else event.setVersion(Bukkit.getServerName() + VERSION);

		// Apply random icon
		CachedServerIcon icon = MotdIconStorage.getRandomIcon();
		if (icon != null)
			event.setServerIcon(icon);

		// Special message if whitelisted
		if (whitelist) {
			event.setVersion("On maintenance!");
			List<PaperServerListPingEvent.ListedPlayerInfo> listedPlayers = event.getListedPlayers();
			listedPlayers.clear();
			listedPlayers.add(MAINTENANCE_INFO);
			Component motd = Component.textOfChildren(Component.newline(),
				MINI.deserialize("<#37BBF5>SoSeDiK's <#ECF3FC>Universe <red>♥"),
				MINI.deserialize("<red>Have a good day!")
			);
			event.motd(motd);
			return;
		}

		// Special message for outdated clients
		if (event.getClient().getProtocolVersion() < event.getProtocolVersion()) {
			Component motd = Component.textOfChildren(Component.newline(),
				MINI.deserialize("<#37BBF5>SoSeDiK's <#ECF3FC>Universe <red>♥"),
				MINI.deserialize("<red>Your client version is outdated! :(")
			);
			event.motd(motd);
			return;
		}

		// Random motd
		Component motd = Component.textOfChildren(Component.newline(),
			MINI.deserialize("<#37BBF5>SoSeDiK's <#ECF3FC>Universe <red>♥"),
			MINI.deserialize("<gray>Testing random stuff")
		);
		event.motd(motd);
	}

}
