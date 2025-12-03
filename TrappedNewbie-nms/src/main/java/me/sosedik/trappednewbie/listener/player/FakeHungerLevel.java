package me.sosedik.trappednewbie.listener.player;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateHealth;
import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

/**
 * Hides saturation from the player, fakes hunger
 */
@NullMarked
public class FakeHungerLevel implements PacketListener {

	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.UPDATE_HEALTH) return;

		var packet = new WrapperPlayServerUpdateHealth(event);
		packet.setFoodSaturation(0F);

		Player player = event.getPlayer();
		if (packet.getFood() > 1 && !ThirstyPlayer.of(player).canRun())
			packet.setFood(1);
	}

}
