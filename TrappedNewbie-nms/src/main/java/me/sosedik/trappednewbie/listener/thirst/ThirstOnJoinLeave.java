package me.sosedik.trappednewbie.listener.thirst;

import com.google.common.base.Preconditions;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
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
 * Initialize thirst on join and clear on leave
 */
@NullMarked
public class ThirstOnJoinLeave implements Listener {

	private static final Map<UUID, ThirstyPlayer> THIRSTY_PLAYERS = new HashMap<>();
	private static final String THIRST_TAG = "thirst";

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(PlayerDataLoadedEvent event) {
		Player player = event.getPlayer();
		ReadWriteNBT nbt = event.getData();
		nbt = nbt.getOrCreateCompound(THIRST_TAG);
		THIRSTY_PLAYERS.put(player.getUniqueId(), new ThirstyPlayer(player, nbt));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSave(PlayerDataSaveEvent event) {
		Player player = event.getPlayer();
		ThirstyPlayer thirstyPlayer = event.isQuit() ? THIRSTY_PLAYERS.remove(player.getUniqueId()) : THIRSTY_PLAYERS.get(player.getUniqueId());
		if (thirstyPlayer != null)
			event.getData().getOrCreateCompound(THIRST_TAG).mergeCompound(thirstyPlayer.save());
	}

	public static ThirstyPlayer of(Player player) {
		Preconditions.checkArgument(player.isOnline(), "Player must be online");
		return THIRSTY_PLAYERS.get(player.getUniqueId());
	}

	public static void saveAll() {
		THIRSTY_PLAYERS.forEach((uuid, thirstyPlayer) -> PlayerDataStorage.getData(uuid).getOrCreateCompound(THIRST_TAG).mergeCompound(thirstyPlayer.save()));
		THIRSTY_PLAYERS.clear();
	}

}
