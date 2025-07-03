package me.sosedik.resourcelib.listener.misc;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import me.sosedik.resourcelib.feature.HudMessenger;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * Catches vanilla action bar messages
 * to display them via custom action bar system
 */
public class ActionBarCatcher implements PacketListener, Listener {

	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.SYSTEM_CHAT_MESSAGE) return;

		var packet = new WrapperPlayServerSystemChatMessage(event);
		if (!packet.isOverlay()) return;

		Player player = event.getPlayer();
		HudMessenger hudMessenger = HudMessenger.of(player);
		hudMessenger.displayMessage(packet.getMessage());
		packet.setMessage(hudMessenger.getHudMessage());
	}

}
