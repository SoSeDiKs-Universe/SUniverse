package me.sosedik.utilizer.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.utilizer.api.command.parser.AnyString;
import me.sosedik.utilizer.api.language.LangHolder;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.language.translator.TranslationLanguage;
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
 * Picking translation language
 */
public class TranslatorCommand {

	private final Map<String, TranslationLanguage> translators = new HashMap<>();

	public TranslatorCommand() {
		for (TranslationLanguage translationLanguage : LangOptionsStorage.getSupportedTranslators()) {
			String key = translationLanguage.id() + "_" + translationLanguage.displayName().replace(" ", ChatUtil.SPACE_REPLACER);
			translators.put(key, translationLanguage);
		}
	}

	@Command("translator <translator> [player]")
	public void onCommand(
		@NotNull CommandSourceStack stack,
		@NotNull @Argument(value = "translator", suggestions = "@translatorCommandSuggestionTranslators") AnyString translatorKeyS,
		@Nullable @Argument(value = "player") Player player,
		@Flag(value = "silent") boolean silent
	) {
		String translatorKey = translatorKeyS.string();

		Player target;
		if (player == null) {
			if (!(stack.getExecutor() instanceof Player executor)) return;
			target = executor;
		} else {
			target = player;
		}

		TranslationLanguage translationLanguage = translators.get(translatorKey);
		if (translationLanguage == null) {
			Messenger.messenger(stack.getSender()).sendMessage("command.translator.unsupported", raw("translation", translatorKey));
			return;
		}

		var langHolder = LangHolder.langHolder(target);
		langHolder.setTranslationLanguage(translationLanguage);
		if (!silent) Messenger.messenger(target).sendMessage("command.translator.set", raw("translation", translatorKey));
		if (stack.getSender() != target) Messenger.messenger(stack.getSender()).sendMessage("command.translator.set.other", raw("translation", translatorKey), raw("player", target.displayName()));
	}

	@Suggestions("@translatorCommandSuggestionTranslators")
	public @NotNull Set<String> onTranslatorSuggestion(@NotNull CommandSourceStack stack) {
		return translators.keySet();
	}

}
