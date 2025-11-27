package me.sosedik.utilizer.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.dataset.UtilizerTags;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class HiddenTooltipsModifier extends ItemModifier {

	public HiddenTooltipsModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!UtilizerTags.NO_TOOLTIP_ITEMS.isTagged(contextBox.getInitialType())) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		item.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().hideTooltip(true).build());
		item.setData(DataComponentTypes.ITEM_NAME, Component.empty());

		return ModificationResult.OK;
	}

}
