package me.sosedik.uglychatter.listener.misc;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound;
import com.github.retrooper.packetevents.protocol.nbt.NBTList;
import com.github.retrooper.packetevents.protocol.nbt.NBTString;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityType;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityTypes;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.chunk.TileEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockEntityData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import io.papermc.paper.event.player.PlayerOpenSignEvent;
import me.sosedik.uglychatter.api.chat.FancyMessageRenderer;
import me.sosedik.uglychatter.api.chat.FancyRendererTag;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Adds fancies support for signs
 */
// MCCheck: 1.21.4, sign block entity data // TODO fix with 1.21.5 changes
@NullMarked
public class SignBeautifier implements PacketListener, Listener {

	private static final Set<UUID> RENDER_BLACKLIST = new HashSet<>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		RENDER_BLACKLIST.remove(player.getUniqueId());

		// Note: event.lines() is a live container
		List<Component> lines = event.lines();
		for (var i = 0; i < lines.size(); i++) {
			Component line = lines.get(i);
			if (line == null) continue;
			// Reset input to make it easier to parse later
			String rawLine = FancyMessageRenderer.getRawInput(line, FancyRendererTag.SKIP_MARKDOWN);
			line = Component.text(rawLine);
			event.line(i, line);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		RENDER_BLACKLIST.remove(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignClick(PlayerOpenSignEvent event) {
		Player player = event.getPlayer();
		RENDER_BLACKLIST.add(player.getUniqueId());

		// Resend raw (not rendered) sign data
		Sign sign = event.getSign();
		player.sendBlockUpdate(sign.getLocation(), sign);
	}

	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (event.getPacketType() == PacketType.Play.Server.BLOCK_ENTITY_DATA) {
			// SoSeDiK lost connection: Internal Exception: io.netty.handler.codec.EncoderException: java.lang.IndexOutOfBoundsException: readerIndex: 0, writerIndex: 292 (expected: 0 <= readerIndex <= writerIndex <= capacity(256))
//			handleBlockEntityData(event.getPlayer(), new WrapperPlayServerBlockEntityData(event));
		} else if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
//			handleChunkData(event.getPlayer(), new WrapperPlayServerChunkData(event));
		}
	}

	private void handleBlockEntityData(Player player, WrapperPlayServerBlockEntityData wrapper) {
		if (!isSign(wrapper.getBlockEntityType())) return;
		if (RENDER_BLACKLIST.contains(player.getUniqueId())) return;

		MiniMessage miniMessage = Messenger.messenger(player).miniMessage();
		NBTCompound nbt = wrapper.getNBT();
		renderText(miniMessage, player, nbt, Side.FRONT);
		renderText(miniMessage, player, nbt, Side.BACK);
	}

	private void handleChunkData(Player player, WrapperPlayServerChunkData wrapper) {
		ClientVersion clientVersion = PacketEvents.getAPI().getPlayerManager().getClientVersion(player);
		MiniMessage miniMessage = null;
		Column column = wrapper.getColumn();
		for (TileEntity tileEntity : column.getTileEntities()) {
			BlockEntityType blockEntityType = BlockEntityTypes.getById(clientVersion, tileEntity.getType());
			if (!isSign(blockEntityType)) continue;

			if (miniMessage == null) miniMessage = Messenger.messenger(player).miniMessage();

			NBTCompound nbt = tileEntity.getNBT();
			renderText(miniMessage, player, nbt, Side.FRONT);
			renderText(miniMessage, player, nbt, Side.BACK);
		}
	}

	private boolean isSign(BlockEntityType blockEntityType) {
		return blockEntityType == BlockEntityTypes.SIGN || blockEntityType == BlockEntityTypes.HANGING_SIGN;
	}

	private void renderText(MiniMessage miniMessage, Player player, NBTCompound nbt, Side side) {
		String sideKey = side == Side.FRONT ? "front_text" : "back_text";
		NBTCompound sideText = nbt.getCompoundTagOrNull(sideKey);
		if (sideText == null) return;

		NBTList<NBTString> messages = sideText.getStringListTagOrNull("messages");
		if (messages == null) return;

		for (int i = 0; i < messages.size(); i++) {
			String serialized = messages.getTag(i).getValue();
			if (serialized.isEmpty()) continue;

			Component line;
			// Raw text is wrapped in "
			if (serialized.charAt(0) == '"' && serialized.charAt(serialized.length() - 1) == '"')
				line = Component.text(serialized.substring(1, serialized.length() - 1));
			else
				line = GsonComponentSerializer.gson().deserialize(serialized);
			serialized = FancyMessageRenderer.getRawInput(line);

			Component rendered = FancyMessageRenderer.renderMessage(miniMessage, serialized, player, player);
			serialized = GsonComponentSerializer.gson().serialize(rendered);
			messages.setTag(i, new NBTString(serialized));
		}
	}

}
