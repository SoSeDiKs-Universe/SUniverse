package me.sosedik.utilizer.util;

import com.google.common.math.DoubleMath;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

	/**
	 * Word wraps the provided component to the provided width
	 *
	 * @param component component
	 * @param length wrap length
	 * @return split component
	 */
	public static List<Component> wrapComponent(final Component component, final int length) {
		if (!(component instanceof final TextComponent text)) {
			return new ArrayList<>(List.of(component));
		}

		final List<Component> wrapped = new ArrayList<>();
		final List<Component> parts = flattenTextComponents(text);

		Component currentLine = Component.empty();
		int lineLength = 0;

		for (final Component part : parts) {
			if (!(part instanceof TextComponent textComponent)) {
				lineLength += Math.max(1, getPlainText(part).length());
				currentLine = currentLine.append(part);
				continue;
			}

			final Style style = textComponent.style();
			final String content = textComponent.content();
			final String[] words = content.split("(?<=\\s)|(?=\\n)");

			for (final String word : words) {
				if (word.isEmpty()) {
					continue;
				}

				final int wordLength = word.length();
				final int totalLength = lineLength + wordLength;
				if (totalLength > length || word.contains("\n")) {
					wrapped.add(currentLine);
					currentLine = Component.empty().style(style);
					lineLength = 0;
				}

				if (!word.equals("\n")) {
					currentLine = currentLine.append(Component.text(word).style(style));
					lineLength += wordLength;
				}
			}
		}

		if (lineLength > 0) {
			wrapped.add(currentLine);
		}

		return wrapped;
	}

	private static List<Component> flattenTextComponents(final TextComponent component) {
		final List<Component> flattened = new ArrayList<>();
		final Style style = component.style();
		final Style enforcedState = enforceStyleStates(style);
		final TextComponent first = component.style(enforcedState);

		final Stack<Component> toCheck = new Stack<>();
		toCheck.add(first);

		while (!toCheck.empty()) {
			final Component parent = toCheck.pop();
			if (!(parent instanceof TextComponent textComponent)) {
				flattened.add(parent);
				continue;
			}
			final String content = textComponent.content();
			if (!content.isEmpty()) {
				flattened.add(parent);
			}

			final List<Component> reversedChildren = parent.children().reversed();
			for (final Component child : reversedChildren) {
				final Style parentStyle = parent.style();
				final Style textStyle = child.style();
				final Style merge = parentStyle.merge(textStyle);
				final Component childComponent = child.style(merge);
				toCheck.add(childComponent);
			}
		}

		return flattened;
	}

	private static Style enforceStyleStates(final Style style) {
		final Style.Builder builder = style.toBuilder();
		final Map<TextDecoration, TextDecoration.State> map = style.decorations();
		map.forEach((decoration, state) -> {
			if (state == TextDecoration.State.NOT_SET) {
				builder.decoration(decoration, false);
			}
		});
		return builder.build();
	}

}
