package me.sosedik.utilizer.impl.translator;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Exception produced during translation
 */
@NullMarked
public class TranslatorException extends Exception {

	public TranslatorException(String message) {
		super(message);
	}

	public TranslatorException(String message, @Nullable Throwable cause) {
		super(message, cause);
	}

}
