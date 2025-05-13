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
	void onLoad();

	/**
	 * Called when the block is unloaded
	 */
	void onUnload();

	/**
	 * Called when the block is placed by a player
	 *
	 * @param event event
	 */
	void onPlace(BlockPlaceEvent event);

	/**
	 * Called when player interacts with a block
	 *
	 * @param event event
	 */
	void onInteract(PlayerInteractEvent event);

	/**
	 * Called when the block is moved
	 *
	 * @param from from
	 * @param to to
	 */
	void onMove(Location from, Location to);

	/**
	 * Called when the block is broken
	 *
	 * @param event event
	 */
	void onBreak(BlockBreakEvent event);

	/**
	 * Called when the block is burnt
	 *
	 * @param event event
	 */
	void onBurn(BlockBurnEvent event);

	/**
	 * Called when the block is exploded
	 *
	 * @param event explosion event
	 * @return whether to remove the block from exploded list
	 */
	boolean onExplode(Event event);

	/**
	 * Called when the block is destroyed
	 *
	 * @param event event
	 */
	void onDestroy(BlockDestroyEvent event);

	/**
	 * Cleans up leftover data
	 */
	default void cleanUp() {
		// Override if needed
	}

	/**
	 * Gets the block
	 *
	 * @return block
	 */
	Block getBlock();

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
