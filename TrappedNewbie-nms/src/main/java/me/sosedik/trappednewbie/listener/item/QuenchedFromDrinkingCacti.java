package me.sosedik.trappednewbie.listener.item;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.chunk.BaseChunk;
import com.github.retrooper.packetevents.protocol.world.chunk.Column;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.enums.Type;
import com.github.retrooper.packetevents.protocol.world.states.type.StateType;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerMultiBlockChange;
import me.sosedik.trappednewbie.dataset.TrappedNewbieEffects;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import me.sosedik.utilizer.util.MathUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Granting quenched effect from drinking cacti
 */
@NullMarked
public class QuenchedFromDrinkingCacti implements PacketListener, Listener {

	private static final ClientVersion VERSION = PacketEvents.getAPI().getServerManager().getVersion().toClientVersion();
	private static final int SKIP_STATE = -1;

	private static final Map<UUID, Map<Integer, Integer>> QUENCHED_MAP = new HashMap<>();
	private static final List<WrappedBlockState> SOLIDS = new ArrayList<>();
	private static final List<WrappedBlockState> REPLACEABLES = new ArrayList<>();

	static {
		StateTypes.values().forEach(state -> {
			WrappedBlockState defaultState = WrappedBlockState.getDefaultState(VERSION, state);
			if (isProhibited(defaultState)) return;

			if (state.isReplaceable()) {
				REPLACEABLES.add(defaultState);
			} else if (state.isSolid() && !state.exceedsCube()) {
				SOLIDS.add(defaultState);
			}
		});
	}

	private static boolean isProhibited(WrappedBlockState state) {
		StateType type = state.getType();
		if (type == StateTypes.NOTE_BLOCK) return true;
		if (type == StateTypes.TRIPWIRE) return true;
		if (type == StateTypes.WATER) return true;
		if (type == StateTypes.LAVA) return true;
		if (type.getName().endsWith("candle")) return true;
		if (type.getName().endsWith("cake")) return true;
		return state.getTypeData() == Type.BOTTOM;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConsume(PlayerItemConsumeEvent event) {
		if (ThirstData.DrinkType.fromItem(event.getItem()) != ThirstData.DrinkType.CACTUS_JUICE) return;

		event.getPlayer().addPotionEffect(new PotionEffect(TrappedNewbieEffects.QUENCHED, 300 * 20, 0));
	}

	@EventHandler
	public void onEffectEnd(EntityPotionEffectEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;

		PotionEffect oldEffect = event.getOldEffect();
		PotionEffect newEffect = event.getNewEffect();
		if (newEffect == null) {
			if (oldEffect == null) return;
			if (oldEffect.getType() != TrappedNewbieEffects.QUENCHED) return;
			if (QUENCHED_MAP.remove(player.getUniqueId()) == null) return;
		} else {
			if (oldEffect != null) return;
			if (newEffect.getType() != TrappedNewbieEffects.QUENCHED) return;
		}

		player.getSentChunks().forEach(chunk -> chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ()));
	}

	@EventHandler
	public void onExit(PlayerQuitEvent event) {
		QUENCHED_MAP.remove(event.getPlayer().getUniqueId());
	}

	@Override
	public void onPacketSend(PacketSendEvent event) {
		PacketTypeCommon packetType = event.getPacketType();
		switch (packetType) {
			case PacketType.Play.Server.CHUNK_DATA -> onChunkData(event);
			case PacketType.Play.Server.BLOCK_CHANGE -> onBlockChange(event);
			case PacketType.Play.Server.MULTI_BLOCK_CHANGE -> onMultiBlockChange(event);
			default -> {}
		}
	}

	private void onChunkData(PacketSendEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPotionEffect(TrappedNewbieEffects.QUENCHED)) return;

		var packet = new WrapperPlayServerChunkData(event);
		Column column = packet.getColumn();
		for (BaseChunk chunk : column.getChunks()) {
			if (chunk.isEmpty()) continue;
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {
					for (int z = 0; z < 16; z++) {
						int newState = getNewState(player, chunk.getBlockId(x, y, z));
						if (newState != SKIP_STATE)
							chunk.set(VERSION, x, y, z, newState);
					}
				}
			}
		}
	}

	private void onBlockChange(PacketSendEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPotionEffect(TrappedNewbieEffects.QUENCHED)) return;

		var packet = new WrapperPlayServerBlockChange(event);
		int newState = getNewState(player, packet.getBlockId());
		if (newState != SKIP_STATE)
			packet.setBlockID(newState);
	}

	private void onMultiBlockChange(PacketSendEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPotionEffect(TrappedNewbieEffects.QUENCHED)) return;

		var packet = new WrapperPlayServerMultiBlockChange(event);
		for (WrapperPlayServerMultiBlockChange.EncodedBlock encodedBlock : packet.getBlocks()) {
			int newState = getNewState(player, encodedBlock.getBlockId());
			if (newState != SKIP_STATE)
				encodedBlock.setBlockId(newState);
		}
	}

	private int getNewState(Player player, int blockId) {
		return QUENCHED_MAP.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).computeIfAbsent(blockId, k -> {
			WrappedBlockState state = getNewState(WrappedBlockState.getByGlobalId(VERSION, blockId));
			if (state == null)
				return -1;
			return state.getGlobalId();
		});
	}

	private @Nullable WrappedBlockState getNewState(WrappedBlockState state) {
		if (shouldBeSkipped(state.getType())) return null;
		if (state.getType() == StateTypes.WATER || state.getType() == StateTypes.LAVA) {
			WrappedBlockState blockState = (state.getType() == StateTypes.WATER ? StateTypes.LAVA : StateTypes.WATER).createBlockState();
			blockState.setLevel(state.getLevel());
			return blockState;
		}
		if (state.getType().isReplaceable()) return MathUtil.getRandom(REPLACEABLES);
		return MathUtil.getRandom(SOLIDS);
	}

	private static boolean shouldBeSkipped(StateType type) {
		return type.isAir() || type.getHardness() == -1F;
	}

}
