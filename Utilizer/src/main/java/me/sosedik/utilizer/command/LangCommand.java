package me.sosedik.utilizer.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.utilizer.api.command.parser.AnyString;
import me.sosedik.utilizer.api.language.LangHolder;
import me.sosedik.utilizer.api.language.LangOptions;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.ChatUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Picking the server language fallback
 */
public class LangCommand {

	private final Map<String, LangOptions> languages = new HashMap<>();

	public LangCommand() {
		for (LangOptions langOptions : LangOptionsStorage.getSupportedLanguages()) {
			String key = langOptions.displayName().replace(" ", ChatUtil.SPACE_REPLACER);
			languages.put(key, langOptions);
		}
	}

	@Command("lang <language> [player]")
	public void onCommand(
		@NotNull CommandSourceStack stack,
		@NotNull @Argument(value = "language", suggestions = "@translatorCommandSuggestionTranslators") AnyString languageKeyS,
		@Nullable @Argument(value = "player") Player player,
		@Flag(value = "silent") boolean silent
	) {
		String languageKey = languageKeyS.string();

		Player target;
		if (player == null) {
			if (!(stack.getExecutor() instanceof Player executor)) return;
			target = executor;
		} else {
			target = player;
		}

		LangOptions langOptions = languages.get(languageKey);
		if (langOptions == null) {
			Messenger.messenger(stack.getSender()).sendMessage("command.lang.unsupported", raw("language", langOptions.displayName()));
			return;
		}

		var langHolder = LangHolder.langHolder(target);
		langHolder.setLangOptions(langOptions);
		if (!silent) Messenger.messenger(target).sendMessage("command.lang.set", raw("language", langOptions.displayName()));
		if (stack.getSender() != target) Messenger.messenger(stack.getSender()).sendMessage("command.lang.set.other", raw("language", langOptions.displayName()), raw("player", target.displayName()));
	}

	@Suggestions("@translatorCommandSuggestionTranslators")
	public @NotNull Set<String> onTranslatorSuggestion(@NotNull CommandSourceStack stack) {
		return languages.keySet();
	}

}