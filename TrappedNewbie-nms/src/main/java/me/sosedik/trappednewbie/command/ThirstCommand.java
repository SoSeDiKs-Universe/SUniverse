package me.sosedik.trappednewbie.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.trappednewbie.TrappedNewbie;
import me.sosedik.trappednewbie.impl.thirst.ThirstyPlayer;
import me.sosedik.utilizer.api.message.Messenger;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Changing player's thirst
 */
@NullMarked
@Permission("trapped_newbie.command.thirst")
public class ThirstCommand {

	@Command("thirst <amount> [player]")
	public void onCommand(
		CommandSourceStack stack,
		@Argument(value = "amount", suggestions = "@thirstCommandSuggestionAmount") @Range(min = "0", max = "20") int amount,
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

		TrappedNewbie.scheduler().sync(() -> ThirstyPlayer.of(target).setThirst(amount));

		TagResolver amountTag = raw("amount", amount);
		if (!silent) Messenger.messenger(target).sendMessage("command.thirst", amountTag);
		if (stack.getSender() != target)
			Messenger.messenger(stack.getSender()).sendMessage("command.thirst.other", amountTag, raw("player", target.displayName()));
	}

	@Suggestions("@thirstCommandSuggestionAmount")
	public Set<String> onAmountSuggestion(CommandSourceStack stack) {
		return stack.getExecutor() instanceof Player player ? Set.of(String.valueOf(ThirstyPlayer.of(player).getThirst())) : Set.of();
	}

}
