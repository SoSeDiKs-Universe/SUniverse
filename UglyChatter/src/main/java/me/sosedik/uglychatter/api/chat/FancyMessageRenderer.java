package me.sosedik.uglychatter.api.chat;

import io.papermc.paper.chat.ChatRenderer;
import me.sosedik.uglychatter.api.markdown.MiniMarkdown;
import me.sosedik.uglychatter.api.mini.placeholder.EmojiPlaceholder;
import me.sosedik.uglychatter.api.mini.placeholder.ReplacementPlaceholder;
import me.sosedik.utilizer.api.language.LangHolder;
import me.sosedik.utilizer.api.language.translator.TranslationLanguage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import me.sosedik.utilizer.impl.translator.OnlineTranslator;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.sosedik.utilizer.api.message.Mini.combined;

/**
 * Renders messages using default fancies like markdown and replacement placeholders
 */
public class FancyMessageRenderer implements ChatRenderer {

	private final Map<String, String> translations = new HashMap<>();
	private String cachedRawMessage = null;

	@Override
	public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
		if (!(viewer instanceof Player playerViewer)) {
			Component strippedMessage = ReplacementPlaceholder.stripPlaceholders(message);
			return combined(sourceDisplayName, Component.text(": "), strippedMessage);
		}

		if (this.cachedRawMessage == null) this.cachedRawMessage = getRawInput(message);

		boolean self = source == viewer;
		TextColor baseColor = TextColor.fromHexString(self ? "#fbe9d1" : "#dceefa");
		var messenger = Messenger.messenger(viewer, TagResolver.resolver(
			StandardTags.color(),
			StandardTags.keybind(),
			StandardTags.translatable(),
			StandardTags.translatableFallback(),
			StandardTags.decorations(),
			StandardTags.gradient(),
			StandardTags.rainbow(),
			StandardTags.reset(),
			StandardTags.newline(),
			StandardTags.transition()
		));
		Component renderedMessage = renderAndTranslate(messenger.miniMessage(), this.cachedRawMessage, source, playerViewer, Style.style(baseColor));

		return combined(
			sourceDisplayName,
			Component.text(": "),
			renderedMessage
		);
	}

	/**
	 * Renders the message and adds translated hover to it
	 *
	 * @param deserializer deserializer for the message
	 * @param rawMessage raw message
	 * @param source message sender
	 * @param viewer message viewer
	 * @param baseStyle parent style to use for the message
	 * @return rendered message
	 */
	public @NotNull Component renderAndTranslate(@NotNull MiniMessage deserializer, @NotNull String rawMessage, @NotNull Player source, @NotNull Player viewer, @NotNull Style baseStyle) {
		Component message = renderMessage(deserializer, rawMessage, source, viewer);
		Component hover = getTranslation(deserializer, rawMessage, source, viewer);
		hover = Component.text().style(baseStyle).append(hover).build();
		return Component.text().style(baseStyle).hoverEvent(hover).append(message).build();
	}

	private @NotNull Component getTranslation(@NotNull MiniMessage deserializer, @NotNull String rawMessage, @NotNull Player source, @NotNull Player viewer) {
		TranslationLanguage translateTo = LangHolder.langHolder(viewer).getTranslationLanguage();
		String translated = this.translations.computeIfAbsent(translateTo.id(), k -> OnlineTranslator.translate(rawMessage, TranslationLanguage.AUTO, translateTo));
		return renderMessage(deserializer, translated, source, viewer);
	}

	/**
	 * Renders the message using minimessage and registered replacement placeholders
	 *
	 * @param deserializer deserializer for the message
	 * @param rawMessage raw message
	 * @param source message sender
	 * @param viewer message viewer
	 * @return rendered message
	 */
	public static @NotNull Component renderMessage(@NotNull MiniMessage deserializer, @NotNull String rawMessage, @Nullable Player source, @Nullable Player viewer) {
		Component message = deserializer.deserialize(rawMessage);
		message = ReplacementPlaceholder.parsePlaceholders(message, source, viewer);
		return message;
	}

	/**
	 * Gets the parsed raw message as minimessage with markdown and placeholders ready
	 *
	 * @param message original message
	 * @param tags tags
	 * @return parsed raw message
	 */
	public static @NotNull String getRawInput(@NotNull Component message, @NotNull FancyRendererTag... tags) {
		List<FancyRendererTag> rendererTags = List.of(tags);
		// Try to preserve initial input decorations
		// And unescape escaped placeholders to allow parsing them with minimessage
		String plain = Mini.buildMini()
				.serialize(message)
				.replace("\\<", "<");
		// Replace unsupported emoji sequences with mappings
		if (!rendererTags.contains(FancyRendererTag.SKIP_EMOJI_MAPPINGS)) plain = EmojiPlaceholder.applyMappings(plain);
		// Strip placeholders
		if (!rendererTags.contains(FancyRendererTag.SKIP_PLACEHOLDERS)) plain = ReplacementPlaceholder.stripPlaceholders(plain);
		// Apply markdown
		if (!rendererTags.contains(FancyRendererTag.SKIP_MARKDOWN)) plain = MiniMarkdown.markdownToMini(plain);
		return plain;
	}

}
