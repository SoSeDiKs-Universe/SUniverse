package me.sosedik.utilizer.api.storage.block;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

@NullMarked
public abstract class BlockDataStorageHolder implements BlockDataStorage {

	private final NamespacedKey storageId;
	private final @Nullable Material material;
	protected Block block;

	protected BlockDataStorageHolder(Block block, ReadWriteNBT nbt) {
		this(block, Objects.requireNonNull(NamespacedKey.fromString(nbt.getString(ID_TAG))));
	}

	protected BlockDataStorageHolder(Block block, NamespacedKey storageId) {
		this.storageId = storageId;
		this.material = Material.matchMaterial(storageId.asString());
		this.block = block;
	}

	@Override
	public void onLoad() {}

	@Override
	public void onUnload() {}

	@Override
	public void onMove(Location from, Location to) {
		this.block = to.getBlock();
	}

	@Override
	public void onBreak(BlockBreakEvent event) {
		cleanUp();
	}

	@Override
	public void onBurn(BlockBurnEvent event) {
		cleanUp();
	}

	@Override
	public boolean onExplode(Event event) {
		cleanUp();
		return false;
	}

	@Override
	public void onDestroy(BlockDestroyEvent event) {
		cleanUp();
	}

	@Override
	public Block getBlock() {
		return this.block;
	}

	@Override
	public NamespacedKey getId() {
		return this.storageId;
	}

	public @Nullable Material getMatchingMaterial() {
		return this.material;
	}

}
