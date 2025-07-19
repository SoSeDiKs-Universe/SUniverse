package me.sosedik.utilizer.api.storage.block;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.api.AInventory;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public abstract class InventoryBlockDataStorageHolder extends BlockDataStorageHolder implements AInventory {

	public static final String DISPLAY_NAME_TAG = "display_name";
	public static final String INVENTORY_STORAGE_TAG = "inventory";

	protected @UnknownNullability Inventory inventory;
	protected @Nullable Component name;

	protected InventoryBlockDataStorageHolder(Block block, ReadWriteNBT nbt) {
		super(block, nbt);
		this.name = nbt.hasTag(DISPLAY_NAME_TAG) ? GsonComponentSerializer.gson().deserialize(nbt.getString(DISPLAY_NAME_TAG)) : null;
	}

	protected InventoryBlockDataStorageHolder(Block block, NamespacedKey storageId) {
		super(block, storageId);
	}

	@Override
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) {
			event.setCancelled(true); // We don't want to place a block, if any
			return;
		}

		Player player = event.getPlayer();
		Block block = getBlock();
		if (player.isSneaking()) {
			// Vanilla allows interacting with an empty hand while sneaking
			if (!player.getInventory().getItemInMainHand().isEmpty()) return;
			// Allow picking up flower with an empty hand
			else if (block.getType() != Material.FLOWER_POT) return;
		}

		// Placing flower
		if (block.getType() == Material.FLOWER_POT && canConvertToPot(player.getInventory().getItemInMainHand().getType())) return;

		event.setCancelled(true); // Also helps not to take the flower from the pot
		player.swingMainHand();
		player.openInventory(getInventory());
	}

	private boolean canConvertToPot(Material type) {
		return type != Material.AIR && Material.matchMaterial(type.getKey().namespace() + ":potted_" + type.getKey().value()) != null;
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		Component name = name();
		if (name != null)
			event.titleOverride(name);
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		List.copyOf(this.inventory.getViewers()).forEach(player -> player.closeInventory(InventoryCloseEvent.Reason.CANT_USE));
	}

	public @UnknownNullability Inventory getInventory() {
		return this.inventory;
	}

	public @Nullable Component name() {
		return this.name;
	}

	public Component name(Player player) {
		Component name = name();
		return name == null ? Messenger.messenger(player).getMessage("inv." + getId().asString().replace(":", ".") + ".title") : name;
	}

	public void setName(@Nullable Component name) {
		this.name = name;
	}

	@Override
	public ReadWriteNBT save() {
		ReadWriteNBT nbt = super.save();
		if (this.name != null) nbt.setString(DISPLAY_NAME_TAG, GsonComponentSerializer.gson().serialize(this.name));
		return nbt;
	}

}
