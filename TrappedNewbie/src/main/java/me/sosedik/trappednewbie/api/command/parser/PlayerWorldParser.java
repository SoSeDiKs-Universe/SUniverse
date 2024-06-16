package me.sosedik.trappednewbie.api.command.parser;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.bukkit.parser.WorldParser;
import org.incendo.cloud.component.CommandComponent;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.parser.ArgumentParseResult;
import org.incendo.cloud.parser.ArgumentParser;
import org.incendo.cloud.parser.ParserDescriptor;
import org.incendo.cloud.suggestion.Suggestion;
import org.incendo.cloud.suggestion.SuggestionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class PlayerWorldParser<C> implements ArgumentParser<C, World>, SuggestionProvider<C> {

	public static <C> @NonNull ParserDescriptor<C, World> playerWorldParser() {
		return ParserDescriptor.of(new PlayerWorldParser<>(), World.class);
	}

	public static <C> CommandComponent.@NonNull Builder<C, World> playerWorldComponent() {
		return CommandComponent.<C, World>builder().parser(playerWorldParser());
	}

	@Override
	public @NonNull ArgumentParseResult<@NonNull World> parse(
		final @NonNull CommandContext<@NonNull C> commandContext,
		final @NonNull CommandInput commandInput
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
			Player player = Bukkit.getPlayerExact(input.substring(1));
			if (player == null)
				return ArgumentParseResult.failure(new WorldParser.WorldParseException(input, commandContext));

			input = "worlds/" + player.getUniqueId();
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
	public @NonNull CompletableFuture<? extends @NonNull Iterable<? extends @NonNull Suggestion>> suggestionsFuture(
		final @NonNull CommandContext<C> commandContext,
		final @NonNull CommandInput input
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
