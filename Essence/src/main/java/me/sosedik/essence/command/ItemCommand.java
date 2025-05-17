package me.sosedik.essence.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.sosedik.essence.Essence;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Default;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

/**
 * Giving items
 */
@NullMarked
@Permission("essence.command.item")
public class ItemCommand {

	private final List<String> suggestions;

	public ItemCommand() {
		this.suggestions = Stream.of(Material.values())
			.filter(Material::isItem)
			.filter(type -> !type.isLegacy())
			.map(Material::getKey)
			.map(NamespacedKey::asString)
			.toList();
	}

	@Command("item <id> [amount] [player]")
	private void onCommand(
		CommandSourceStack stack,
		@Argument(value = "id", suggestions = "@itemCommandSuggestionItemId") NamespacedKey id,
		@Argument(value = "amount", suggestions = "@itemCommandSuggestionAmount") @Default(value = "1") @Range(min = "1", max = "64") Integer amount,
		@Nullable @Argument(value = "player") Player player
	) {
		Player target;
		if (player == null) {
			if (!(stack.getExecutor() instanceof Player executor)) return;
			target = executor;
		} else {
			target = player;
		}

		Material type = Material.matchMaterial(id.asString());
		if (type == null) return;

		Essence.scheduler().sync(() -> target.getInventory().addItem(ItemStack.of(type, amount)));
	}

	@Suggestions("@itemCommandSuggestionItemId")
	public List<String> getItemSuggestions(CommandSourceStack stack, String input) {
		return this.suggestions;
	}

	@Suggestions("@itemCommandSuggestionAmount")
	public List<String> getAmountSuggestion(CommandSourceStack stack, String input) {
		Material type = Material.matchMaterial(input);
		return List.of(type == null ? "64" : String.valueOf(type.getMaxStackSize()));
	}

}
