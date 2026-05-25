package me.sosedik.fancymotd.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.UUID;

/**
 * Fallback motd messages
 */
@NullMarked
public class PaperMotdRandomizer extends AbstractMotdRandomizer {

	private static final MiniMessage MINI = MiniMessage.miniMessage();

	private static final String HEADER_TEMPLATE = "<#37BBF5>SoSeDiK's <#ECF3FC>Universe <red>♥";
	private static final String MAINTENANCE_MESSAGE = "<red>Have a good day!";
	private static final String MAINTENANCE_MESSAGE_VERSION_LEGACY = "On maintenance!";
	private static final String OUTDATED_CLIENT_MESSAGE = "<red>Your client version is outdated! :(";
	private static final String DEFAULT_MESSAGE = "<gray>Testing random stuff";

	private static final Component HEADER_COMPONENT = MINI.deserialize(HEADER_TEMPLATE);
	private static final Component MAINTENANCE_MOTD = Component.textOfChildren(HEADER_COMPONENT, Component.newline(), MINI.deserialize(MAINTENANCE_MESSAGE));
	private static final Component OUTDATED_MOTD = Component.textOfChildren(HEADER_COMPONENT, Component.newline(), MINI.deserialize(OUTDATED_CLIENT_MESSAGE));
	private static final Component DEFAULT_MOTD = Component.textOfChildren(HEADER_COMPONENT, Component.newline(), MINI.deserialize(DEFAULT_MESSAGE));

	private static final PaperServerListPingEvent.ListedPlayerInfo MAINTENANCE_INFO = new PaperServerListPingEvent.ListedPlayerInfo(LegacyComponentSerializer.legacySection().serialize(Component.text("Server is under maintenance", NamedTextColor.RED)), UUID.randomUUID());

	@Override
	protected Component getMotd(PaperServerListPingEvent event, boolean whitelist) {
		// Special message if whitelisted
		if (whitelist) {
			event.setVersion(MAINTENANCE_MESSAGE_VERSION_LEGACY);
			List<PaperServerListPingEvent.ListedPlayerInfo> listedPlayers = event.getListedPlayers();
			listedPlayers.clear();
			listedPlayers.add(MAINTENANCE_INFO);
			return MAINTENANCE_MOTD;
		}

		// Special message for outdated clients
		if (event.getClient().getProtocolVersion() < event.getProtocolVersion())
			return OUTDATED_MOTD;

		// Default MOTD
		return DEFAULT_MOTD;
	}

}
