package me.sosedik.utilizer.listener.player;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import me.sosedik.utilizer.api.event.player.PlayerDataLoadedEvent;
import me.sosedik.utilizer.api.event.player.PlayerDataSaveEvent;
import me.sosedik.utilizer.api.language.LangHolder;
import me.sosedik.utilizer.api.language.LangOptions;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.language.translator.TranslationLanguage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Loads and saves player's language
 */
@NullMarked
public class PlayerLanguageLoadSave implements Listener {

	private static final Map<UUID, LangHolder> LANG_HOLDERS = new HashMap<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(PlayerDataLoadedEvent event) {
		Player player = event.getPlayer();
		ReadWriteNBT data = event.getData();
		LangOptions langOptions = LangOptionsStorage.getLangOptions(data.getString("server_language"));
		String translationLanguageId = data.getOrNull("translation_language", String.class);
		TranslationLanguage translationLanguage = translationLanguageId == null ? null : LangOptionsStorage.getTranslator(translationLanguageId);
		var langHolder = new LangHolder(player.getUniqueId(), langOptions, translationLanguage);
		LANG_HOLDERS.put(player.getUniqueId(), langHolder);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSave(PlayerDataSaveEvent event) {
		if (!event.isQuit()) return;

		LANG_HOLDERS.remove(event.getPlayer().getUniqueId());
	}

	public static LangHolder getLangHolder(UUID uuid) {
		return LANG_HOLDERS.get(uuid);
	}

}
