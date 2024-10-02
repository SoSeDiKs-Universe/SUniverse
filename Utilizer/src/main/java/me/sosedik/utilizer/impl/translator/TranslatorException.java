package me.sosedik.utilizer.impl.translator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Exception produced during translation
 */
public class TranslatorException extends Exception {

	public TranslatorException(@NotNull String message) {
		super(message);
	}

	public TranslatorException(@NotNull String message, @Nullable Throwable cause) {
		super(message, cause);
	}

}
