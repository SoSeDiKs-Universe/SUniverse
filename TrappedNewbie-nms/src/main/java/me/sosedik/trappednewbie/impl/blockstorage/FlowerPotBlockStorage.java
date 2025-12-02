package me.sosedik.trappednewbie.impl.blockstorage;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.api.storage.block.ExtraDroppableBlockStorage;
import me.sosedik.utilizer.api.storage.block.InventoryBlockDataStorageHolder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NullMarked
public class FlowerPotBlockStorage extends InventoryBlockDataStorageHolder implements ExtraDroppableBlockStorage {

	private static final Component DEFAULT_TITLE = Component.translatable(Material.FLOWER_POT.translationKey());

	public FlowerPotBlockStorage(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
		this.inventory = Bukkit.createInventory(this, InventoryType.HOPPER, DEFAULT_TITLE);
		if (nbt.hasTag(INVENTORY_STORAGE_TAG)) this.inventory.setStorageContents(Objects.requireNonNull(nbt.getItemStackArray(INVENTORY_STORAGE_TAG)));
	}

	@Override
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) {
			event.setCancelled(true); // We don't want to place a block, if any
			return;
		}

		Player player = event.getPlayer();
		if (player.isSneaking() && !player.getInventory().getItemInMainHand().isEmpty()) return;

		Block block = getBlock();
		// Placing flower
		if (block.getType() == Material.FLOWER_POT && canConvertToPot(player.getInventory().getItemInMainHand().getType())) return;

		super.onInteract(event);
	}

	private boolean canConvertToPot(Material type) {
		return type != Material.AIR && Material.matchMaterial(type.getKey().namespace() + ":potted_" + type.getKey().value()) != null;
	}

	@Override
	public boolean dropOnExplosion() {
		return true;
	}

	@Override
	public List<ItemStack> getExtraDrops(Event event) {
		List<ItemStack> drops = new ArrayList<>();
		for (ItemStack item : getInventory().getStorageContents()) {
			if (!ItemStack.isEmpty(item))
				drops.add(item);
		}
		return drops;
	}

	@Override
	public Component getDefaultName(Player player) {
		return DEFAULT_TITLE;
	}

	@Override
	public ReadWriteNBT save() {
		ReadWriteNBT nbt = super.save();
		nbt.setItemStackArray(INVENTORY_STORAGE_TAG, getInventory().getStorageContents());
		return nbt;
	}

}
