package me.sosedik.utilizer.api.language;

import me.sosedik.utilizer.api.language.translator.TranslationLanguage;
import me.sosedik.utilizer.api.storage.player.PlayerDataStorage;
import me.sosedik.utilizer.listener.player.PlayerLanguageLoadSave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class LangHolder {

	private final UUID uuid;
	private LangOptions langOptions;
	private @Nullable TranslationLanguage translationLanguage;

	public LangHolder(@NotNull UUID uuid, @NotNull LangOptions langOptions, @Nullable TranslationLanguage translationLanguage) {
		this.uuid = uuid;
		this.langOptions = langOptions;
		this.translationLanguage = translationLanguage;
	}

	public @NotNull UUID getUUID() {
		return this.uuid;
	}

	public @Nullable Player getPlayer() {
		return Bukkit.getPlayer(getUUID());
	}

	public @NotNull LangOptions getLangOptions() {
		Player player = getPlayer();
		if (player == null) return this.langOptions;

		LangOptions clientLang = LangOptionsStorage.getLangOptionsIfExist(player.getLocale());
		return clientLang == null ? this.langOptions : clientLang;
	}

	public @NotNull TranslationLanguage getTranslationLanguage() {
		if (this.translationLanguage != null) return this.translationLanguage;

		return LangOptionsStorage.getTranslatorLanguage(getLangOptions().minecraftId());
	}

	/**
	 * Set this player's server language
	 *
	 * @param langOptions new server language
	 */
	public void setLangOptions(@NotNull LangOptions langOptions) {
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
	public void setTranslationLanguage(@NotNull TranslationLanguage translationLanguage) {
		this.translationLanguage = translationLanguage;
		PlayerDataStorage.getData(getUUID()).setString("translation_language", translationLanguage.id());
	}

	public static @NotNull LangHolder langHolder(@NotNull Player player) {
		return langHolder(player.getUniqueId());
	}

	public static @NotNull LangHolder langHolder(@NotNull UUID uuid) {
		return PlayerLanguageLoadSave.getLangHolder(uuid);
	}

}
