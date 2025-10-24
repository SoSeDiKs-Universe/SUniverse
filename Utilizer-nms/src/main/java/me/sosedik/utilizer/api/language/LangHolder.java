package me.sosedik.utilizer.api.language;

import me.sosedik.utilizer.api.language.translator.TranslationLanguage;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import me.sosedik.utilizer.listener.player.PlayerLanguageLoadSave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public final class LangHolder {

	private final UUID uuid;
	private LangOptions langOptions;
	private @Nullable TranslationLanguage translationLanguage;

	public LangHolder(UUID uuid, LangOptions langOptions, @Nullable TranslationLanguage translationLanguage) {
		this.uuid = uuid;
		this.langOptions = langOptions;
		this.translationLanguage = translationLanguage;
	}

	public UUID getUUID() {
		return this.uuid;
	}

	public @Nullable Player getPlayer() {
		return Bukkit.getPlayer(getUUID());
	}

	public LangOptions getLangOptions() {
		Player player = getPlayer();
		if (player == null) return this.langOptions;

		LangOptions clientLang = LangOptionsStorage.getLangOptionsIfExist(player.getLocale());
		return clientLang == null ? this.langOptions : clientLang;
	}

	public TranslationLanguage getTranslationLanguage() {
		if (this.translationLanguage != null) return this.translationLanguage;

		return LangOptionsStorage.getTranslatorLanguage(getLangOptions().minecraftId());
	}

	/**
	 * Set this player's server language
	 *
	 * @param langOptions new server language
	 */
	public void setLangOptions(LangOptions langOptions) {
		Player player = getPlayer();
		if (player == null) return;

		this.langOptions = langOptions;

		PlayerDataStorage.getData(getUUID()).setString("server_language", langOptions.minecraftId());
	}

	/**
	 * Set this player's translation language
	 *
	 * @param translationLanguage translation language
	 */
	public void setTranslationLanguage(TranslationLanguage translationLanguage) {
		this.translationLanguage = translationLanguage;
		PlayerDataStorage.getData(getUUID()).setString("translation_language", translationLanguage.id());
	}

	public static LangHolder langHolder(Player player) {
		return langHolder(player.getUniqueId());
	}

	public static LangHolder langHolder(UUID uuid) {
		return PlayerLanguageLoadSave.getLangHolder(uuid);
	}

}
