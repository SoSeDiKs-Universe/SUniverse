package me.sosedik.utilizer.listener;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTChunk;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.Position;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.math.WorldChunkPosition;
import me.sosedik.utilizer.api.storage.block.BlockDataStorage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Persistent block storage for regular blocks
 */
@NullMarked
public class BlockStorage {

	private static final Map<WorldChunkPosition, Map<BlockPosition, BlockDataStorage>> BLOCKS = new HashMap<>();
	private static final Map<NamespacedKey, Class<? extends BlockDataStorage>> MAPPINGS = new HashMap<>();

	public static synchronized void moveData(Collection<Location> fromLocs, BlockFace direction) {
		Map<Location, Map.Entry<Location, BlockDataStorage>> storedData = new HashMap<>();
		for (Location from : fromLocs) {
			BlockDataStorage fromData = removeInfo(from);
			if (fromData != null)
				storedData.put(from.clone().shiftTowards(direction), Map.entry(from, fromData));
		}
		storedData.forEach((to, fromData) -> BLOCKS.computeIfAbsent(WorldChunkPosition.of(to), k -> new HashMap<>())
				.computeIfAbsent(Position.block(to), k -> fromData.getValue()).onMove(fromData.getKey(), to));
	}

	public static synchronized void moveData(Location from, Location to) {
		BlockDataStorage fromData = removeInfo(from);
		if (fromData == null) return;

		BLOCKS.computeIfAbsent(WorldChunkPosition.of(to), k -> new HashMap<>())
				.computeIfAbsent(Position.block(to), k -> fromData).onMove(from, to);
	}

	public static synchronized @Nullable BlockDataStorage getByLoc(Block block) {
		return getByLoc(block.getLocation());
	}

	public static synchronized @Nullable BlockDataStorage getByLoc(Location loc) {
		Map<BlockPosition, BlockDataStorage> BlockPositions = BLOCKS.get(WorldChunkPosition.of(loc));
		return BlockPositions == null ? null : BlockPositions.get(Position.block(loc));
	}

	public static synchronized @Nullable BlockDataStorage initBlock(Block block) {
		return createInfo(block.getLocation(), block.getType().getKey());
	}

