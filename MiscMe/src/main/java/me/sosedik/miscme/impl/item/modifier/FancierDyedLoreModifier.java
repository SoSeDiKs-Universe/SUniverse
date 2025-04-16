package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.api.message.Mini;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Makes dyed lore fancier, displays color's hex value
 */
@NullMarked
public class FancierDyedLoreModifier extends ItemModifier {

	private static final Component DYED_LORE = Component.translatable("item.dyed", NamedTextColor.GRAY, TextDecoration.ITALIC);

	public FancierDyedLoreModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContextType().hasVisibleLore()) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (!item.hasData(DataComponentTypes.DYED_COLOR)) return ModificationResult.PASS;

		TooltipDisplay tooltipDisplay = item.getData(DataComponentTypes.TOOLTIP_DISPLAY);
		if (tooltipDisplay == null) {
			item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.DYED_COLOR).build());
		} else if (!tooltipDisplay.hideTooltip() && !tooltipDisplay.hiddenComponents().contains(DataComponentTypes.DYED_COLOR)) {
			item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hiddenComponents(tooltipDisplay.hiddenComponents()).addHiddenComponents(DataComponentTypes.DYED_COLOR).build()); // TODO replace with toBuilder once available
		}

		DyedItemColor dyedItemColor = item.getData(DataComponentTypes.DYED_COLOR);
		assert dyedItemColor != null;
		contextBox.addLore(getDyedLore(dyedItemColor.color()));

		return ModificationResult.OK;
	}

	public static Component getDyedLore(TextColor color) {
		return Mini.combine(Component.space(), DYED_LORE, Component.text(color.asHexString(), color, TextDecoration.ITALIC));
	}

}
