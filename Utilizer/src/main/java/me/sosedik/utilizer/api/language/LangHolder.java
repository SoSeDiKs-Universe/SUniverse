package me.sosedik.utilizer.api.language;

import me.sosedik.utilizer.Utilizer;
import me.sosedik.utilizer.api.event.player.PlayerLanguageChangedEvent;
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
	private LangKey langKey;
	private TranslationLanguage translationLanguage;

	public LangHolder(@NotNull UUID uuid, @NotNull LangKey langKey, @NotNull TranslationLanguage translationLanguage) {
		this.uuid = uuid;
		this.langKey = langKey;
		this.translationLanguage = translationLanguage;
	}

	public @NotNull UUID getUUID() {
		return uuid;
	}

	public @Nullable Player getPlayer() {
		return Bukkit.getPlayer(getUUID());
	}

	public @NotNull LangKey getLangKey() {
		Player player = getPlayer();
		if (player == null) return langKey;

		LangKey clientLang = LangKeysStorage.getLangKeyIfExists(player.getLocale());
		return clientLang == null ? langKey : clientLang;
	}

	public @NotNull TranslationLanguage getTranslationLanguage() {
		return translationLanguage;
	}

	/**
	 * Set this player's server language
	 *
	 * @param langKey new server language
	 */
	public void setLangKey(@NotNull LangKey langKey) {
		Player player = getPlayer();
		if (player == null) return;

		LangKey oldLangKey = this.langKey;
		this.langKey = langKey;

		PlayerDataStorage.getData(getUUID()).setString("lang", langKey.locale());

		Utilizer.scheduler().sync(() -> new PlayerLanguageChangedEvent(player, oldLangKey, langKey).callEvent());
	}

	/**
	 * Set this player's translation language
	 *
	 * @param translationLanguage translation language
	 */
	public void setTranslationLanguage(@NotNull TranslationLanguage translationLanguage) {
		this.translationLanguage = translationLanguage;
		PlayerDataStorage.getData(getUUID()).setString("translation_lang", translationLanguage.id());
	}

	public static @NotNull LangHolder langHolder(@NotNull Player player) {
		return langHolder(player.getUniqueId());
	}

	public static @NotNull LangHolder langHolder(@NotNull UUID uuid) {
		return PlayerLanguageLoadSave.getLangHolder(uuid);
	}

	public static @NotNull LangKey getLangKey(@Nullable Player player) {
		return player == null ? LangKeysStorage.getDefaultLangKey() : langHolder(player).getLangKey();
	}

}
