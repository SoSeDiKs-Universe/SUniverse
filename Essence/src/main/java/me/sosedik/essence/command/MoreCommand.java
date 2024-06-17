package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import me.sosedik.utilizer.api.message.Messenger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
 * Quickly changing item's amount
 */
@Permission("essence.command.more")
public class MoreCommand {

	@Command("more [amount]")
	public void onCommand(
		@NotNull CommandSourceStack stack,
		@Nullable @Argument(value = "amount") @Range(min = "0") Integer amount, // , suggestions = "@moreCommandSuggestionAmount"
		@Flag(value = "offHand") boolean offHand,
		@Flag(value = "feedback") boolean feedback
	) {
		if (!(stack.getExecutor() instanceof Player target)) return;

		Essence.scheduler().sync(() -> {
			ItemStack item = offHand ? target.getInventory().getItemInOffHand() : target.getInventory().getItemInMainHand();
			int finAmount = amount == null ? item.getMaxStackSize() : amount;
			item.setAmount(finAmount);

			if (feedback)
				Messenger.messenger(stack.getSender()).sendMessage("command.more", raw("amount", finAmount), raw("item", item.displayName()));
		});
	}

	@Suggestions("@moreCommandSuggestionAmount")
	public @NotNull Set<String> onAmountSuggestion() {
		return Set.of("64");
	}

}
