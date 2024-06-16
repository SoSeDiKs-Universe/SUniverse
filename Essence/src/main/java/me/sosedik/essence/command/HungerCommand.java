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
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Changing player's hunger
 */
@Permission("essence.command.hunger")
public class HungerCommand {

	@Command("hunger <amount> [player]")
	public void onCommand(
		@NotNull CommandSourceStack stack,
		@Argument(value = "amount", suggestions = "@hungerCommandSuggestionAmount") @Range(min = "0", max = "20") int amount,
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

		Essence.scheduler().sync(() -> target.setFoodLevel(amount));

		TagResolver amountTag = raw("amount", amount);
		if (!silent) Messenger.messenger(target).sendMessage("command.hunger", amountTag);
		if (stack.getSender() != target)
			Messenger.messenger(stack.getSender()).sendMessage("command.hunger.other", amountTag, raw("player", target.displayName()));
	}

	@Suggestions("@hungerCommandSuggestionAmount")
	public @NotNull Set<String> onAmountSuggestion(@NotNull CommandSourceStack stack) {
		return stack.getExecutor() instanceof Player player ? Set.of(String.valueOf(player.getFoodLevel())) : Set.of();
	}

}
