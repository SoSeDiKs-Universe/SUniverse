package me.sosedik.utilizer.api.language;

import com.google.gson.JsonObject;
import me.sosedik.utilizer.api.language.translator.TranslationLanguage;
import org.jetbrains.annotations.NotNull;

/**
 * Supported language code
 *
 * @param locale              language's locale in-game
 * @param country             language's country
 * @param name                language name
 * @param emoji               language emoji flag
 * @param translationLanguage translation language
 */
public record LangKey(@NotNull String locale,
                      @NotNull String country,
                      @NotNull String name,
                      @NotNull String emoji,
                      @NotNull TranslationLanguage translationLanguage) {

	public static @NotNull LangKey fromJson(@NotNull JsonObject json) {
		String minecraftId = json.get("locale.id").getAsString();
		String name = json.get("locale.name").getAsString();
		String country = json.get("locale.country").getAsString();
		String emoji = json.get("locale.emoji_key").getAsString();
		String translatorId = json.get("locale.translator_id").getAsString();
		var translationLanguage = new TranslationLanguage(translatorId);
		return new LangKey(minecraftId, country, name, emoji, translationLanguage);
	}

}
