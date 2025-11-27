package me.sosedik.trappednewbie.impl.blockstorage;

import com.destroystokyo.paper.MaterialTags;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import me.sosedik.miscme.listener.item.ImmersiveDyes;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.dataset.TrappedNewbieItems;
import me.sosedik.trappednewbie.impl.item.modifier.ItemModelModifier;
import me.sosedik.utilizer.api.storage.block.BlockDataStorageHolder;
import me.sosedik.utilizer.api.storage.block.ExtraDroppableBlockStorage;
import me.sosedik.utilizer.util.MiscUtil;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.function.Consumer;

@NullMarked
public class SleepingBagBlockStorage extends BlockDataStorageHolder implements ExtraDroppableBlockStorage {

	private static final String ITEM_KEY = "item";
	private static final String FACING_KEY = "facing";

	private final ItemDisplay display;
	private final ItemStack storedItem;
	private final BlockFace facing;

	public SleepingBagBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
		this.facing = nbt.getOrDefault(FACING_KEY, BlockFace.NORTH);
		ItemStack storedItem = nbt.hasTag(ITEM_KEY) ? nbt.getItemStack(ITEM_KEY) : null;
		if (storedItem == null)
			storedItem = ItemStack.of(TrappedNewbieItems.SLEEPING_BAG).withColor(DyeColor.WHITE);
		this.storedItem = storedItem;
		this.display = createDisplay();
		updateDisplayItem();
	}

	public SleepingBagBlockStorage(BlockPlaceEvent event, ReadWriteNBT nbt) {
		super(event.getBlockPlaced(), nbt);
		this.storedItem = event.getItemInHand().asOne();
		this.facing = event.getPlayer().getFacing().getOppositeFace();
		this.display = createDisplay();
		updateDisplayItem();
	}

	public boolean tryToDye(Player player, EquipmentSlot hand) {
		ItemStack item = player.getInventory().getItem(hand);
		if (!MaterialTags.DYES.isTagged(item)) return false;

		DyedItemColor data = this.storedItem.getData(DataComponentTypes.DYED_COLOR);
		Color currentColor = data == null ? Color.WHITE : data.color();
		Color newColor = MiscUtil.getDyeColor(item.getType(), "DYE").getColor();
		if (currentColor.equals(newColor)) return false;

		newColor = currentColor.mixColors(newColor);
		this.storedItem.setData(DataComponentTypes.DYED_COLOR, DyedItemColor.dyedItemColor(newColor));
		updateDisplayItem();

		Location loc = getBlock().getLocation().center(0.1);
		Material effectType = Material.getMaterial(item.getType().name().replace("DYE", "BED"));
		if (effectType != null)
			ImmersiveDyes.playEffect(player, hand, loc, effectType.createBlockData());

		if (!player.getGameMode().isInvulnerable() && Math.random() < ImmersiveDyes.DYE_REDUCE_CHANCE)
			item.subtract();

		return true;
	}

	private ItemDisplay createDisplay() {
		Location loc = getBlock().getLocation().center();
		return loc.getWorld().spawn(loc, ItemDisplay.class, display -> {
			display.setPersistent(false);
			double rotation = switch (this.facing) {
				case WEST -> -90;
				case EAST -> 90;
				case NORTH -> 180;
				default -> 0;
			};
			Transformation transformation = display.getTransformation();
			transformation.getLeftRotation().set(new Quaternionf().rotationY((float) Math.toRadians(rotation)));
			display.setTransformation(transformation);
		});
	}

	private void updateDisplayItem() {
		ItemStack displayItem = this.storedItem.asOne();
		NBT.modify(displayItem, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setBoolean(ItemModelModifier.SKIP_TAG, true));
		displayItem.setData(DataComponentTypes.ITEM_MODEL, TrappedNewbie.trappedNewbieKey("sleeping_bag"));
		this.display.setItemStack(displayItem);
	}

	@Override
	public List<ItemStack> getExtraDrops(Event event) {
		return List.of(this.storedItem);
	}

	@Override
	public void cleanUp() {
		this.display.remove();
	}

	@Override
	public void onMove(Location from, Location to) {
		super.onMove(from, to);
		this.display.teleport(getBlock().getRelative(this.facing).getLocation().center());
	}

	@Override
	public ReadWriteNBT save() {
		ReadWriteNBT nbt = super.save();
		nbt.setItemStack(ITEM_KEY, this.storedItem);
		nbt.setEnum(FACING_KEY, this.facing);
		return nbt;
	}

}
