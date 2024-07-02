package me.sosedik.fancymotd.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.sosedik.fancymotd.Pinger;
import me.sosedik.fancymotd.feature.MotdIconStorage;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
import static me.sosedik.utilizer.api.message.Mini.component;
import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Custom motd messages
 */
public class MotdRandomizer implements Listener {

	private static final String SPACER = LegacyComponentSerializer.legacySection().serialize(Component.text("..", NamedTextColor.BLACK));
	private static final String VERSION = " [" + Bukkit.getMinecraftVersion() + "]";

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

		// Get ip and obtain Pinger instance
		String ip = event.getClient().getAddress().getAddress().getHostAddress();
		var pinger = Pinger.getPinger(ip);
		Messenger messenger = Messenger.messenger(pinger.getLanguage());

		// Custom players display
		var customDisplay = messenger.getMessage(whitelist ? "motd.playerlist.whitelist" : "fancymotd.placeholder.discord.raw");
		if (customDisplay != Component.empty()) {
			List<PaperServerListPingEvent.ListedPlayerInfo> playerProfiles = event.getListedPlayers();
			if (whitelist)
				playerProfiles.clear();
			String display = LegacyComponentSerializer.legacySection().serialize(customDisplay);
			String[] displays = display.split("\n", -1);
			for (var i = 0; i < displays.length; i++) {
				String line = displays[i];
				String coloredSpace = getColoredSpace(line);
				line = line.replace(" ", coloredSpace);
				playerProfiles.add(i, new PaperServerListPingEvent.ListedPlayerInfo(line, UUID.randomUUID()));
			}
		}

		// Special message if whitelisted
		if (whitelist) {
			event.setVersion(String.join("", messenger.getRawMessage("motd.whitelist")));
			Component motd = combine(Component.newline(),
				messenger.getMessage("motd.whitelist.header"),
				messenger.getMessage("motd.whitelist.splash")
			);
			event.motd(motd);
			return;
		}

		// Special message for outdated clients
		if (event.getClient().getProtocolVersion() < event.getProtocolVersion()) {
			Component motd = combine(Component.newline(),
				messenger.getMessage("motd.header", component("clock_time", getTime(pinger, messenger))),
				messenger.getMessage("motd.outdated_client")
			);
			event.motd(motd);
			return;
		}

		// Special welcome for newbies
		if (pinger.isNewbie()) {
			Component motd = combine(Component.newline(),
				messenger.getMessage("motd.welcome.header"),
				messenger.getMessage("motd.welcome.splash")
			);
			event.motd(motd);
			return;
		}

		// Random motd
		Component motd = combine(Component.newline(),
			messenger.getMessage("motd.header", component("clock_time", getTime(pinger, messenger))),
			messenger.getMessage("motd.splash")
		);
		event.motd(motd);
	}

	// Player names in motd do not support modern colors, using legacy hack
	@SuppressWarnings("deprecation")
	private @NotNull String getColoredSpace(@NotNull String line) {
		return SPACER + ChatColor.RESET + ChatColor.getLastColors(line);
	}

	private @NotNull Component getTime(@NotNull Pinger pinger, @NotNull Messenger messenger) {
		if (!pinger.hasClock()) return Component.empty();
		int h = (int) (Bukkit.getWorlds().getFirst().getTime() / 1000) + 6;
		if (h > 23)
			h -= 24;
		int m = (int) ((60 * (Bukkit.getWorlds().getFirst().getTime() % 1000)) / 1000);
		return messenger.getMessage("motd.clock_time", raw("hours", (h < 10 ? "0" : "") + h), raw("minutes", (m < 10 ? "0" : "") + m));
	}


}
