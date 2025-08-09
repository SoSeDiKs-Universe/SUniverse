package me.sosedik.trappednewbie.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;
import static me.sosedik.utilizer.api.message.Mini.combine;
import static me.sosedik.utilizer.api.message.Mini.combined;

/**
 * Shows food values in lore
 */
@NullMarked
public class FoodTooltipModifier extends ItemModifier {

	private static final Component HUNGER_ICON = ResourceLib.requireFontData(trappedNewbieKey("hunger_icon")).icon();
	private static final Component HUNGER_ICON_HALF = ResourceLib.requireFontData(trappedNewbieKey("half_hunger_icon")).icon();
	private static final Component SATURATION_ICON_1 = ResourceLib.requireFontData(trappedNewbieKey("sat_1_icon")).icon();
	private static final Component SATURATION_ICON_2 = ResourceLib.requireFontData(trappedNewbieKey("sat_2_icon")).icon();
	private static final Component SATURATION_ICON_3 = ResourceLib.requireFontData(trappedNewbieKey("sat_3_icon")).icon();
	private static final Component SATURATION_ICON_4 = ResourceLib.requireFontData(trappedNewbieKey("sat_4_icon")).icon();

	public FoodTooltipModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();
		if (!item.hasData(DataComponentTypes.FOOD)) return ModificationResult.PASS;

		ItemStack ogItem = item.withType(contextBox.getInitialType());
		if (ogItem.hasData(DataComponentTypes.FOOD)) {
			FoodProperties data = ogItem.getData(DataComponentTypes.FOOD);
			assert data != null;
			int foodValue = data.nutrition();
			if (foodValue != 0)
				contextBox.addLore(combineToOne(foodValue, SpacingUtil.getNegativePixel(), HUNGER_ICON_HALF, HUNGER_ICON));
		}

		item.unsetData(DataComponentTypes.FOOD);

		return ModificationResult.OK;
	}

	public static Component combineToOne(int value, Component spacing, Component half, Component full) {
		if (value < 0) return combined(Component.text('-', NamedTextColor.RED), combineToOne(-value, spacing, half, full));
		if (value == 1) return half;
		if (value == 2) return full;

		List<Component> display = new ArrayList<>();
		if (value % 2 == 1) {
			display.add(half);
			value--;
		}

		for (int i = 0; i < Math.max(0, value / 2); i++)
			display.add(full);

		return combine(spacing, display);
	}

}
