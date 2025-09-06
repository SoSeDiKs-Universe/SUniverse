package me.sosedik.trappednewbie.impl.item.modifier;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import net.kyori.adventure.text.Component;
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
public class ThirstTooltipModifier extends ItemModifier {

	private static final Component THIRST_ICON = ResourceLib.requireFontData(trappedNewbieKey("thirst_full_icon")).icon();
	private static final Component THIRST_ICON_HALF = ResourceLib.requireFontData(trappedNewbieKey("thirst_half_icon")).icon();
	private static final Component SATURATION_ICON_FULL = ResourceLib.requireFontData(trappedNewbieKey("thirst_sat_full_icon")).icon();
	private static final Component SATURATION_ICON_HALF = ResourceLib.requireFontData(trappedNewbieKey("thirst_sat_half_icon")).icon();
	private static final Component COOLED_ICON = ResourceLib.requireFontData(trappedNewbieKey("cooled")).icon();
	private static final int ICON_WIDTH = ResourceLib.requireFontData(trappedNewbieKey("thirst_full_icon")).width();

	public ThirstTooltipModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();

		var thirstData = ThirstData.of(item, contextBox.getInitialType());
		int thirst = thirstData.thirst();
		if (thirst == 0) return ModificationResult.PASS;

		Component thirstDisplay = FoodTooltipModifier.combineToOne(thirst, SpacingUtil.getSpacing(1), THIRST_ICON_HALF, THIRST_ICON);

		float saturation = thirstData.saturation();
		if (saturation > 0) {
			int fulls = ((int) saturation) / 2;
			float leftover = saturation - ((int) saturation);
			List<Component> display = new ArrayList<>();
			if (leftover > 0) display.add(SATURATION_ICON_HALF);
			for (int i = 0; i < fulls; i++) display.add(SATURATION_ICON_FULL);
			int thirstWidth = ICON_WIDTH * thirst;
			int satIcons = leftover > 0 ? fulls + 1 : fulls;
			int saturationWidth = ICON_WIDTH * satIcons + 1;
			Component saturationDisplay = combine(SpacingUtil.getNegativePixel(), display);
			thirstDisplay = Component.textOfChildren(thirstDisplay, SpacingUtil.getOffset(-thirstWidth + (thirstWidth - saturationWidth), saturationWidth, saturationDisplay));
		}

		if (thirstData.cooled())
			thirstDisplay = combined(thirstDisplay, Component.space(), COOLED_ICON);

		contextBox.addLore(thirstDisplay.shadowColor(ShadowColor.none()));

		return ModificationResult.OK;
	}

}
