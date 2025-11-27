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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static me.sosedik.utilizer.api.message.Mini.combine;

/**
 * Handles emoji placeholders, e.g. {@code :sparkles:} to {@code ✨}
 */
@NullMarked
public class EmojiPlaceholder extends ReplacementPlaceholder {

	private static final Map<String, String> EMOJI_REPLACEMENTS = new LinkedHashMap<>();
	private static final String EMOJI_VARIANT_SYMBOL = "\ufe0f";

	private final Component display;

	public EmojiPlaceholder(String rawEmoji, Set<String> aliases) {
		// aliases is modified later, hence the copy of the set
		Iterator<String> aliasesIterator = new HashSet<>(aliases).iterator();

		String mapping = aliasesIterator.next();
		this.shortcode = mapping;

		String emojied = mapping.substring(0, mapping.length() - 1) + rawEmoji + ":";
		ExtraChatTabSuggestions.addTabSuggestion(emojied);
		aliases.add(emojied);

		List<Component> hovers = new ArrayList<>();
		hovers.add(Component.text(emojied));
		while (aliasesIterator.hasNext()) {
			String current = aliasesIterator.next();
			emojied = current.substring(0, current.length() - 1) + rawEmoji + ":";
			hovers.add(Component.text(emojied));
			ExtraChatTabSuggestions.addTabSuggestion(emojied);
			aliases.add(emojied);
			if (current.length() < mapping.length())
				mapping = current;
		}

		this.display = Component.text().content(rawEmoji)
			.color(NamedTextColor.WHITE)
			.hoverEvent(combine(Component.newline(), hovers))
			.clickEvent(ClickEvent.copyToClipboard(rawEmoji))
			.build();

		aliases.add(rawEmoji); // Parse raw symbols as well to always preserve a fancy display
		setReplacementPattern(aliases);

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
	public static String applyMappings(String text) {
		for (Map.Entry<String, String> entry : EMOJI_REPLACEMENTS.entrySet())
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

		// Load emoji remappings
		JsonObject emojiRemappings = FileUtil.readJsonObject(new File(emojiAssetsDir, "emoji_remappings/lang/en_us.json"));
		List<Map.Entry<String, String>> replacements = new ArrayList<>();
		emojiRemappings.entrySet().forEach(entry -> replacements.add(Map.entry(entry.getKey(), entry.getValue().getAsString())));
		replacements.sort((e1, e2) -> Integer.compare(e2.getKey().length(), e1.getKey().length()));
		replacements.forEach(entry -> EMOJI_REPLACEMENTS.put(entry.getKey(), entry.getValue()));

		// Load emoji mappings
		Map<String, Set<String>> emojiMappings = new HashMap<>();
		var emojiShortcodes = FileUtil.readJsonObject(new File(emojiAssetsDir, "emoji_shortcodes/lang/en_us.json"));
		emojiShortcodes.entrySet().forEach(entry -> {
			String shortcode = entry.getKey();
			String rawEmoji = entry.getValue().getAsString();
			// Replace emoji with a mapping if needed
			rawEmoji = EMOJI_REPLACEMENTS.getOrDefault(rawEmoji, rawEmoji);
			// Save emoji-less variant mapping as well
			if (rawEmoji.endsWith(EMOJI_VARIANT_SYMBOL)) {
				String oldEmoji = rawEmoji;
				rawEmoji = rawEmoji.substring(0, rawEmoji.length() - 1);
				EMOJI_REPLACEMENTS.put(oldEmoji, rawEmoji);
			}
			emojiMappings.computeIfAbsent(rawEmoji, k -> new HashSet<>()).add(shortcode);
		});
		emojiMappings.forEach(EmojiPlaceholder::new);
	}

}
