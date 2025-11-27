package me.sosedik.delightfulfarming.impl.item.modifier;

import me.sosedik.delightfulfarming.DelightfulFarming;
import me.sosedik.delightfulfarming.feature.sugar.MealTime;
import me.sosedik.delightfulfarming.feature.sugar.SugarEater;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.kiterino.modifier.item.context.ItemModifierContext;
import me.sosedik.miscme.impl.item.modifier.ClockModifier;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.resourcelib.api.font.FontData;
import me.sosedik.utilizer.api.language.LangOptionsStorage;
import me.sosedik.utilizer.api.message.Messenger;
import me.sosedik.utilizer.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.EnumMap;
import java.util.Map;

import static me.sosedik.utilizer.api.message.Mini.combined;
import static me.sosedik.utilizer.api.message.Mini.raw;

/**
 * Fakes berries to prevent placing them onto blocks
 */
@NullMarked
public class ClockMealModifier extends ItemModifier {

	private static final Map<MealTime, Component> MEAL_ICONS = new EnumMap<>(MealTime.class);

	static {
		for (MealTime mealTime : MealTime.values()) {
			FontData fontData = ResourceLib.storage().getFontData(DelightfulFarming.delightfulFarmingKey(mealTime.getId()));
			assert fontData != null;
			MEAL_ICONS.put(mealTime, fontData.icon());
		}
	}

	public ClockMealModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (contextBox.getInitialType() != Material.CLOCK) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		var sugarEater = SugarEater.of(player);
		MealTime mealTime = sugarEater.getMealTime();
		Component icon = MEAL_ICONS.get(mealTime);

		var messenger = Messenger.messenger(LangOptionsStorage.getByLocale(contextBox.getLocale()));
		Component message = messenger.getMessage("meal.time",
			raw("meal", messenger.getMessage("meal." + mealTime.getId() + ".name")),
			raw("time", ClockModifier.formatTime(mealTime.getTimeTo(), messenger, player))
		).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE).colorIfAbsent(NamedTextColor.GRAY);

		contextBox.addLore(combined(icon, Component.space(), message));

		return ModificationResult.OK;
	}

	@Override
	public boolean skipContext(ItemModifierContext context) {
		return ItemUtil.shouldFreeze(context);
	}

}
