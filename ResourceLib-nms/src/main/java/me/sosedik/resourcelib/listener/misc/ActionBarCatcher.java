package me.sosedik.resourcelib.listener.misc;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerActionBar;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import me.sosedik.resourcelib.feature.HudMessenger;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * Catches vanilla action bar messages
 * to display them via custom action bar system
 */
public class ActionBarCatcher implements PacketListener, Listener {

	private static final String HUD_MARKER = ChatUtil.getPlainText(SpacingUtil.getSpacing(-2));

	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (event.getPacketType() == PacketType.Play.Server.SYSTEM_CHAT_MESSAGE) {
			var packet = new WrapperPlayServerSystemChatMessage(event);
			if (!packet.isOverlay()) return;

			Player player = event.getPlayer();
			HudMessenger hudMessenger = HudMessenger.of(player);
			hudMessenger.displayMessage(packet.getMessage());
			packet.setMessage(hudMessenger.getHudMessage());
		} else if (event.getPacketType() == PacketType.Play.Server.ACTION_BAR) {
			var packet = new WrapperPlayServerActionBar(event);
			Component text = packet.getActionBarText();
			if (ChatUtil.getPlainText(text).contains(HUD_MARKER)) return;

			Player player = event.getPlayer();
			HudMessenger hudMessenger = HudMessenger.of(player);
			hudMessenger.displayMessage(text);
			packet.setActionBarText(hudMessenger.getHudMessage());
		}
	}

}
