package me.sosedik.utilizer.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.utilizer.dataset.UtilizerTags;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HiddenTooltipsModifier extends ItemModifier {

	public HiddenTooltipsModifier(@NotNull NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public @NotNull ModificationResult modify(@NotNull ItemContextBox contextBox) {
		if (!UtilizerTags.NO_TOOLTIP_ITEMS.isTagged(contextBox.getInitialType())) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		item.setData(DataComponentTypes.HIDE_TOOLTIP);
		item.setData(DataComponentTypes.ITEM_NAME, Component.empty());

		return ModificationResult.OK;
	}

}
