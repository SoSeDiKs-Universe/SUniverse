package me.sosedik.utilizer.api.language.translator;

import org.jspecify.annotations.NullMarked;

/**
 * A language used in translation service
 *
 * @param id language identifier
 * @param displayName native display name
 */
@NullMarked
public record TranslationLanguage(String id, String displayName) {

	/**
	 * Auto detect language
	 */
	public static final TranslationLanguage AUTO = new TranslationLanguage("auto", "Auto");

}
