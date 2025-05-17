package me.sosedik.utilizer.impl.storage;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.api.storage.block.BlockDataStorageHolder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SimpleRotatableBlockStorage extends BlockDataStorageHolder {

	private static final String FACING_KEY = "facing";

	private final BlockFace facing;
	private final ItemDisplay display;

	public SimpleRotatableBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
		this.facing = nbt.getOrDefault(FACING_KEY, BlockFace.UP);
		this.display = createDisplay();
	}

	public SimpleRotatableBlockStorage(BlockPlaceEvent event, ReadWriteNBT nbt) {
		super(event.getBlockPlaced(), nbt);
		BlockFace facing = event.getBlockAgainst().getFace(event.getBlockPlaced());
		this.facing = facing == null ? BlockFace.UP : facing;
		this.display = createDisplay();
	}

	private ItemDisplay createDisplay() {
		return this.block.getWorld().spawn(this.block.getLocation().center(), ItemDisplay.class, display -> {
			display.setItemStack(new ItemStack(requireMatchingMaterial()));
			display.setPersistent(false);
			Transformation transformation = display.getTransformation();
			transformation.getLeftRotation().set(switch (facing) {
				case SOUTH -> new Quaternionf().rotationX((float) Math.toRadians(90));
				case NORTH -> new Quaternionf().rotationX((float) Math.toRadians(-90));
				case EAST -> new Quaternionf().rotationZ((float) Math.toRadians(-90));
				case WEST -> new Quaternionf().rotationZ((float) Math.toRadians(90));
				case DOWN -> new Quaternionf().rotationX((float) Math.toRadians(180));
				default -> new Quaternionf();
			});
			display.setTransformation(transformation);
		});
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		this.display.remove();
	}

	@Override
	public ReadWriteNBT save() {
		ReadWriteNBT nbt = super.save();
		nbt.setEnum(FACING_KEY, this.facing);
		return nbt;
	}

}
