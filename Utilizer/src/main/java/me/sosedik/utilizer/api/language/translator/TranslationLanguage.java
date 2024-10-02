package me.sosedik.utilizer.api.language.translator;

import org.jetbrains.annotations.NotNull;

/**
 * A language used in translation service
 *
 * @param id language identifier
 * @param displayName native display name
 */
public record TranslationLanguage(
	@NotNull String id,
	@NotNull String displayName
) {

	/**
	 * Auto detect language
	 */
	public static final TranslationLanguage AUTO = new TranslationLanguage("auto", "Auto");

}
