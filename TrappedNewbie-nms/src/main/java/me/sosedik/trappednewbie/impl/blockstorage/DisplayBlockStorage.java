package me.sosedik.trappednewbie.impl.blockstorage;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.api.storage.block.BlockDataStorageHolder;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class DisplayBlockStorage extends BlockDataStorageHolder {

	protected final ItemDisplay display;

	public DisplayBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
		this.display = createDisplay();
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		this.display.remove();
	}

	@Override
	public void onMove(Location from, Location to) {
		super.onMove(from, to);
		this.display.teleport(to.center());
	}

	private ItemDisplay createDisplay() {
		Location loc = getBlock().getLocation().center();
		return loc.getWorld().spawn(loc, ItemDisplay.class, display -> {
			display.setPersistent(false);
			display.setItemStack(ItemStack.of(requireMatchingMaterial()));
		});
	}

}
