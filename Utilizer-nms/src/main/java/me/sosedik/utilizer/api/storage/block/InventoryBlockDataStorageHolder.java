package me.sosedik.utilizer.api.storage.block;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.api.AInventory;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
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
import xyz.xenondevs.invui.window.Window;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@NullMarked
public abstract class InventoryBlockDataStorageHolder extends BlockDataStorageHolder implements AInventory {

	public static final String DISPLAY_NAME_TAG = "display_name";
	public static final String INVENTORY_STORAGE_TAG = "inventory";

	protected @UnknownNullability Inventory inventory;
	protected @Nullable Map<UUID, Window> windows;
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
		// Vanilla allows interacting with an empty hand while sneaking
		if (player.isSneaking() && !player.getInventory().getItemInMainHand().isEmpty()) return;

		event.setCancelled(true);
		player.swingMainHand();
		openInventory(player);
	}

	public void openInventory(Player player) {
		player.openInventory(this.inventory);
	}

	public boolean isViewing(Player player) {
		if (this.windows != null)
			return this.windows.containsKey(player.getUniqueId());
		return player.getOpenInventory().getTopInventory().equals(this.inventory);
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		if (event.getPlayer() instanceof Player player)
			event.titleOverride(name(player));
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		if (this.inventory != null)
			List.copyOf(this.inventory.getViewers()).forEach(player -> player.closeInventory(InventoryCloseEvent.Reason.CANT_USE));
		if (this.windows != null)
			Map.copyOf(this.windows).values().forEach(Window::close);
	}

	public Inventory getInventory() {
		return this.inventory;
	}

	public @Nullable Window getWindow(Player player) {
		return this.windows == null ? null : this.windows.get(player.getUniqueId());
	}

	public @Nullable Component name() {
		return this.name;
	}

	public Component name(Player player) {
		Component name = name();
		return name == null ? getDefaultName(player) : name;
	}

	public void setName(@Nullable Component name) {
		this.name = name;
	}

	public Component getDefaultName(Player player) {
		return Messenger.messenger(player).getMessage("inv." + getId().asString().replace(":", ".") + ".title");
	}

	@Override
	public ReadWriteNBT save() {
		ReadWriteNBT nbt = super.save();
		if (this.name != null) nbt.setString(DISPLAY_NAME_TAG, GsonComponentSerializer.gson().serialize(this.name));
		return nbt;
	}

}
