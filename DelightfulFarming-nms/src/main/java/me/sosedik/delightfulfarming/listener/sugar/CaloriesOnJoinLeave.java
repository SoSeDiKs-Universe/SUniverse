package me.sosedik.delightfulfarming.listener.sugar;

import com.google.common.base.Preconditions;
import me.sosedik.delightfulfarming.feature.sugar.SugarEater;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Initialize and remove SugarEater
 */
@NullMarked
public class CaloriesOnJoinLeave implements Listener {

	private static final String SUGAR_EATER_TAG = "sugar_eater";
	private static final Map<UUID, SugarEater> SUGAR_EATERS = new HashMap<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(PlayerDataLoadedEvent event) {
		Player player = event.getPlayer();
		var sugarEater = new SugarEater(player, event.getData().getOrCreateCompound(SUGAR_EATER_TAG));
		SUGAR_EATERS.put(player.getUniqueId(), sugarEater);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		long timeSinceLastLogin = System.currentTimeMillis() - player.getLastPlayed();
		if (timeSinceLastLogin > 90 * 60 * 1000)
			SugarEater.of(player).onLongAbsence();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSave(PlayerDataSaveEvent event) {
		Player player = event.getPlayer();
		SugarEater sugarEater;
		if (event.isQuit()) {
			sugarEater = SUGAR_EATERS.remove(player.getUniqueId());
			if (sugarEater == null) return;
		} else {
			sugarEater = SugarEater.of(player);
		}

		event.getData().getOrCreateCompound(SUGAR_EATER_TAG).mergeCompound(sugarEater.save());
	}

	public static SugarEater getOrLoad(Player player) {
		Preconditions.checkArgument(player.isOnline(), "Player must be online");
		return SUGAR_EATERS.computeIfAbsent(player.getUniqueId(), k -> new SugarEater(player, PlayerDataStorage.getData(player).getOrCreateCompound(SUGAR_EATER_TAG)));
	}

}
