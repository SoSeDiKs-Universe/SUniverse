package me.sosedik.resourcelib.util;

import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static me.sosedik.utilizer.api.message.Mini.combine;

public class SpacingUtil {

	private SpacingUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Font used for spacings
	 */
	public static final Key SPACINGS_FONT = Key.key("spacings", "spaces");

	/**
	 * Same as normal space
	 */
	public static final String FAKE_SPACE = ChatUtil.SPACE_REPLACER;
	/**
	 * One pixel back, but in minecraft font
	 */
	public static final String NEGATIVE_PIXEL = "\uF801";
	/**
	 * One pixel forward, but in minecraft font
	 */
	public static final String POSITIVE_PIXEL = "\uF802";
	/**
	 * Space of icon's width
	 */
	public static final Component ICON_SPACE = getSpacing(9);

	/**
	 * Color used in text shader to position action bar text to the top left corner
	 */
	public static final TextColor TOP_LEFT_CORNER_HUD = Objects.requireNonNull(TextColor.fromHexString("#2804F9"));

	/**
	 * Color used in text shader to position tab text to the top left corner
	 */
	public static final TextColor TOP_LEFT_CORNER_TAB = Objects.requireNonNull(TextColor.fromHexString("#2404F9"));

	/**
	 * Color used in text shader to position action bar text to the top left corner
	 */
	public static final TextColor TOP_RIGHT_CORNER_HUD = Objects.requireNonNull(TextColor.fromHexString("#6804F9"));

	/**
	 * Color used in text shader to position tab text to the top left corner
	 */
	public static final TextColor TOP_RIGHT_CORNER_TAB = Objects.requireNonNull(TextColor.fromHexString("#6404F9"));

	/**
	 * Gets raw spacing symbols
	 *
	 * @param spacing spacing
	 * @return spacings string without font applied
	 */
	public static @NotNull String getSpacingSymbols(int spacing) {
		return new String(Character.toChars(688128 + spacing));
	}

	/**
	 * Gets offset Component
	 *
	 * @param offset needed offset
	 * @return offset Component
	 */
	public static @NotNull Component getOffset(int offset, @NotNull Component component) {
		return getOffset(offset, getWidth(component), component);
	}

	/**
	 * Gets offset Component using non-splitting characters
	 *
	 * @param offset    needed offset
	 * @param length    component length
	 * @param component component
	 * @return offset Component
	 */
	public static @NotNull Component getOffset(int offset, int length, @NotNull Component component) {
		if (offset == 0) return Mini.combined(component, getSpacing(-length));
		return Mini.combined(getSpacing(offset), component, getSpacing(-(offset + length)));
	}

	/**
	 * Gets spacing Component
	 *
	 * @param spacing needed spacing
	 * @return spaced Component
	 */
	public static @NotNull Component getSpacing(int spacing) {
		return Component.text(getSpacingSymbols(spacing)).font(SPACINGS_FONT);
	}

	/**
	 * Gets component for fixing overlapping layers
	 *
	 * @return component for fixing overlapping layers
	 */
	public static @NotNull Component getLayerFixer() {
		return getSpacing(4097);
	}

	/**
	 * Gets fontless component with -1 offset
	 *
	 * @return component with -1 offset without a font applied
	 */
	public static @NotNull Component getNegativePixel() {
		return Component.text(NEGATIVE_PIXEL);
	}

	/**
	 * Constructs components list with an icon prefix
	 *
	 * @param icon        icon
	 * @param messagePath path for message
	 * @return components list
	 */
	public static @NotNull List<Component> iconize(@NotNull Messenger messenger, @NotNull Component icon, @NotNull String messagePath, @NotNull TagResolver... placeholders) {
		String[] message = messenger.getRawMessage(messagePath);
		List<Component> components = new ArrayList<>();
		components.add(combine(Component.space(), icon, Mini.mini(messenger.miniMessage(), message[0], placeholders)));
		for (int j = 1; j < message.length; j++)
			components.add(combine(Component.space(), ICON_SPACE, Mini.mini(messenger.miniMessage(), message[j], placeholders)));
		return components;
	}

	/**
	 * Calculates pixel width of the component
	 *
	 * @param component component
	 * @return pixel width of the component
	 */
	public static int getWidth(@NotNull Component component) {
		String content = ChatUtil.getPlainText(component);
		boolean bold = component.hasDecoration(TextDecoration.BOLD);
		int length = content.length();
		if (bold)
			length *= 2;
		for (char ch : content.toCharArray()) {
			int width = getWidth(ch);
			length += width;
		}
		return length;
	}

	/**
	 * Returns pixel width of the character
	 *
	 * @param ch character
	 * @return pixel width of the character
	 */
	public static int getWidth(int ch) {
		if (Character.isUpperCase(ch)) {
			return switch (ch) {
				// English
				case 'I' -> 3;
				// Cyrillic
				case 'І', 'Ї' -> 3;
				case 'Ъ', 'Д' -> 6;
				case 'Ш', 'Ф', 'Ы', 'Ж', 'Ю' -> 7;
				case 'Щ' -> 8;
				default -> 5;
			};
		} else if (Character.isDigit(ch)) {
			return 5;
		} else if (Character.isLowerCase(ch)) {
			return switch (ch) {
				// English
				case 'i' -> 1;
				case 'l' -> 2;
				case 't' -> 3;
				case 'f', 'k' -> 4;
				// Cyrillic
				case 'і' -> 1;
				case 'ї' -> 3;
				case 'к' -> 4;
				case 'д', 'ы', 'щ' -> 6;
				case 'ю' -> 7;
				default -> 5;
			};
		} else {
			return switch (ch) {
				case '!', '¡', '.', ',', ';', ':', '|' -> 1;
				case '\'', '`' -> 2;
				case '*', '(', ')', '[', ']', '{', '}', ' ' -> 3;
				case '<', '>', '°' -> 4;
				case '@', '~' -> 6;
				default -> 5;
			};
		}
	}

}
