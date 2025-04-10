package me.sosedik.trappednewbie.api.command.parser;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.listener.world.PerPlayerWorlds;
import me.sosedik.utilizer.util.MiscUtil;
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
public final class PlayerWorldParser<C> implements ArgumentParser.FutureArgumentParser<C, World>, SuggestionProvider<C> {

	private static final String ENV_SEPARATOR = "#";

	public static <C> ParserDescriptor<C, World> playerWorldParser() {
		return ParserDescriptor.of(new PlayerWorldParser<>(), World.class);
	}

	public static <C> CommandComponent.Builder<C, World> playerWorldComponent() {
		return CommandComponent.<C, World>builder().parser(playerWorldParser());
	}

	@Override
	public CompletableFuture<ArgumentParseResult<World>> parseFuture(CommandContext<C> commandContext, CommandInput commandInput) {
		String input = commandInput.readString();

		// Player's own world
		if ("@s".equals(input)) {
			Player target = resolveTarget(commandContext);
			if (target == null)
				return ArgumentParseResult.failureFuture(new WorldParser.WorldParseException(input, commandContext));

			CompletableFuture<ArgumentParseResult<World>> future = new CompletableFuture<>();
			TrappedNewbie.scheduler().sync(() -> future.complete(ArgumentParseResult.success(PerPlayerWorlds.getPersonalWorld(target.getUniqueId()))));
			return future;
		}

		// @ + Player nicknames to world names
		if (input.charAt(0) == '@') {
			String playerName;
			World.Environment environment = null;
			if (input.contains(ENV_SEPARATOR)) {
				String[] split = input.split(ENV_SEPARATOR);
				environment = MiscUtil.parseOrNull(split[1], World.Environment.class);
				if (environment == null)
					return ArgumentParseResult.failureFuture(new WorldParser.WorldParseException(input, commandContext));

				if (input.startsWith("@s" + ENV_SEPARATOR)) {
					Player target = resolveTarget(commandContext);
					if (target == null)
						return ArgumentParseResult.failureFuture(new WorldParser.WorldParseException(input, commandContext));

					CompletableFuture<ArgumentParseResult<World>> future = new CompletableFuture<>();
					World.Environment finalEnvironment = environment;
					TrappedNewbie.scheduler().sync(() -> future.complete(ArgumentParseResult.success(PerPlayerWorlds.getResourceWorld(target.getUniqueId(), finalEnvironment))));
					return future;
				} else {
					playerName = split[0].substring(1);
				}
			} else {
				playerName = input.substring(1);
			}

			Player player = Bukkit.getPlayerExact(playerName);
			if (player == null)
				return ArgumentParseResult.failureFuture(new WorldParser.WorldParseException(input, commandContext));

			CompletableFuture<ArgumentParseResult<World>> future = new CompletableFuture<>();
			World.Environment finalEnvironment = environment;
			TrappedNewbie.scheduler().sync(() -> {
				World world;
				if (finalEnvironment == null) {
					world = PerPlayerWorlds.getPersonalWorld(player.getUniqueId());
				} else {
					world = PerPlayerWorlds.getResourceWorld(player.getUniqueId(), finalEnvironment);
				}
				future.complete(ArgumentParseResult.success(world));
			});
			return future;
		}

		// Try getting by name first, fallback to namespaced key
		World world = Bukkit.getWorld(input);
		if (world == null) {
			var key = NamespacedKey.fromString(input);
			if (key != null) {
				world = Bukkit.getWorld(key);
			}
		}

		if (world == null)
			return ArgumentParseResult.failureFuture(new WorldParser.WorldParseException(input, commandContext));

		return ArgumentParseResult.successFuture(world);
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
					completions.add(Suggestion.suggestion(playerPrefix + ENV_SEPARATOR + environment.name().toLowerCase(Locale.US)));
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
