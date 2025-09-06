package me.sosedik.delightfulfarming.listener.sugar;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth;
import org.jspecify.annotations.NullMarked;

/**
 * Hides saturation from the player
 */
@NullMarked
public class HiddenSaturation implements PacketListener {

	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.UPDATE_HEALTH) return;

		var packet = new WrapperPlayServerUpdateHealth(event);
		packet.setFoodSaturation(0F);
	}

}
