package me.sosedik.requiem.api.event.player;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.requiem.impl.block.TombstoneBlockStorage;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PlayerTombstoneCreateEvent extends PlayerEvent {

	private static final HandlerList HANDLERS = new HandlerList();

	private final PlayerDeathEvent parentEvent;
	private final ReadWriteNBT data;

	public PlayerTombstoneCreateEvent(PlayerDeathEvent parentEvent) {
		super(parentEvent.getPlayer());
		this.parentEvent = parentEvent;
		this.data = NBT.createNBTObject();
	}

	public PlayerDeathEvent getParentEvent() {
		return this.parentEvent;
	}

	public ReadWriteNBT getData() {
		return this.data;
	}

	public void addExp(int exp) {
		this.data.setInteger(TombstoneBlockStorage.EXP_KEY, getExp() + exp);
	}

	public int getExp() {
		return this.data.getOrDefault(TombstoneBlockStorage.EXP_KEY, 0);
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
