package me.sosedik.trappednewbie.api.command.parser;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public final class PlayerWorldParser<C> implements ArgumentParser<C, World>, SuggestionProvider<C> {

	public static <C> @NotNull ParserDescriptor<C, World> playerWorldParser() {
		return ParserDescriptor.of(new PlayerWorldParser<>(), World.class);
	}

	public static <C> CommandComponent.@NotNull Builder<C, World> playerWorldComponent() {
		return CommandComponent.<C, World>builder().parser(playerWorldParser());
	}

	@Override
	public @NotNull ArgumentParseResult<@NotNull World> parse(
		final @NotNull CommandContext<@NotNull C> commandContext,
		final @NotNull CommandInput commandInput
	) {
		String input = commandInput.readString();

		// Player's own world
		if ("@s".equals(input)) {
			Player target = null;
			if (commandContext.sender() instanceof CommandSourceStack stack) {
				if (stack.getExecutor() instanceof Player player) {
					target = player;
				} else if (stack.getSender() instanceof Player player) {
					target = player;
				}
			}
			if (target == null) {
				return ArgumentParseResult.failure(new WorldParser.WorldParseException(input, commandContext));
			}
			input = "worlds/" + target.getUniqueId();
		}

		// @ + Player nicknames to world names
		if (input.charAt(0) == '@') {
			String worldPrefix;
			String playerName;
			if (input.contains("#")) {
				String[] split = input.split("#");
				worldPrefix = "worlds-resources/" + split[1] + "/";
				playerName = split[0].substring(1);
			} else {
				worldPrefix = "worlds/";
				playerName = input.substring(1);
			}
			Player player = Bukkit.getPlayerExact(playerName);
			if (player == null)
				return ArgumentParseResult.failure(new WorldParser.WorldParseException(input, commandContext));

			input = worldPrefix + player.getUniqueId();
		}

		// Try getting by name first, fallback to namespaced key
		World world = Bukkit.getWorld(input);
		if (world == null) {
			var key = NamespacedKey.fromString(input);
			if (key != null) {
				world = Bukkit.getWorld(key);
			}
		}

		if (world == null) {
			return ArgumentParseResult.failure(new WorldParser.WorldParseException(input, commandContext));
		}

		return ArgumentParseResult.success(world);
	}

	@Override
	public @NotNull CompletableFuture<? extends @NotNull Iterable<? extends @NotNull Suggestion>> suggestionsFuture(
		final @NotNull CommandContext<C> commandContext,
		final @NotNull CommandInput input
	) {
		List<Suggestion> completions = new ArrayList<>();

		Player target = null;
		if (commandContext.sender() instanceof CommandSourceStack stack) {
			if (stack.getExecutor() instanceof Player player) {
				target = player;
			} else if (stack.getSender() instanceof Player player) {
				target = player;
			}
		}

		if ("@s".startsWith(input.readString())) {
			completions.add(Suggestion.suggestion("@s"));
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player == target) continue;

			completions.add(Suggestion.suggestion("@" + player.getName()));
			for (World.Environment environment : World.Environment.values()) {
				if (environment == World.Environment.CUSTOM) continue;
				completions.add(Suggestion.suggestion("@" + player.getName() + "#" + environment.name().toLowerCase(Locale.ENGLISH)));
			}
		}

		for (World world : Bukkit.getWorlds()) {
			String name = world.getName();
			if (name.startsWith("worlds/")) continue;
			if (target != null && target.getWorld() == world) continue;

			completions.add(Suggestion.suggestion(name));

			NamespacedKey key = world.getKey();
			if (input.hasRemainingInput() && key.getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
				completions.add(Suggestion.suggestion(key.getKey()));
			}
			completions.add(Suggestion.suggestion(key.getNamespace() + ':' + key.getKey()));
		}

		return CompletableFuture.completedFuture(completions);
	}

}
