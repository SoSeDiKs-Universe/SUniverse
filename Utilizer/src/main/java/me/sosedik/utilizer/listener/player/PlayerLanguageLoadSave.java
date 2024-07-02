package me.sosedik.utilizer.listener.player;

import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
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
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads and saves player's language
 */
public class PlayerLanguageLoadSave implements Listener {

	private static final Map<UUID, LangHolder> LANG_HOLDERS = new ConcurrentHashMap<>();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onLoad(@NotNull PlayerDataLoadedEvent event) {
		Player player = event.getPlayer();
		ReadWriteNBT data = event.getData();
		LangOptions langOptions = LangOptionsStorage.getLangOptions(data.getString("server_language"));
		String translationLanguageId = data.getOrNull("translation_language", String.class);
		if (translationLanguageId == null) {
			translationLanguageId = langOptions.translatorId();
			if (translationLanguageId == null)
				translationLanguageId = "en"; // TODO actual translations
		}
		var translationLanguage = new TranslationLanguage(translationLanguageId);
		var langHolder = new LangHolder(player.getUniqueId(), langOptions, translationLanguage);
		LANG_HOLDERS.put(player.getUniqueId(), langHolder);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSave(@NotNull PlayerDataSaveEvent event) {
		if (!event.isQuit()) return;

		LANG_HOLDERS.remove(event.getPlayer().getUniqueId());
	}

	public static @NotNull LangHolder getLangHolder(@NotNull UUID uuid) {
		return LANG_HOLDERS.get(uuid);
	}

}
