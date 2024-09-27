package me.sosedik.uglychatter.api.mini.placeholder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a placeholder in a non-minimessage format
 */
public abstract class ReplacementPlaceholder {

	private static final List<ReplacementPlaceholder> REPLACEMENT_PLACEHOLDERS = new ArrayList<>();

	protected Pattern replacementPattern;
	protected TextReplacementConfig shortcodeTextReplacementConfig;
	protected String shortcode;

	protected void register() {
		REPLACEMENT_PLACEHOLDERS.add(this);
	}

	/**
	 * Gets the placeholder shortcode, e.g. {@code :placeholder:}
	 *
	 * @return the placeholder shortcode
	 */
	public @NotNull String getShortcode() {
		return this.shortcode;
	}

	/**
	 * Gets the pattern by which the placeholder is matched
	 *
	 * @return the replacement pattern
	 */
	public @NotNull Pattern getReplacementPattern() {
		return this.replacementPattern;
	}

	/**
	 * Gets the text replacement config to replace the placeholder by its shortcode
	 *
	 * @return the text replacement config
	 */
	public @NotNull TextReplacementConfig getShortcodeTextReplacementConfig() {
		return this.shortcodeTextReplacementConfig;
	}

	protected void setReplacementPattern(@NotNull Pattern replacementPattern) {
		this.replacementPattern = replacementPattern;
		this.shortcodeTextReplacementConfig = TextReplacementConfig.builder()
			.match(replacementPattern)
			.replacement(Component.text(getShortcode()))
			.build();
	}

	protected void setReplacementPattern(@NotNull Collection<String> placeholderKeys) {
		setReplacementPattern(buildPattern(placeholderKeys));
	}

	private @NotNull Pattern buildPattern(@NotNull Collection<String> placeholderKeys) {
		var regex = new StringBuilder();
		for (String placeholderKey : placeholderKeys)
			regex.append('(').append(Pattern.quote(placeholderKey)).append(")|");
		regex.setLength(regex.length() - 1);
		return Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE);
	}

	/**
	 * Gets the placeholder display for the viewer
	 *
	 * @param result match result
	 * @param sender sender
	 * @param viewer viewer
	 * @return the placeholder display
	 */
	public @NotNull Component getDisplay(@NotNull MatchResult result, @Nullable Player sender, @Nullable Player viewer) {
		return getDisplay(sender, viewer);
	}

	/**
	 * Gets the placeholder display for the viewer
	 *
	 * @param sender sender
	 * @param viewer viewer
	 * @return the placeholder display
	 */
	public @NotNull Component getDisplay(@Nullable Player sender, @Nullable Player viewer) {
		return getDisplay(viewer);
	}

	/**
	 * Gets the placeholder display for the viewer
	 *
	 * @param viewer viewer
	 * @return the placeholder display
	 */
	public @NotNull Component getDisplay(@Nullable Player viewer) {
		return getDisplay();
	}

	/**
	 * Gets the placeholder display
	 *
	 * @return the placeholder display
	 */
	public @NotNull Component getDisplay() {
		return Component.empty();
	}

	/**
	 * Gets the registered replacement placeholders
	 *
	 * @return the registered replacement placeholders
	 */
	public static @NotNull List<ReplacementPlaceholder> replacementPlaceholders() {
		return REPLACEMENT_PLACEHOLDERS;
	}

	/**
	 * Parses the registered replacement placeholders
	 *
	 * @param text text
	 * @return modified text
	 */
	public static @NotNull Component parsePlaceholders(@NotNull Component text) {
		return parsePlaceholders(text, null, null);
	}

	/**
	 * Parses the registered replacement placeholders
	 *
	 * @param text text
	 * @param sender sender
	 * @param viewer viewer
	 * @return modified text
	 */
	public static @NotNull Component parsePlaceholders(@NotNull Component text, @Nullable Player sender, @Nullable Player viewer) {
		return parsePlaceholders(replacementPlaceholders(), text, sender, viewer);
	}

	/**
	 * Parses the replacement placeholders
	 *
	 * @param placeholders placeholders
	 * @param text text
	 * @param sender sender
	 * @param viewer viewer
	 * @return modified text
	 */
	public static <T extends ReplacementPlaceholder> @NotNull Component parsePlaceholders(@NotNull List<T> placeholders, @NotNull Component text, @Nullable Player sender, @Nullable Player viewer) {
		for (T placeholder : placeholders) {
			var placeholderCompound = TextReplacementConfig.builder()
				.match(placeholder.getReplacementPattern())
				.replacement((result, builder) -> placeholder.getDisplay(result, sender, viewer))
				.build();
			text = text.replaceText(placeholderCompound);
		}
		return text;
	}

	/**
	 * Replaces registered replacement placeholders with their shortcodes
	 *
	 * @param text text
	 * @return modified text
	 */
	public static @NotNull String stripPlaceholders(@NotNull String text) {
		return stripPlaceholders(replacementPlaceholders(), text);
	}

	/**
	 * Replaces registered replacement placeholders with their shortcodes
	 *
	 * @param text text
	 * @return modified text
	 */
	public static <T extends ReplacementPlaceholder> @NotNull String stripPlaceholders(@NotNull List<T> placeholders, @NotNull String text) {
		var result = new StringBuffer(text);
		for (T placeholder : placeholders) {
			Pattern pattern = placeholder.getReplacementPattern();
			Matcher matcher = pattern.matcher(result);
			StringBuffer tempResult = new StringBuffer();

			while (matcher.find()) {
				matcher.appendReplacement(tempResult, placeholder.getShortcode());
			}
			matcher.appendTail(tempResult);

			result = tempResult;
		}
		return result.toString();
	}

	/**
	 * Replaces registered replacement placeholders with their shortcodes
	 *
	 * @param text text
	 * @return modified text
	 */
	public static @NotNull Component stripPlaceholders(@NotNull Component text) {
		return stripPlaceholders(replacementPlaceholders(), text);
	}

	/**
	 * Replaces placeholders with their shortcodes
	 *
	 * @param placeholders placeholders
	 * @param text text
	 * @return modified text
	 */
	public static <T extends ReplacementPlaceholder> @NotNull Component stripPlaceholders(@NotNull List<T> placeholders, @NotNull Component text) {
		for (T placeholder : placeholders)
			text = text.replaceText(placeholder.getShortcodeTextReplacementConfig());
		return text;
	}

}
