package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.CommandUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Giving items
 */
@NullMarked
@Permission("essence.command.item")
public class ItemCommand {

	private static final List<String> ITEM_SUGGESTIONS = Registry.ITEM.keyStream()
		.map(NamespacedKey::asString)
		.toList();

	@Command("item <id> [amount] [player]")
	private void onCommand(
		CommandSourceStack stack,
		@Argument(value = "id", suggestions = "@itemCommandSuggestionItemId") NamespacedKey id,
		@Argument(value = "amount", suggestions = "@itemCommandSuggestionAmount") @Default(value = "1") @Range(min = "1", max = "64") Integer amount,
		@Nullable @Argument(value = "player") Player player,
		@Flag(value = "silent") boolean silent
	) {
		Player target = CommandUtils.getTargetPlayer(stack, player);
		if (target == null) return;

		Material type = Material.matchMaterial(id.asString());
		if (type == null) {
			Messenger.messenger(stack.getSender()).sendMessage("command.item.invalid", raw("item", id.asString()));
			return;
		}

		var item = ItemStack.of(type, amount);
		Essence.scheduler().sync(() -> {
			target.getInventory().addItem(item);
			if (!silent) {
				Messenger.messenger(target).sendMessage("command.item.received",
					raw("item", item.effectiveName().hoverEvent(item)),
					raw("amount", amount));
			}
			if (CommandUtils.isTargetingOther(stack, target)) {
				Messenger.messenger(stack.getSender()).sendMessage("command.item.gave",
					raw("item", item.effectiveName().hoverEvent(item)),
					raw("amount", amount),
					raw("player", target.displayName()));
			}
		});
	}

	@Suggestions("@itemCommandSuggestionItemId")
	public List<String> getItemSuggestions(CommandSourceStack stack, String input) {
		return ITEM_SUGGESTIONS;
	}

	@Suggestions("@itemCommandSuggestionAmount")
	public List<String> getAmountSuggestion(CommandSourceStack stack, String input) {
		Material type = Material.matchMaterial(input);
		return List.of(type == null ? "64" : String.valueOf(type.getMaxStackSize()));
	}

}
