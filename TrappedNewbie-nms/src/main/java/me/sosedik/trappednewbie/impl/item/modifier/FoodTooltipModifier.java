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
import net.kyori.adventure.text.format.ShadowColor;
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
	private static final int ICON_WIDTH = ResourceLib.requireFontData(trappedNewbieKey("hunger_icon")).width();

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
			if (foodValue != 0) {
				Component hungerDisplay = combineToOne(foodValue, SpacingUtil.getNegativePixel(), HUNGER_ICON_HALF, HUNGER_ICON);
				float saturation = data.saturation();
				if (saturation > 0) {
					int fulls = ((int) saturation) / 2;
					float leftover = saturation - ((int) saturation);
					List<Component> display = new ArrayList<>();
					if (leftover > 0) {
						if (leftover < 0.6F) display.add(SATURATION_ICON_1);
						else if (leftover < 1.2F) display.add(SATURATION_ICON_2);
						else display.add(SATURATION_ICON_3);
					}
					for (int i = 0; i < fulls; i++) display.add(SATURATION_ICON_4);
					int hungerWidth = ICON_WIDTH * (int) Math.ceil(foodValue / 2D) + 1;
					int satIcons = leftover > 0 ? fulls + 1 : fulls;
					int saturationWidth = ICON_WIDTH * satIcons + 1;
					Component saturationDisplay = combine(SpacingUtil.getNegativePixel(), display);
					hungerDisplay = Component.textOfChildren(hungerDisplay, SpacingUtil.getOffset(-hungerWidth + (hungerWidth - saturationWidth), saturationWidth, saturationDisplay));
					if (saturationWidth > hungerWidth)
						hungerDisplay = Component.textOfChildren(SpacingUtil.getSpacing(saturationWidth - hungerWidth - 1), hungerDisplay);
				}
				contextBox.addLore(hungerDisplay.shadowColor(ShadowColor.none()));
			}

			item.setData(DataComponentTypes.FOOD, FoodProperties.food().canAlwaysEat(data.canAlwaysEat()).build());
		} else if (item.hasData(DataComponentTypes.FOOD)) {
			FoodProperties data = item.getData(DataComponentTypes.FOOD);
			assert data != null;
			item.setData(DataComponentTypes.FOOD, FoodProperties.food().canAlwaysEat(data.canAlwaysEat()).build());
		}

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
