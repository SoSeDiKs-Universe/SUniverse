package me.sosedik.trappednewbie.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.trappednewbie.api.item.tinker.ArrowData;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CustomArrowModifier extends ItemModifier {

	private static final NamespacedKey ARROW_MODEL_KEY = NamespacedKey.minecraft("arrow");

	public CustomArrowModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		if (!Tag.ITEMS_ARROWS.isTagged(contextBox.getInitialType())) return ModificationResult.PASS;

		ArrowData.fromArrow(item, contextBox.getInitialType()).saveToCustomModelData(item, false);

		// Only update model for non-vanilla arrows
		if (item.getType() != contextBox.getInitialType() && item.isDataOverridden(DataComponentTypes.ITEM_MODEL))
			item.setData(DataComponentTypes.ITEM_MODEL, ARROW_MODEL_KEY);

		return ModificationResult.OK;
	}

}
