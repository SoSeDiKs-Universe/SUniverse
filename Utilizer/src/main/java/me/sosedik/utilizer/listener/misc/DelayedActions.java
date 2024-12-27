package me.sosedik.utilizer.listener.misc;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.util.DelayedAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;

/**
 * Simplifies handling of delayed actions that should persist between player logins
 */
public class DelayedActions implements Listener {

	private static final String DELAYED_ACTIONS_TAG = "delayed_actions";
	private static final String DELAYED_ACTION_KEY_TAG = "id";
	private static final String DELAYED_ACTION_TIME_TAG = "ticks_left";

	private static final Map<String, BiFunction<Player, ReadableNBT, DelayedAction>> KNOWN_DELAYED_ACTIONS = new HashMap<>();
	private static final Map<UUID, Queue<DelayedAction>> DELAYED_ACTIONS = new HashMap<>();

	public DelayedActions() {
		Utilizer.scheduler().sync(() -> DELAYED_ACTIONS.values().forEach(delayedActions -> {
			delayedActions.removeIf(action -> {
				action.updateDelay(action.getTicksLeft() - 1);
				action.tick();
				if (action.getTicksLeft() <= 0) {
					action.execute();
					return true;
				}
				return false;
			});
		}), 0L, 1L);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onLoad(PlayerDataLoadedEvent event) {
		if (!event.getData().hasTag(DELAYED_ACTIONS_TAG)) return;

		Player player = event.getPlayer();
		ReadWriteNBTCompoundList datas = event.getData().getCompoundList(DELAYED_ACTIONS_TAG);
		for (ReadWriteNBT data : datas) {
			if (!data.hasTag(DELAYED_ACTION_KEY_TAG)) continue;

			String id = data.getString(DELAYED_ACTION_KEY_TAG);
			BiFunction<Player, ReadableNBT, DelayedAction> supplier = KNOWN_DELAYED_ACTIONS.get(id);
			if (supplier == null) continue;

			DelayedAction delayedAction = supplier.apply(player, data);
			if (delayedAction.getTicksLeft() == -1) delayedAction.updateDelay(data.getOrDefault(DELAYED_ACTION_TIME_TAG, -1));
			DELAYED_ACTIONS.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentLinkedQueue<>()).add(delayedAction);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onSave(PlayerDataSaveEvent event) {
		if (!event.isQuit()) return;

		Queue<DelayedAction> delayedActions = DELAYED_ACTIONS.remove(event.getPlayer().getUniqueId());
		if (delayedActions == null) return;

		ReadWriteNBTCompoundList datas = event.getData().getCompoundList(DELAYED_ACTIONS_TAG);
		delayedActions.forEach(delayedAction -> {
			ReadWriteNBT data = delayedAction.save();
			data.setString(DELAYED_ACTION_KEY_TAG, delayedAction.getId());
			data.setInteger(DELAYED_ACTION_TIME_TAG, delayedAction.getTicksLeft());
			datas.addCompound(data);
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		Queue<DelayedAction> delayedActions = DELAYED_ACTIONS.get(event.getPlayer().getUniqueId());
		if (delayedActions == null) return;

		delayedActions.removeIf(DelayedAction::abortOnDeath);
	}

	/**
	 * Checks whether the player has an action delayed
	 *
	 * @param player player
	 * @param id action id
	 * @return whether the player has an action delayed
	 */
	public static boolean isActive(Player player, String id) {
		Queue<DelayedAction> delayedActions = DELAYED_ACTIONS.get(player.getUniqueId());
		if (delayedActions == null) return false;
		// Some actions might have multiple instances, hence not a map
		for (DelayedAction delayedAction : delayedActions) {
			if (id.equals(delayedAction.getId()))
				return true;
		}
		return false;
	}

	/**
	 * Schedules an action
	 *
	 * @param player action owner
	 * @param delayedAction delayed action
	 * @param ticksDelay ticks left to execution
	 */
	public static void scheduleAction(Player player, DelayedAction delayedAction, int ticksDelay) {
		delayedAction.updateDelay(ticksDelay);
		DELAYED_ACTIONS.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentLinkedQueue<>()).add(delayedAction);
	}

	/**
	 * Aborts an action
	 *
	 * @param player action owner
	 * @param delayedAction delayed action
	 */
	public static void abortAction(Player player, DelayedAction delayedAction) {
		Queue<DelayedAction> delayedActions = DELAYED_ACTIONS.get(player.getUniqueId());
		if (delayedActions == null) return;

		delayedActions.remove(delayedAction);
	}

	/**
	 * Registers a new delayed action type
	 *
	 * @param id delayed action id
	 * @param loader delayed action loader
	 */
	public static void registerDelayedAction(
		String id,
		BiFunction<Player, ReadableNBT, DelayedAction> loader
	) {
		KNOWN_DELAYED_ACTIONS.put(id, loader);
	}

}
