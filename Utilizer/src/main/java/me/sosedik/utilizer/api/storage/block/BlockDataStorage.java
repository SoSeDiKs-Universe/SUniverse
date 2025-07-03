package me.sosedik.utilizer.api.storage.block;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface BlockDataStorage {

	String ID_TAG = "id";

	/**
	 * Called when the block is loaded
	 */
	default void onLoad() {}

	/**
	 * Called when the block is unloaded
	 */
	default void onUnload() {}

	/**
	 * Called when the block is placed by a player
	 *
	 * @param event event
	 */
	default void onPlace(BlockPlaceEvent event) {}

	/**
	 * Called when player interacts with a block
	 *
	 * @param event event
	 */
	default void onInteract(PlayerInteractEvent event) {}

	/**
	 * Called when the block is moved
	 *
	 * @param from from
	 * @param to to
	 */
	default void onMove(Location from, Location to) {}

	/**
	 * Called when the block is broken
	 *
	 * @param event event
	 */
	default void onBreak(BlockBreakEvent event) {}

	/**
	 * Called when the block is burnt
	 *
	 * @param event event
	 */
	default void onBurn(BlockBurnEvent event) {}

	/**
	 * Called when the block is exploded
	 *
	 * @param event explosion event
	 * @return whether to remove the block from exploded list
	 */
	default boolean onExplode(Event event) {
		return false;
	}

	/**
	 * Called when the block is destroyed
	 *
	 * @param event event
	 */
	default void onDestroy(BlockDestroyEvent event) {}

	/**
	 * Cleans up leftover data
	 */
	default void cleanUp() {}

	/**
	 * Gets the block
	 *
	 * @return block
	 */
	Block getBlock();

	/**
	 * Checks whether this block is in a loaded chunk
	 *
	 * @return whether this block is loaded
	 */
	default boolean isLoaded() {
		// Block is stored in a variable, so it's safe to get it
		return getBlock().getLocation().isChunkLoaded();
	}

	/**
	 * Gets the id of this block
	 *
	 * @return the id of this block
	 */
	NamespacedKey getId();

	/**
	 * Saves block data
	 *
	 * @return block data
	 */
	default ReadWriteNBT save() {
		ReadWriteNBT nbt = NBT.createNBTObject();
		nbt.setString(ID_TAG, getId().asString());
		return nbt;
	}

}
