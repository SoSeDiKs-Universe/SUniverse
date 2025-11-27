package me.sosedik.utilizer.util;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.utilizer.listener.misc.DelayedActions;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents an action that should be executed at some later point
 * and persist within player logouts
 */
@NullMarked
public abstract class DelayedAction {

	private static final String TICKS_LEFT_TAG = "ticks_left";

	protected String id;
	protected int ticksLeft;

	public DelayedAction(String id, @Nullable ReadableNBT data) {
		this.id = id;
		this.ticksLeft = data == null ? -1 : data.getOrDefault(TICKS_LEFT_TAG, -1);
	}

	/**
	 * Gets the action id
	 *
	 * @return the action id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Gets the amount of ticks this action has left until execution
	 *
	 * @return the amount of ticks this action has left
	 */
	public int getTicksLeft() {
		return this.ticksLeft;
	}

	/**
	 * Updates the amount of ticks this action has left until execution
	 *
	 * @param ticks ticks
	 */
	public void updateDelay(int ticks) {
		this.ticksLeft = ticks;
	}

	/**
	 * Tick action
	 */
	public void tick() {
		// Override if needed
	}

	/**
	 * Aborts this action
	 */
	protected void abortExecution(Player player) {
		DelayedActions.abortAction(player, this);
	}

	/**
	 * Whether the action should be aborted when the player dies
	 *
	 * @return whether the action should be aborted when the player dies
	 */
	public boolean abortOnDeath() {
		return true;
	}

	/**
	 * Execute an action
	 */
	public abstract void execute();

	/**
	 * Saves action data
	 *
	 * @return serialized action data
	 */
	public abstract ReadWriteNBT save();

}
