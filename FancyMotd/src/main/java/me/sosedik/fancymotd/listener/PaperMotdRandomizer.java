package me.sosedik.fancymotd.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.sosedik.fancymotd.feature.MotdIconStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.combine;

/**
 * Custom motd messages
 */
public class PaperMotdRandomizer implements Listener {

	private static final String VERSION = " [" + Bukkit.getMinecraftVersion() + "]";
	private static final MiniMessage MINI = MiniMessage.miniMessage();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPing(@NotNull PaperServerListPingEvent event) {
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
			listedPlayers.add(getListedPlayerInfo());
			Component motd = combine(Component.newline(),
				MINI.deserialize("<#37BBF5>SoSeDiK's <#ECF3FC>Universe <red>♥"),
				MINI.deserialize("<red>Have a good day!")
			);
			event.motd(motd);
			return;
		}

		// Special message for outdated clients
		if (event.getClient().getProtocolVersion() < event.getProtocolVersion()) {
			Component motd = combine(Component.newline(),
				MINI.deserialize("<#37BBF5>SoSeDiK's <#ECF3FC>Universe <red>♥"),
				MINI.deserialize("<red>Your client version is outdated! :(")
			);
			event.motd(motd);
			return;
		}

		// Random motd
		Component motd = combine(Component.newline(),
			MINI.deserialize("<#37BBF5>SoSeDiK's <#ECF3FC>Universe <red>♥"),
				MINI.deserialize("<gray>Testing random stuff")
		);
		event.motd(motd);
	}

	@SuppressWarnings("deprecation")
	private @NotNull PaperServerListPingEvent.ListedPlayerInfo getListedPlayerInfo() {
		return new PaperServerListPingEvent.ListedPlayerInfo(ChatColor.RED + "Server is under maintenance", UUID.randomUUID());
	}

}
