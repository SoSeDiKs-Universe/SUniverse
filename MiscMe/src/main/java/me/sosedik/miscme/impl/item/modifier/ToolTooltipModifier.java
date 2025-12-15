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
 * Shows item's attack damage and speed values in lore
 */
@NullMarked
public class ToolTooltipModifier extends ItemModifier {

	public static final Component DAMAGE_ICON = ResourceLib.requireFontData(miscMeKey("attack_damage")).icon();
	public static final Component SPEED_ICON = ResourceLib.requireFontData(miscMeKey("attack_speed")).icon();

	public ToolTooltipModifier(NamespacedKey modifierId) {
		super(modifierId);
	}

	@Override
	public ModificationResult modify(ItemContextBox contextBox) {
		if (!contextBox.getContext().getContextType().hasVisibleLore()) return ModificationResult.PASS;

		Player player = contextBox.getViewer();
		if (player == null) return ModificationResult.PASS;

		ItemStack item = contextBox.getItem();
		if (item.isDataOverridden(DataComponentTypes.ATTRIBUTE_MODIFIERS) && !item.hasData(DataComponentTypes.ATTRIBUTE_MODIFIERS)) return ModificationResult.PASS;
		if (contextBox.getInitialType() != item.getType()) item = item.withType(contextBox.getInitialType());

		double damage = ItemUtil.getAttributeValue(item, Attribute.ATTACK_DAMAGE, player);
		double speed = ItemUtil.getAttributeValue(item, Attribute.ATTACK_SPEED, player);

		if (!item.hasData(DataComponentTypes.TOOL) && !item.hasData(DataComponentTypes.WEAPON)) {
			if (!contextBox.getItem().isDataOverridden(DataComponentTypes.ATTRIBUTE_MODIFIERS)) return ModificationResult.PASS;

			if (damage == 1 && speed == 4) {
				contextBox.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);
				return ModificationResult.PASS;
			}
		}

		Component text = combined(
			combined(SPEED_ICON, Component.space(), Component.text(ChatUtil.formatDouble(speed))),
			Component.text("  "),
			combined(DAMAGE_ICON, Component.space(), Component.text(ChatUtil.formatDouble(damage)))
		).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE);

		contextBox.addLore(text);

		contextBox.addHiddenComponents(DataComponentTypes.ATTRIBUTE_MODIFIERS);

		return ModificationResult.OK;
	}

}
