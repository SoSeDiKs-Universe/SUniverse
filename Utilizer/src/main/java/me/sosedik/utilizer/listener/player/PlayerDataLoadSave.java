package me.sosedik.utilizer.listener.player;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controls basic player data flow
 */
@NullMarked
public class PlayerDataLoadSave implements Listener {

	private static final Map<UUID, ReadWriteNBT> STORED_DATA = new HashMap<>();

	private static @UnknownNullability Plugin plugin;

	public PlayerDataLoadSave(Plugin plugin) {
		PlayerDataLoadSave.plugin = plugin;

		long saveInterval = 5 * 60 * 20L;
		Utilizer.scheduler().sync(() -> Bukkit.getOnlinePlayers().forEach(player -> saveData(player, true)), saveInterval, saveInterval);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPreJoin(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;

		UUID uuid = event.getUniqueId();
		Utilizer.scheduler().async(() -> {
			ReadWriteNBT data = loadData(uuid);
			STORED_DATA.put(uuid, data);
		});
	}

	// Leave LOWEST for preparation events that should run before custom data overrides them
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ReadWriteNBT data = getOrLoadData(player);
		var dataLoadedEvent = new PlayerDataLoadedEvent(player, data);
		dataLoadedEvent.callEvent();
		data.clearNBT();
		data.mergeCompound(dataLoadedEvent.getBackupData());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuit(PlayerConnectionCloseEvent event) {
		UUID uuid = event.getPlayerUniqueId();
		ReadWriteNBT data = STORED_DATA.remove(uuid);
		if (data == null) return;

		File dataFile = new File(plugin.getDataFolder(), "players/" + uuid + ".dat");
		Utilizer.scheduler().async(() -> saveData(dataFile, data));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuit(PlayerQuitEvent event) {
		saveData(event.getPlayer(), false);
	}

	private static ReadWriteNBT loadData(UUID uuid) {
		File dataFile = new File(plugin.getDataFolder(), "players/" + uuid + ".dat");
		if (!dataFile.exists()) return NBT.createNBTObject();

		return FileUtil.readNbtFile(dataFile);
	}

	private static synchronized void saveData(Player player, boolean keepData) {
		UUID uuid = player.getUniqueId();
		ReadWriteNBT data = keepData ? STORED_DATA.get(uuid) : STORED_DATA.remove(uuid);
		if (data == null) return; // Shouldn't happen

		var event = new PlayerDataSaveEvent(player, data, !keepData);
		event.callEvent();
		ReadWriteNBT saveData = event.getData();
		File dataFile = new File(plugin.getDataFolder(), "players/" + uuid + ".dat");
		Utilizer.scheduler().async(() -> saveData(dataFile, saveData));
	}

	private static synchronized void saveData(File file, ReadWriteNBT data) {
		try {
			NBT.writeFile(file, data);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't save player data file!", e);
		}
	}

	/**
	 * Gets currently present player nbt data
	 *
	 * @param player player
	 * @return player nbt data
	 */
	private static ReadWriteNBT getOrLoadData(Player player) {
		UUID uuid = player.getUniqueId();
		ReadWriteNBT data = STORED_DATA.get(uuid);
		return data == null ? loadData(uuid) : data;
	}

	/**
	 * Gets currently present player nbt data
	 *
	 * @param uuid uuid
	 * @return player nbt data
	 */
	public static ReadWriteNBT getData(UUID uuid) {
		ReadWriteNBT data = STORED_DATA.get(uuid);
		return data == null ? NBT.createNBTObject() : data;
	}

	/**
	 * Saves and unloads data for all online players
	 */
	public static void saveAllData() {
		STORED_DATA.forEach((uuid, nbt) -> {
			File dataFile = new File(plugin.getDataFolder(), "players/" + uuid + ".dat");
			saveData(dataFile, nbt);
		});
		STORED_DATA.clear();
	}

}
