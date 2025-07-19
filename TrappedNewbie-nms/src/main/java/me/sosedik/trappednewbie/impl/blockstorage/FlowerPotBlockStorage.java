package me.sosedik.trappednewbie.impl.blockstorage;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.api.storage.block.InventoryBlockDataStorageHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public class FlowerPotBlockStorage extends InventoryBlockDataStorageHolder {

	private static final Component DEFAULT_TITLE = Component.translatable(Material.FLOWER_POT.translationKey());

	public FlowerPotBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
		this.inventory = Bukkit.createInventory(this, InventoryType.HOPPER, DEFAULT_TITLE);
		if (nbt.hasTag(INVENTORY_STORAGE_TAG)) this.inventory.setStorageContents(Objects.requireNonNull(nbt.getItemStackArray(INVENTORY_STORAGE_TAG)));
	}

	@Override
	public void onInteract(PlayerInteractEvent event) {
		if (getBlock().getType() != Material.FLOWER_POT) {
			Player player = event.getPlayer();
			ItemStack item = player.getInventory().getItemInMainHand();
			if (Tag.ITEMS_SMALL_FLOWERS.isTagged(item.getType())) return;
		}

		super.onInteract(event);
	}

	@Override
	public void onDrop(BlockDropItemEvent event) {
		for (ItemStack item : getInventory().getStorageContents()) {
			if (!ItemStack.isEmpty(item))
				event.addDrop(item);
		}
	}

	@Override
	public ReadWriteNBT save() {
		ReadWriteNBT nbt = super.save();
		nbt.setItemStackArray(INVENTORY_STORAGE_TAG, getInventory().getStorageContents());
		return nbt;
	}

}
