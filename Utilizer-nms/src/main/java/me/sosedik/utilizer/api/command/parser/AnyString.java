package me.sosedik.utilizer.api.command.parser;

import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.jspecify.annotations.NullMarked;

/**
 * Dirty hack that uses minecraft:nbt_path as a string parser
 * to allow more chars in input
 */
@NullMarked
public record AnyString(String string) {

	public static class AnyStringParser<C> implements ArgumentParser<C, AnyString> {

		@Override
		public ArgumentParseResult<AnyString> parse(CommandContext<C> commandContext, CommandInput commandInput) {
			return ArgumentParseResult.success(new AnyString(commandInput.readString()));
		}

	}

}
