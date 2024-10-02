package me.sosedik.utilizer.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChatUtil {

	private ChatUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Color used in the shader to remove the text's shadow
	 */
	public static final TextColor SHADOWLESS_COLOR = Objects.requireNonNull(TextColor.fromHexString("#4e5c24"));

	/**
	 * Gets the symbol replacing the spaces in places where spaces are unsupported
	 */
	public static final String SPACE_REPLACER = "\uF834";

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