	public static synchronized @Nullable BlockDataStorage createInfo(Location loc, NamespacedKey key) {
		Class<? extends BlockDataStorage> storageClass = MAPPINGS.get(key);
		if (storageClass == null) return null;
		if (getByLoc(loc) != null) return null;

		Bukkit.broadcast(Component.text("Yay: %s %s %s - %s".formatted(loc.blockX(), loc.blockY(), loc.blockZ(), key)).clickEvent(ClickEvent.suggestCommand("/tp @s %s %s %s".formatted(loc.blockX(), loc.blockY() + 1, loc.blockZ()))));
		try {
			ReadWriteNBT nbt = NBT.createNBTObject();
			nbt.setString(BlockDataStorage.ID_TAG, key.asString());
			BlockDataStorage storage = storageClass
				.getConstructor(Block.class, ReadWriteNBT.class)
				.newInstance(loc.getBlock(), nbt);
			saveInfo(loc, storage);
			storage.onLoad();
			return storage;
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException |
		         NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	public static synchronized BlockDataStorage saveInfo(Block block, BlockDataStorage storage) {
		return saveInfo(block.getLocation(), storage);
	}

	public static synchronized BlockDataStorage saveInfo(Location loc, BlockDataStorage storage) {
		var blocksMap = BLOCKS.computeIfAbsent(WorldChunkPosition.of(loc), k -> new HashMap<>());
		BlockDataStorage oldStorage = blocksMap.put(Position.block(loc), storage);
		if (oldStorage != null) oldStorage.cleanUp();
		return storage;
	}

	/**
	 * Clear block from storage
	 *
	 * @param loc block location
	 */
	public static synchronized @Nullable BlockDataStorage removeInfo(Location loc) {
		WorldChunkPosition chunk = WorldChunkPosition.of(loc);
		BlockPosition location = Position.block(loc);
		Map<BlockPosition, BlockDataStorage> BlockPositions = BLOCKS.get(chunk);
		if (BlockPositions == null) return null;

		BlockDataStorage storage = BlockPositions.remove(location);
		if (storage == null) return null;

		storage.cleanUp();

		if (BlockPositions.isEmpty())
			BLOCKS.remove(chunk);

		deleteBlock(chunk, location);

		return storage;
	}

	private static synchronized void deleteBlock(Location location) {
		deleteBlock(WorldChunkPosition.of(location), Position.block(location));
	}

	private static synchronized void deleteBlock(WorldChunkPosition chunk, BlockPosition location) {
		ReadWriteNBT chunkNbt = new NBTChunk(chunk.getChunk()).getPersistentDataContainer();
		ReadWriteNBT blocksNbt = chunkNbt.getCompound("blocks");
		if (blocksNbt == null) return;

		String blockTag = "%s_%s_%s".formatted(location.blockX(), location.blockY(), location.blockZ());
		if (!blocksNbt.hasTag(blockTag)) return;

		blocksNbt.removeKey(blockTag);
		if (blocksNbt.getKeys().isEmpty())
			chunkNbt.removeKey("blocks");
	}

	public static synchronized void saveAllData() {
		Iterator<WorldChunkPosition> iterator = BLOCKS.keySet().iterator();
		while (iterator.hasNext()) {
			WorldChunkPosition loc = iterator.next();
			saveChunk(loc, true);
			iterator.remove();
		}
	}

	public static synchronized void loadInfo(Chunk loadedChunk, boolean scan) {
		var storedChunk = WorldChunkPosition.of(loadedChunk);
		Map<BlockPosition, BlockDataStorage> blockPositions = BLOCKS.get(storedChunk);
		if (blockPositions != null) return;

		if (scan) {
			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {
					for (int y = loadedChunk.getWorld().getMinHeight(); y < loadedChunk.getWorld().getMaxHeight(); y++) {
						Block block = loadedChunk.getBlock(x, y, z);
						initBlock(block);
					}
				}
			}
			return;
		}

		ReadWriteNBT chunkNbt = new NBTChunk(loadedChunk).getPersistentDataContainer();
		ReadWriteNBT blocksNbt = chunkNbt.getCompound("blocks");
		if (blocksNbt == null) return;

		for (String blockKey : blocksNbt.getKeys()) {
			ReadWriteNBT blockNbt = blocksNbt.getCompound(blockKey);
			if (blockNbt == null) continue;

			String[] coords = blockKey.split("_");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			BlockPosition location = Position.block(x, y, z);

			String blockId = blockNbt.hasTag(BlockDataStorage.ID_TAG) ? blockNbt.getString(BlockDataStorage.ID_TAG) : null;
			if (blockId == null) {
				Utilizer.logger().warn("Unknown block at {} {} {} {}", loadedChunk.getWorld().getName(), location.blockX(), location.blockY(), location.blockZ());
				continue;
			}
			Class<? extends BlockDataStorage> storageClass = MAPPINGS.get(NamespacedKey.fromString(blockId));
			if (storageClass == null) {
				Utilizer.logger().warn("Missing block mapping for block at {} {} {} {} with id {}", loadedChunk.getWorld().getName(), location.blockX(), location.blockY(), location.blockZ(), blockId);
				continue;
			}
			BlockDataStorage storage;
			try {
				ReadWriteNBT nbt = NBT.createNBTObject();
				nbt.mergeCompound(blockNbt); // blockNbt will be yeeted, pass copy instead
				storage = storageClass
					.getConstructor(Block.class, ReadWriteNBT.class)
					.newInstance(location.toLocation(loadedChunk.getWorld()).getBlock(), nbt);
			} catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
				Utilizer.logger().warn("Couldn't load custom block data!", e);
				continue;
			}
			var map = BLOCKS.computeIfAbsent(storedChunk, k -> new HashMap<>());
			if (map.putIfAbsent(location, storage) == null)
				storage.onLoad();
		}

		chunkNbt.removeKey("blocks");
	}

	public static synchronized void saveChunk(WorldChunkPosition chunk, boolean remove) {
		Map<BlockPosition, BlockDataStorage> blockPositions = remove ? BLOCKS.remove(chunk) : BLOCKS.get(chunk);
		if (blockPositions == null) return;

		blockPositions.forEach((loc, storage) -> {
			if (remove)
				storage.onUnload();

			new NBTChunk(chunk.getChunk()).getPersistentDataContainer()
				.getOrCreateCompound("blocks")
				.getOrCreateCompound("%s_%s_%s".formatted(loc.blockX(), loc.blockY(), loc.blockZ()))
				.mergeCompound(storage.save());
		});
	}

	public static void addMapping(NamespacedKey key, Class<? extends BlockDataStorage> dataClass) {
		MAPPINGS.put(key, dataClass);
	}

}
