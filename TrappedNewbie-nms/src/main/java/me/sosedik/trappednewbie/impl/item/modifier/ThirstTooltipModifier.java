package me.sosedik.trappednewbie.impl.item.modifier;

import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.util.SpacingUtil;
import me.sosedik.trappednewbie.impl.thirst.ThirstData;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.trappednewbie.TrappedNewbie.trappedNewbieKey;
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

	public ThirstTooltipModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		ItemStack item = contextBox.getItem();

		var thirstData = ThirstData.of(item, contextBox.getInitialType());
		int thirst = thirstData.thirst();
		if (thirst == 0) return ModificationResult.PASS;

		Component thirstLore = FoodTooltipModifier.combineToOne(thirst, SpacingUtil.getSpacing(1), THIRST_ICON_HALF, THIRST_ICON);
		if (thirstData.cooled()) thirstLore = combined(thirstLore, Component.space(), COOLED_ICON);
		contextBox.addLore(thirstLore);

		return ModificationResult.OK;
	}

}
