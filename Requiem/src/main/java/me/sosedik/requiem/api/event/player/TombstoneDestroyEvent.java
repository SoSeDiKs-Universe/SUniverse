package me.sosedik.requiem.api.event.player;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class TombstoneDestroyEvent extends BlockEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final @Nullable Player player;
	private final List<ReadWriteNBT> storages;
	private int exp = 0;
	private final List<ItemStack> toDrop = new ArrayList<>();

	public TombstoneDestroyEvent(Block block, @Nullable Player player, List<ReadWriteNBT> storages) {
		super(block);
		this.player = player;
		this.storages = storages;
	}

	public @Nullable Player getPlayer() {
		return this.player;
	}

	public List<ReadWriteNBT> getStorages() {
		return this.storages;
	}

	public void addExp(int exp) {
		this.exp += exp;
	}

	public int getExp() {
		return this.exp;
	}

	public List<ItemStack> getToDrop() {
		return this.toDrop;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
