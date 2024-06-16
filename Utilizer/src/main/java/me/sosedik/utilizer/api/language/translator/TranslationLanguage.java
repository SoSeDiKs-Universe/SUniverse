package me.sosedik.utilizer.api.language.translator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * A language used in translation service
 *
 * @param displayName native display name
 * @param id language identifier
 */
public record TranslationLanguage(@NonNull String displayName,
                                  @NotNull String id) {

	/**
	 * A language used in translation service
	 *
	 * @param id language identifier
	 */
	public TranslationLanguage(@NonNull String id) {
		this("TOD", id); // ToDo
	}

}
