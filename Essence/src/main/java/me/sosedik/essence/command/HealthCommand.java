package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Changing player's health
 */
@Permission("essence.command.health")
public class HealthCommand {

	@Command("health <amount> [player]")
	public void onCommand(
		@NotNull CommandSourceStack stack,
		@Argument(value = "amount", suggestions = "@healthCommandSuggestionAmount") @Range(min = "0") double amount,
		@Nullable @Argument(value = "player") Player player,
		@Flag(value = "silent") boolean silent
	) {
		Player target;
		if (player == null) {
			if (!(stack.getExecutor() instanceof Player executor)) return;
			target = executor;
		} else {
			target = player;
		}

		Essence.scheduler().sync(() -> target.setHealth(amount));

		TagResolver amountTag = raw("amount", amount);
		if (!silent) Messenger.messenger(target).sendMessage("command.health", amountTag);
		if (stack.getSender() != target)
			Messenger.messenger(stack.getSender()).sendMessage("command.health.other", amountTag, raw("player", target.displayName()));
	}

	@Suggestions("@healthCommandSuggestionAmount")
	public @NotNull Set<String> onAmountSuggestion(@NotNull CommandSourceStack stack) {
		return stack.getExecutor() instanceof Player player ? Set.of(String.valueOf((int) Math.ceil(player.getHealth()))) : Set.of();
	}

}
