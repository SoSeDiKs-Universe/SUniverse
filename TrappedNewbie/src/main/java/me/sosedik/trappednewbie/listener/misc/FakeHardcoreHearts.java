package me.sosedik.trappednewbie.listener.misc;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerJoinGame;
import org.jetbrains.annotations.NotNull;

/**
 * Makes client always display hardcore hearts
 */
// MCCheck: 1.21.1, login packet
public class FakeHardcoreHearts implements PacketListener {

	@Override
	public void onPacketSend(@NotNull PacketSendEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.JOIN_GAME) return;

		var packet = new WrapperPlayServerJoinGame(event);
		packet.setHardcore(true);
	}

}
