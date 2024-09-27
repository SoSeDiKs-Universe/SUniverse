package me.sosedik.uglychatter.api.chat;

import io.papermc.paper.chat.ChatRenderer;
import me.sosedik.uglychatter.api.markdown.MiniMarkdown;
import me.sosedik.uglychatter.api.mini.placeholder.EmojiPlaceholder;
import me.sosedik.uglychatter.api.mini.placeholder.ReplacementPlaceholder;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.ChatUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.sosedik.utilizer.api.message.Mini.combined;

/**
 * Renders messages using default fancies like markdown and replacement placeholders
 */
public class FancyMessageRenderer implements ChatRenderer {

	@Override
	public @NotNull Component render(@NotNull Player source, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience viewer) {
		if (!(viewer instanceof Player playerViewer)) {
			Component strippedMessage = ReplacementPlaceholder.stripPlaceholders(message);
			return combined(sourceDisplayName, Component.text(": "), strippedMessage);
		}
		var messenger = Messenger.messenger(viewer);
		String rawMessage = getRawInput(message);
		Component renderedMessage = renderMessage(messenger.miniMessage(), rawMessage, source, playerViewer);
		return combined(
			sourceDisplayName,
			Component.text(": "),
			renderedMessage
		);
	}

	/**
	 * Renders the message using minimessage and registered replacement placeholders
	 *
	 * @param deserializer deserializer for the message
	 * @param rawMessage raw message
	 * @param sender message sender
	 * @param viewer message viewer
	 * @return rendered message
	 */
	public static @NotNull Component renderMessage(@NotNull MiniMessage deserializer, @NotNull String rawMessage, @Nullable Player sender, @Nullable Player viewer) {
		Component message = deserializer.deserialize(rawMessage);
		message = ReplacementPlaceholder.parsePlaceholders(message, sender, viewer);
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
		// User's input is plain anyway, no big deal if something's a miss in a plain form
		// This also allows parsing minimessage tags later, which would be escaped otherwise
		String plain = ChatUtil.getPlainText(message);
		// Replace unsupported emoji sequences with mappings
		if (!rendererTags.contains(FancyRendererTag.SKIP_EMOJI_MAPPINGS)) plain = EmojiPlaceholder.applyMappings(plain);
		// Strip placeholders
		if (!rendererTags.contains(FancyRendererTag.SKIP_PLACEHOLDERS)) plain = ReplacementPlaceholder.stripPlaceholders(plain);
		// Apply markdown
		if (!rendererTags.contains(FancyRendererTag.SKIP_MARKDOWN)) plain = MiniMarkdown.markdownToMini(plain);
		return plain;
	}

}
