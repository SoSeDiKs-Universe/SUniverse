package me.sosedik.trappednewbie.api.command.parser;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.trappednewbie.TrappedNewbie;
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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@NullMarked
public final class PlayerWorldParser<C> implements ArgumentParser<C, World>, SuggestionProvider<C> {

	public static <C> ParserDescriptor<C, World> playerWorldParser() {
		return ParserDescriptor.of(new PlayerWorldParser<>(), World.class);
	}

	public static <C> CommandComponent.Builder<C, World> playerWorldComponent() {
		return CommandComponent.<C, World>builder().parser(playerWorldParser());
	}

	@Override
	public ArgumentParseResult<World> parse(
		final CommandContext<C> commandContext,
		final CommandInput commandInput
	) {
		String input = commandInput.readString();

		// Player's own world
		if ("@s".equals(input)) {
			Player target = resolveTarget(commandContext);
			if (target == null) {
				return ArgumentParseResult.failure(new WorldParser.WorldParseException(input, commandContext));
			}
			input = "worlds-personal/" + target.getUniqueId();
		}

		// @ + Player nicknames to world names
		if (input.charAt(0) == '@') {
			String worldPrefix;
			String playerName;
			if (input.contains("#")) {
				String[] split = input.split("#");
				worldPrefix = "worlds-resources/" + split[1] + "/";
				if (input.startsWith("@s#")) {
					Player target = resolveTarget(commandContext);
					if (target == null) {
						return ArgumentParseResult.failure(new WorldParser.WorldParseException(input, commandContext));
					}
					playerName = target.getName();
				} else {
					playerName = split[0].substring(1);
				}
			} else {
				worldPrefix = "worlds-personal/";
				if (input.startsWith("@s#")) {
					Player target = resolveTarget(commandContext);
					if (target == null) {
						return ArgumentParseResult.failure(new WorldParser.WorldParseException(input, commandContext));
					}
					playerName = target.getName();
				} else {
					playerName = input.substring(1);
				}
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

	private @Nullable Player resolveTarget(CommandContext<C> commandContext) {
		if (commandContext.sender() instanceof CommandSourceStack stack) {
			if (stack.getExecutor() instanceof Player player) {
				return player;
			} else if (stack.getSender() instanceof Player player) {
				return player;
			}
		}
		return null;
	}

	@Override
	public CompletableFuture<? extends Iterable<? extends Suggestion>> suggestionsFuture(
		final CommandContext<C> commandContext,
		final CommandInput input
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

		if ("@".startsWith(input.readString())) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				String playerPrefix = player == target ? "@s" : "@" + player.getName();
				completions.add(Suggestion.suggestion(playerPrefix));
				for (World.Environment environment : World.Environment.values()) {
					if (environment == World.Environment.CUSTOM) continue;
					completions.add(Suggestion.suggestion(playerPrefix + "#" + environment.name().toLowerCase(Locale.US)));
				}
			}
		}

		for (World world : Bukkit.getWorlds()) {
			NamespacedKey key = world.getKey();
			if (TrappedNewbie.NAMESPACE.equals(key.getNamespace())) {
				if (key.getKey().startsWith("worlds-personal/")) continue;
				if (key.getKey().startsWith("worlds-resources/")) continue;
			}

			if (target != null && target.getWorld() == world) continue;

			completions.add(Suggestion.suggestion(world.getName()));

			if (input.hasRemainingInput() && key.getNamespace().equals(NamespacedKey.MINECRAFT_NAMESPACE)) {
				completions.add(Suggestion.suggestion(key.getKey()));
			}
			completions.add(Suggestion.suggestion(key.getNamespace() + ':' + key.getKey()));
		}

		return CompletableFuture.completedFuture(completions);
	}

}
