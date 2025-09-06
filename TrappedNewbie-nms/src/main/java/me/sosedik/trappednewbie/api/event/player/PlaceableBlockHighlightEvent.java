package me.sosedik.trappednewbie.api.event.player;

import me.sosedik.trappednewbie.listener.player.ReachAround;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Called when block is capable of being
 * highlighted for reach-around placement
 */
@NullMarked
public class PlaceableBlockHighlightEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final EquipmentSlot hand;
	private @Nullable Location placeTarget;
	private boolean targetUnknown;
	private boolean cancelled = false;

	public PlaceableBlockHighlightEvent(Player player, EquipmentSlot hand, @Nullable Location placeTarget) {
		super(player);
		this.hand = hand;
		this.placeTarget = placeTarget;
		this.targetUnknown = placeTarget == null;
	}

	public EquipmentSlot getHand() {
		return this.hand;
	}

	public @Nullable Location getPlaceTarget() {
		if (this.targetUnknown && this.placeTarget == null) {
			this.placeTarget = ReachAround.getPlayerReachAroundTarget(getPlayer());
			this.targetUnknown = false;
		}
		return this.placeTarget;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
