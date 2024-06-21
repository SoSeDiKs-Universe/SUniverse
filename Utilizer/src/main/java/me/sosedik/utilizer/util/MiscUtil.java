package me.sosedik.utilizer.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	public static <T extends Enum<?>> @NotNull T parseOr(@Nullable String value, @NotNull T defaultValue) {
		if (value == null || value.isEmpty()) return defaultValue;
		try {
			return (T) Enum.valueOf(defaultValue.getClass(), value.toUpperCase());
		} catch (Exception ignored) {
			return defaultValue;
		}
	}

}
