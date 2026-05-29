package me.sosedik.uglychatter.api.mini.placeholder;

import com.google.gson.JsonObject;
import me.sosedik.utilizer.listener.misc.ExtraChatTabSuggestions;
import me.sosedik.utilizer.util.FileUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NullMarked;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.sosedik.utilizer.api.message.Mini.combine;

/**
 * Handles emoji placeholders, e.g. {@code :sparkles:} to {@code ✨}
 */
@NullMarked
public class EmojiPlaceholder extends ReplacementPlaceholder {

	private static final Map<String, String> REVERSE_EMOJI_REPLACEMENTS = new HashMap<>();
	private static final String EMOJI_VARIANT_SYMBOL = "\ufe0f";

	private final Component display;

	public EmojiPlaceholder(String rawEmoji, Set<String> aliases) {
		// aliases is modified later, hence the copy of the set
		Iterator<String> aliasesIterator = new LinkedHashSet<>(aliases).iterator();

		String mapping = null;
		List<Component> hovers = new ArrayList<>();
		while (aliasesIterator.hasNext()) {
			String current = aliasesIterator.next();
			if (current.charAt(0) != ':') continue;

			String emojied = current.substring(0, current.length() - 1) + rawEmoji + ":";
			hovers.add(Component.text(emojied));
			ExtraChatTabSuggestions.addTabSuggestion(emojied);
			aliases.add(emojied);
			if (mapping == null)
				mapping = current;
			else if (current.length() < mapping.length())
				mapping = current;
		}

		this.shortcode = mapping == null ? rawEmoji : mapping;

		this.display = Component.text().content(rawEmoji)
			.color(NamedTextColor.WHITE)
			.hoverEvent(hovers.isEmpty() ? null : combine(Component.newline(), hovers))
			.clickEvent(ClickEvent.copyToClipboard(rawEmoji))
			.build();

		aliases.add(rawEmoji); // Parse raw symbols as well to always preserve a fancy display
		setReplacementPattern(aliases);

		aliases.forEach(alias -> REVERSE_EMOJI_REPLACEMENTS.put(alias, REVERSE_EMOJI_REPLACEMENTS.getOrDefault(rawEmoji, rawEmoji)));

		register();
	}

	@Override
	public Component getDisplay() {
		return this.display;
	}

	/**
	 * Applies emoji mapping to the string
	 *
	 * @param text text string
	 * @return remapped text string
	 */
	public static String applyReverseMappings(String text) {
		for (Map.Entry<String, String> entry : REVERSE_EMOJI_REPLACEMENTS.entrySet())
			text = text.replace(entry.getKey(), entry.getValue());
		return text;
	}

	/**
	 * Loads emoji mappings
	 *
	 * @param plugin plugin instance
	 */
	public static void setupEmoji(Plugin plugin) {
		var emojiAssetsDir = new File(plugin.getDataFolder(), "emoji/assets");
		if (!emojiAssetsDir.exists()) {
			plugin.getLogger().warning("Emoji assets are missing!");
			return;
		}

		Map<String, Set<String>> emojiMappings = new LinkedHashMap<>();

		// Load emoji remappings (real emoji -> resource pack mapping)
		JsonObject emojiRemappings = FileUtil.readJsonObject(new File(emojiAssetsDir, "emoji_remappings/lang/en_us.json"));
		emojiRemappings.entrySet().stream()
			.sorted((e1, e2) -> Integer.compare(e2.getKey().length(), e1.getKey().length()))
			.forEach(entry -> {
				String realEmoji = entry.getKey();
				String emojiReplacement = entry.getValue().getAsString();
				REVERSE_EMOJI_REPLACEMENTS.put(emojiReplacement, realEmoji);
				Set<String> replacements = emojiMappings.computeIfAbsent(emojiReplacement, _ -> new LinkedHashSet<>());
				replacements.add(realEmoji);
				if (realEmoji.contains(EMOJI_VARIANT_SYMBOL))
					replacements.add(realEmoji.replace(EMOJI_VARIANT_SYMBOL, ""));
			});

		// Load emoji mappings (shortcode -> emoji)
		var emojiShortcodes = FileUtil.readJsonObject(new File(emojiAssetsDir, "emoji_shortcodes/lang/en_us.json"));
		emojiShortcodes.entrySet().forEach(entry -> {
			String shortcode = entry.getKey();
			String emojiReplacement = entry.getValue().getAsString();
			emojiMappings.computeIfAbsent(emojiReplacement, _ -> new LinkedHashSet<>()).add(shortcode);
		});

		emojiMappings.forEach(EmojiPlaceholder::new);
	}

}
