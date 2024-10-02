package me.sosedik.utilizer.api.command.parser;

import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.jetbrains.annotations.NotNull;

/**
 * Dirty hack that uses minecraft:nbt_path as a string parser
 * to allow more chars in input
 */
public record AnyString(@NotNull String string) {

	public static class AnyStringParser<C> implements ArgumentParser<C, AnyString> {

		@Override
		public @NotNull ArgumentParseResult<@NotNull AnyString> parse(@NotNull CommandContext<@NotNull C> commandContext, @NotNull CommandInput commandInput) {
			return ArgumentParseResult.success(new AnyString(commandInput.readString()));
		}

	}

}
