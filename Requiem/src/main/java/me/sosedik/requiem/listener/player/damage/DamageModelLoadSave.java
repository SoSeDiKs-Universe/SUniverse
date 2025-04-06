package me.sosedik.requiem.listener.player.damage;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import me.sosedik.requiem.feature.playermodel.PlayerDamageModel;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Loads and saved player damage models
 */
@NullMarked
public class DamageModelLoadSave implements Listener {

	private static final String DAMAGE_MODEL_TAG = "damage_model";
	private static final Map<UUID, PlayerDamageModel> DAMAGE_MODELS = new HashMap<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(PlayerDataLoadedEvent event) {
		Player player = event.getPlayer();
		ReadableNBT data = event.getData().getCompound(DAMAGE_MODEL_TAG);
		var damageModel = new PlayerDamageModel(player, data);
		DAMAGE_MODELS.put(player.getUniqueId(), damageModel);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSave(PlayerDataSaveEvent event) {
		Player player = event.getPlayer();
		PlayerDamageModel damageModel = event.isQuit() ? DAMAGE_MODELS.remove(player.getUniqueId()) : DAMAGE_MODELS.get(player.getUniqueId());
		if (damageModel == null) return;

		ReadWriteNBT data = event.getData().getOrCreateCompound(DAMAGE_MODEL_TAG);
		damageModel.save(data);
	}

	public static PlayerDamageModel of(Player player) {
		return DAMAGE_MODELS.computeIfAbsent(player.getUniqueId(), k -> new PlayerDamageModel(player, PlayerDataStorage.getData(player).getCompound(DAMAGE_MODEL_TAG)));
	}

	public static void saveAll() {
		DAMAGE_MODELS.forEach((uuid, damageModel) -> {
			ReadWriteNBT data = PlayerDataStorage.getData(uuid).getOrCreateCompound(DAMAGE_MODEL_TAG);
			data.clearNBT();
			damageModel.save(data);
		});
		DAMAGE_MODELS.clear();
	}

}
