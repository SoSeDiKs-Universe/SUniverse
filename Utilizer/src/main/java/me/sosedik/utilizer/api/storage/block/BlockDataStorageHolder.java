package me.sosedik.utilizer.api.storage.block;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
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
	public void onMove(Location from, Location to) {
		this.block = to.getBlock();
	}

	@Override
	public Block getBlock() {
		return this.block;
	}

	@Override
	public NamespacedKey getId() {
		return this.storageId;
	}

	public Material requireMatchingMaterial() {
		return Objects.requireNonNull(getMatchingMaterial());
	}

	public @Nullable Material getMatchingMaterial() {
		return this.material;
	}

}
