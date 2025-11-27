package me.sosedik.miscme.impl.item.modifier;

import io.papermc.paper.datacomponent.DataComponentTypes;
import me.sosedik.kiterino.modifier.item.ItemContextBox;
import me.sosedik.kiterino.modifier.item.ItemModifier;
import me.sosedik.kiterino.modifier.item.ModificationResult;
import me.sosedik.resourcelib.ResourceLib;
import me.sosedik.utilizer.util.ChatUtil;
import me.sosedik.utilizer.util.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import static me.sosedik.miscme.MiscMe.miscMeKey;
import static me.sosedik.utilizer.api.message.Mini.combined;

/**
 * Shows item's armor value in lore
 */
@NullMarked
public class ArmorTooltipModifier extends ItemModifier {

	public static final Component ARMOR_ICON = ResourceLib.requireFontData(miscMeKey("armor")).icon();
	public static final Component ARMOR_TOUGHNESS_ICON = ResourceLib.requireFontData(miscMeKey("armor_toughness")).icon();
	public static final Component KNOCKBACK_RESISTANCE_ICON = ResourceLib.requireFontData(miscMeKey("knockback_resistance")).icon();

	public ArmorTooltipModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContext().getContextType().hasVisibleLore()) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (item.isDataOverridden(DataComponentTypes.ATTRIBUTE_MODIFIERS) && !item.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) return ModificationResult.PASS;

		double armor = ItemUtil.getAttributeValue(item, Attribute.ARMOR, player);
		if (armor <= 0) return ModificationResult.PASS;

		double toughness = ItemUtil.getAttributeValue(item, Attribute.ARMOR_TOUGHNESS, player);
		double knockbackResistance = ItemUtil.getAttributeValue(item, Attribute.KNOCKBACK_RESISTANCE, player);

		Component armorText = combined(ARMOR_ICON, Component.space(), Component.text(ChatUtil.formatDouble(armor)));

		Component text;
		if (toughness > 0 || knockbackResistance > 0) {
			text = Component.empty().append(armorText);
			if (toughness > 0) text = text.append(Component.space(), combined(ARMOR_TOUGHNESS_ICON, Component.space(), Component.text(ChatUtil.formatDouble(toughness))));
			if (knockbackResistance > 0) text = text.append(Component.space(), combined(KNOCKBACK_RESISTANCE_ICON, Component.space(), Component.text(ChatUtil.formatDouble(knockbackResistance * 100) + "%")));
		} else {
			text = armorText;
		}

		contextBox.addLore(text.colorIfAbsent(NamedTextColor.GRAY).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

		contextBox.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);

		return ModificationResult.OK;
	}

}
