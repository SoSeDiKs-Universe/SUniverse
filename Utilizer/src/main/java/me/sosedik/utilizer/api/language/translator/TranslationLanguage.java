package me.sosedik.utilizer.api.language.translator;

import org.jetbrains.annotations.NotNull;

/**
 * A language used in translation service
 *
 * @param displayName native display name
 * @param id language identifier
 */
public record TranslationLanguage(@NotNull String displayName,
                                  @NotNull String id) {

	/**
	 * A language used in translation service
	 *
	 * @param id language identifier
	 */
	public TranslationLanguage(@NotNull String id) {
		this("TOD", id); // ToDo
	}

}
