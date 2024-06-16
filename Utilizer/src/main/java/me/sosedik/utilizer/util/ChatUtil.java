package me.sosedik.utilizer.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class ChatUtil {

	private ChatUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Converts Component into plain text String
	 *
	 * @param component Component to serialize
	 * @return plain text of the Component
	 */
	public static @NotNull String getPlainText(@NotNull Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}

}
