package me.sosedik.utilizer.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class MiscUtil {

	private MiscUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Tries to get the enum by its name, or returns
	 * the default value if not found
	 *
	 * @param value enum name
	 * @param defaultValue default value
	 * @return parsed enum
	 * @param <T> enum
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Enum<?>> T parseOr(@Nullable String value, T defaultValue) {
		if (value == null || value.isEmpty()) return defaultValue;
		try {
			return (T) Enum.valueOf(defaultValue.getClass(), value.toUpperCase());
		} catch (Exception ignored) {
			return defaultValue;
		}
	}

}
