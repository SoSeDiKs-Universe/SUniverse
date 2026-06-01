package me.sosedik.miscme.listener.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.context.ItemModifierContextType;
import me.sosedik.kiterino.modifier.item.context.packet.BaseItemContext;
import me.sosedik.miscme.MiscMe;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.Art;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Painting variants can be switched
 */
@NullMarked
public class PaintingSwitcher implements Listener {

	public static final ItemModifierContextType PAINTING_SWITCHER_CONTEXT = ItemModifierContextType.context(BaseItemContext.class).withLore().build();
	private static final Component RANDOM_PAINTING_ICON = ResourceLib.requireFontData(MiscMe.miscMeKey("random_painting")).icon().shadowColor(ShadowColor.none());

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;
		if (event.useItemInHand() == Event.Result.DENY) return;
		if (event.getAction() != Action.RIGHT_CLICK_AIR) return;

		Player player = event.getPlayer();
		if (!player.isSneaking()) return;

		ItemStack item = player.getInventory().getItemInMainHand();
		if (item.getType() != Material.PAINTING) return;

		event.setCancelled(true);
		showDialog(player, null);
	}

	private static void showDialog(Player player, @Nullable String filter) {
		if (filter != null) {
			filter = filter.trim();
			if (filter.isEmpty())
				filter = null;
		}

		Registry<Art> registry = RegistryAccess.registryAccess().getRegistry(RegistryKey.PAINTING_VARIANT);
		var messenger = Messenger.messenger(player);
		String finalFilterLower = filter == null ? null : filter.toLowerCase();
		List<Art> paintings = StreamSupport.stream(
				Spliterators.spliteratorUnknownSize(registry.iterator(), Spliterator.ORDERED),
				false
			)
			.filter(art -> {
				if (finalFilterLower == null) return true;

				NamespacedKey key = registry.getKeyOrThrow(art);
				String[] title = messenger.getRawMessageIfExists("painting." + key.namespace() + "." + key.value() + ".title");
				String[] author = messenger.getRawMessageIfExists("painting." + key.namespace() + "." + key.value() + ".author");
				return title != null && String.join(" ", title).toLowerCase().contains(finalFilterLower)
					|| author != null && String.join(" ", author).toLowerCase().contains(finalFilterLower);
			})
			.sorted(Comparator
				.comparingInt(Art::getBlockWidth)
				.thenComparingInt(Art::getBlockHeight)
				.thenComparing(art -> {
					NamespacedKey key = registry.getKeyOrThrow(art);
					String[] rawMessage = messenger.getRawMessageIfExists("painting." + key.namespace() + "." + key.value());
					return rawMessage == null ? key.value() : String.join(" ", rawMessage);
				})
			)
			.toList();

		Consumer<@Nullable Art> action = newArt -> {
			ItemStack handItem = player.getInventory().getItemInMainHand();
			if (handItem.getType() != Material.PAINTING) return;

			if (newArt == null)
				handItem.resetData(DataComponentTypes.PAINTING_VARIANT);
			else
				handItem.setData(DataComponentTypes.PAINTING_VARIANT, newArt);
		};

		List<ActionButton> actions = paintings.stream().map(art -> {
			NamespacedKey key = registry.getKeyOrThrow(art);
			Component icon = ResourceLib.requireFontData(key).icon().shadowColor(ShadowColor.none());
			ActionButton.Builder builder = ActionButton.builder(icon);

			var paintingItem = ItemStack.of(Material.PAINTING);
			paintingItem.setData(DataComponentTypes.PAINTING_VARIANT, art);
			var contextBox = new ItemContextBox(player, player.locale(), new BaseItemContext(PAINTING_SWITCHER_CONTEXT, null), paintingItem);
			ItemStack parsedPaintingItem = ItemModifier.modifyItem(contextBox);
			if (parsedPaintingItem != null && parsedPaintingItem.hasLore())
				builder.tooltip(Mini.combine(Component.newline(), parsedPaintingItem.lore()));

			builder.action(DialogAction.customClick((_, _) -> action.accept(art), ClickCallback.Options.builder().build()));

			return builder.width(22).build();
		}).collect(Collectors.toCollection(ArrayList::new));

		ActionButton.Builder randomPaintingBuilder = ActionButton.builder(RANDOM_PAINTING_ICON);
		randomPaintingBuilder.tooltip(Component.translatable("painting.random", NamedTextColor.YELLOW));
		randomPaintingBuilder.action(DialogAction.customClick((_, _) -> action.accept(null), ClickCallback.Options.builder().build()));
		actions.addFirst(randomPaintingBuilder.width(22).build());

		var filterAction = ActionButton
			.builder(messenger.getMessage("paintings.picker.filter.apply"))
			.action(DialogAction.customClick((responseView, _) -> showDialog(player, responseView.getText("filter")), ClickCallback.Options.builder().build()))
			.build();

		String finalFilter = filter;
		Dialog dialog = Dialog.create(builder -> builder.empty()
			.base(
				DialogBase
					.builder(messenger.getMessage("paintings.picker.title"))
					.canCloseWithEscape(false) // Esc triggers filter action
					.inputs(List.of(
						DialogInput
							.text("filter", messenger.getMessage("paintings.picker.filter.title"))
							.initial(finalFilter == null ? "" : finalFilter)
							.build()
					))
					.build()
			)
			.type(DialogType.multiAction(actions).exitAction(filterAction).columns(15).build())
		);
		player.showDialog(dialog);
	}

}
