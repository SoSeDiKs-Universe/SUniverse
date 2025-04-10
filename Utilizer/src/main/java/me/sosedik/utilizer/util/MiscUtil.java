package me.sosedik.utilizer.util;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

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
	public static <T extends Enum<T>> T parseOr(@Nullable String value, T defaultValue) {
		if (value == null || value.isEmpty()) return defaultValue;
		try {
			return (T) Enum.valueOf(defaultValue.getClass(), value.toUpperCase(Locale.US));
		} catch (IllegalArgumentException ignored) {
			return defaultValue;
		}
	}

	/**
	 * Tries to get the enum by its name, or returns {@code null} if not found
	 *
	 * @param value enum name
	 * @param enumClass enum class
	 * @return parsed enum
	 * @param <T> enum
	 */
	public static <T extends Enum<T>> @Nullable T parseOrNull(@Nullable String value, Class<T> enumClass) {
		if (value == null || value.isEmpty()) return null;
		try {
			return Enum.valueOf(enumClass, value.toUpperCase(Locale.US));
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

}
