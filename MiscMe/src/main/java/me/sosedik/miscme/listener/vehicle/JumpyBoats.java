package me.sosedik.miscme.listener.vehicle;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerInput;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSteerVehicle;
import me.sosedik.miscme.MiscMe;
import org.bukkit.Sound;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Boats are jumpy! :D
 */
// MCCheck: 1.21.4, player input packet
public class JumpyBoats implements PacketListener {

	private static final Set<UUID> JUMP_DELAYS = new HashSet<>();

	@Override
	public void onPacketReceive(@NotNull PacketReceiveEvent event) {
		if (event.getPacketType() != PacketType.Play.Client.PLAYER_INPUT) return;

		var packet = new WrapperPlayClientPlayerInput(event);
		if (!packet.isJump()) return;

		Player player = event.getPlayer();
		if (JUMP_DELAYS.contains(player.getUniqueId())) return;

		MiscMe.scheduler().sync(() -> {
			if (!(player.getVehicle() instanceof Boat boat)) return;
			if (boat.isUnderWater()) return;

			if (boat.isInWater()) {
				triggerDelay(player);
				boat.setVelocity(boat.getVelocity().add(new Vector(0, 0.3, 0)));
				boat.emitSound(Sound.ENTITY_PLAYER_SPLASH, 0.4F, 1F);
			} else if (boat.isOnGround()) {
				triggerDelay(player);
				boat.setVelocity(boat.getVelocity().add(new Vector(0, 0.25, 0)));
			}
		});
	}

	private void triggerDelay(@NotNull Player player) {
		JUMP_DELAYS.add(player.getUniqueId());
		MiscMe.scheduler().async(() -> JUMP_DELAYS.remove(player.getUniqueId()), 5L);
	}

}
