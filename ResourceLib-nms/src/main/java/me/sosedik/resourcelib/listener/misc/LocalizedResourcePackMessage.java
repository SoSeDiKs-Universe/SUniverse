package me.sosedik.resourcelib.listener.misc;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.configuration.server.WrapperConfigServerResourcePackSend;
import me.sosedik.fancymotd.Pinger;
import me.sosedik.utilizer.api.message.Messenger;
import org.jspecify.annotations.NullMarked;

/**
 * Localizes resource pack prompt messages
 * based on {@link Pinger}'s cached locale
 */
@NullMarked
public class LocalizedResourcePackMessage implements PacketListener {

	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (event.getPacketType() != PacketType.Configuration.Server.RESOURCE_PACK_SEND) return;

		String ip = event.getSocketAddress().getAddress().getHostAddress();
		var packet = new WrapperConfigServerResourcePackSend(event);
		packet.setPrompt(Messenger.messenger(Pinger.getPinger(ip).getLanguage()).getMessage("resource_pack.prompt"));
	}

}
