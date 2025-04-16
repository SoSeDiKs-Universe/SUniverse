package me.sosedik.resourcelib.listener.misc;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerActionBar;
import me.sosedik.resourcelib.feature.HudMessenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;

/**
 * Catches vanilla action bar messages
 * to display them via custom action bar system
 */
public class ActionBarCatcher implements PacketListener, Listener {

	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (event.getPacketType() != PacketType.Play.Server.ACTION_BAR) return;

		var packet = new WrapperPlayServerActionBar(event);
		if (!(packet.getActionBarText() instanceof TranslatableComponent text)) return;

		event.setCancelled(true);
		Player player = event.getPlayer();
		HudMessenger.of(player).displayMessage(text);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMount(EntityMountEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		HudMessenger.of(player).displayMessage(Component.translatable("mount.onboard", Component.keybind("key.sneak")));
	}

}
