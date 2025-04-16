package me.sosedik.utilizer.util;

import com.google.common.math.DoubleMath;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ChatUtil {

	private ChatUtil() {
		throw new IllegalStateException("Utility class");
	}

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
	public static String getPlainText(Component component) {
		return PlainTextComponentSerializer.plainText().serialize(component);
	}

	/**
	 * Formats double value
	 *
	 * @param value value
	 * @return formatted number
	 */
	public static String formatDouble(double value) {
		return DoubleMath.isMathematicalInteger(value) ? String.valueOf((int) value) : String.valueOf(value);
	}

}
