package me.sosedik.fancymotd.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.sosedik.fancymotd.Pinger;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.component;
import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Custom motd messages
 */
@NullMarked
public class MotdRandomizer extends AbstractMotdRandomizer {

	private static final String SPACER = LegacyComponentSerializer.legacySection().serialize(Component.text("..", NamedTextColor.BLACK));

	// Time calculation constants
	private static final int TICKS_PER_HOUR = 1000;
	private static final int HOURS_PER_DAY = 24;
	private static final int MINUTES_PER_HOUR = 60;

	@Override
	protected Component getMotd(PaperServerListPingEvent event, boolean whitelist) {
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
			return combine(Component.newline(),
				messenger.getMessage("motd.whitelist.header"),
				messenger.getMessage("motd.whitelist.splash")
			);
		}

		// Special message for outdated clients
		if (event.getClient().getProtocolVersion() < event.getProtocolVersion()) {
			return combine(Component.newline(),
				messenger.getMessage("motd.header", component("clock_time", getTime(pinger, messenger))),
				messenger.getMessage("motd.outdated_client")
			);
		}

		// Special welcome for newbies
		if (pinger.isNewbie()) {
			return combine(Component.newline(),
				messenger.getMessage("motd.welcome.header"),
				messenger.getMessage("motd.welcome.splash")
			);
		}

		// Random motd
		return combine(Component.newline(),
			messenger.getMessage("motd.header", component("clock_time", getTime(pinger, messenger))),
			messenger.getMessage("motd.splash")
		);
	}

	// Player names in motd do not support modern colors, using legacy hack
	@SuppressWarnings("deprecation")
	private String getColoredSpace(String line) {
		return SPACER + ChatColor.RESET + ChatColor.getLastColors(line);
	}

	private Component getTime(Pinger pinger, Messenger messenger) {
		if (!pinger.hasClock()) return Component.empty();
		int h = (int) (Bukkit.getWorlds().getFirst().getTime() / TICKS_PER_HOUR) + 6;
		if (h >= HOURS_PER_DAY)
			h -= HOURS_PER_DAY;
		int m = (int) ((MINUTES_PER_HOUR * (Bukkit.getWorlds().getFirst().getTime() % TICKS_PER_HOUR)) / TICKS_PER_HOUR);
		return messenger.getMessage("motd.clock_time", raw("hours", (h < 10 ? "0" : "") + h), raw("minutes", (m < 10 ? "0" : "") + m));
	}

}
