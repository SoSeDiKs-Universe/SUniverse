package me.sosedik.utilizer.api.command;

import org.incendo.cloud.execution.preprocessor.CommandPreprocessingContext;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProcessor;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * Command suggestion processor filters suggestions based on the remaining unconsumed input in the
 * queue. Case-insensitive.
 *
 * @param <C> command sender type
 */
@NullMarked
public class FilteringCommandCaselessSuggestionProcessor<C> implements SuggestionProcessor<C> {

	@Override
	public Stream<Suggestion> process(CommandPreprocessingContext<C> context, Stream<Suggestion> suggestions) {
		String input;
		if (context.commandInput().isEmpty(true)) {
			input = "";
		} else {
			input = context.commandInput().skipWhitespace().remainingInput().toLowerCase(Locale.ROOT);
		}

		return suggestions
				.filter(suggestion -> suggestion.suggestion().startsWith(input))
				.filter(suggestion -> !suggestion.suggestion().startsWith("--I_"));  // ignore internal flags
	}

}
